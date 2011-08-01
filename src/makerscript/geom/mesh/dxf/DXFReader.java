/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */
package makerscript.geom.mesh.dxf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import makerscript.geom.mesh.MeshBuilder;
import makerscript.util.Convertible;


public class DXFReader implements Convertible
{
  public static final int CODE_SECTION  = 2;
  public static final int CODE_TYPE     = 0;
  
  private int              where      = 0;
  private String[]         file       = null;
  private List<Integer>    whereStack = new ArrayList<Integer>();
  private List<DXFObject>  objects    = new ArrayList<DXFObject>();
  private List<DXFBlock>   blocks     = new ArrayList<DXFBlock>();
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFReader( String filePath ) {
    BufferedReader reader = createReader( filePath );
    
    // Load the entire file
    List<String> lines = new ArrayList<String>();
    try {
      
      String line = reader.readLine();
      while( line != null ) {
        lines.add( line.trim() );
        line = reader.readLine();
      }
      reader.close();
      
    } catch (Exception e) { e.printStackTrace(); }
    
    // Create the string array, and copy all the lines there
    System.out.println( lines.size() );
    file = new String[ lines.size() ];
    for( String line : lines )
      file[where++] = line;
    where = 0;
    
    // Process the file
    process();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder builder ) {
    for( DXFBlock block : blocks ) {
      builder.beginObject( block.name );
        block.convert( builder );
      builder.endObject();
    }
    
    for( DXFObject object : objects ) {
      object.convert( builder );
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  protected void process() {
    pushWhere();
      boolean blocksFound   = readToSectionType( "BLOCKS" );
      if    ( blocksFound   ) readBlocks();
    popWhere();
    pushWhere();
      boolean entitiesFound = readToSectionType( "ENTITIES" );
      if    ( entitiesFound ) readEntities();
    popWhere();
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected boolean readEntities() {
    while(true) {
      DXFPair entityType = readToCode( 0 );
      if( entityType == null ) return false;
      else {
        if ( entityType.S.equals("ENDSEC") ) break;
        else handleAddEntity( objects, entityType );
      }
    }
    return true;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected boolean readBlocks() {
    DXFBlock curBlock = null;
    while(true) {
      DXFPair   entityType = readToCode( 0 );
      if      ( entityType == null ) return false;
      else if ( entityType.S.equals("BLOCK")  ) curBlock = new DXFBlock( this );
      else if ( entityType.S.equals("ENDBLK") )
      {
        if( curBlock.objects.size() > 0 )
          blocks.add( curBlock );
  
        curBlock = null;
      }
      else if ( entityType.S.equals("ENDSEC") ) break;
      else if ( curBlock != null )              handleAddEntity( curBlock.objects, entityType );
    }
    return true;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected void convertEntity( MeshBuilder d, String name ) {
    for( DXFBlock b : blocks ) {
      if( b.name.equals(name) ) {
        b.convert( d );
        return;
      }
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  private void handleAddEntity( List<DXFObject> objs, DXFPair entityType ) {
    if      ( entityType.S.equals("LINE")       ) objs.add( new DXFLine       ( this ) );
    else if ( entityType.S.equals("POLYLINE")   ) objs.add( new DXFPolyLine   ( this ) );
    else if ( entityType.S.equals("LWPOLYLINE") ) objs.add( new DXFLWPolyLine ( this ) );
    else if ( entityType.S.equals("CIRCLE")     ) objs.add( new DXFCircle     ( this ) );
    else if ( entityType.S.equals("ARC")        ) objs.add( new DXFArc        ( this ) );
    else if ( entityType.S.equals("SPLINE")     ) objs.add( new DXFSpline     ( this ) );  
    else if ( entityType.S.equals("INSERT")     ) objs.add( new DXFInsert     ( this ) );  
    else {
      System.out.println( "Unknown entity type: " + entityType.S );
    }
  }

  // ------------------------------------------------------------------------------------------------------------- //
  protected boolean readToSection() { return readToType("SECTION"); }
  protected boolean readToType( String type ) {
    DXFPair pair = new DXFPair( this );
    while( pairIsValid(pair) && !pair.is( CODE_TYPE, type ) )
      pair.next();
    return pairIsValid(pair);
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected DXFPair readToCode( int code ) {
    DXFPair pair = new DXFPair( this );
    while( pairIsValid(pair) && pair.G != code  )
      pair.next();
    return pairIsValid(pair)? pair : null;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected boolean readToSectionType( String type ) {
    DXFPair pair = new DXFPair( this );
    while( pairIsValid(pair) && !pair.is( CODE_SECTION, type ) ) {
      if( !pair.is( CODE_TYPE, "SECTION" ) ) 
        if( !readToSection() ) return false;
      pair.next();
    }
    return pairIsValid(pair);
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected boolean pairIsValid( DXFPair pair ) {
    return pair != null && pair.S != null;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public int readNextInt() {
    try                  { return Integer.parseInt( readNextString() ); }
    catch( Exception e ) { return 0; }
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public float readNextFloat( String name ) {
    try                  { return Float.parseFloat( readNextString() ); }
    catch( Exception e ) { return 0.f; }
  }

  // ------------------------------------------------------------------------------------------------------------- //
  public String readNextString() {
    if( where < file.length ) return file[where++];
    return null;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public void pushWhere() {
    whereStack.add( where );
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public void popWhere() {
    where = whereStack.remove( whereStack.size() - 1 );
  }
  // ------------------------------------------------------------------------------------------------------------- //
  private BufferedReader createReader( String filename ) {
    try {
      InputStream is = null;
      try {
        is = new FileInputStream(filename);
      } catch( FileNotFoundException e ) {
        System.err.println(filename + " does not exist or could not be read");
        return null;
      }

      return new BufferedReader( new InputStreamReader(is) );
      
    } catch (Exception e) {
      if (filename == null) System.err.println( "Filename passed to reader() was null"     );
      else                  System.err.println( "Couldn't create a reader for " + filename );
    }
    return null;
  }
}
