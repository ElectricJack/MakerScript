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
package makerscript.commands;

import java.util.Queue;
import java.util.ArrayList;

import makerscript.Layer;
import makerscript.ScriptableMillState;

import makerscript.geom.mesh.MeshBuilderBase;
import makerscript.geom.mesh.collada.COLLADAReader;
import makerscript.geom.mesh.dxf.DXFReader;
import makerscript.geom.mesh.Mesh;

import makerscript.geom.Vector2;
import makerscript.geom.Vector3;
import makerscript.geom.Poly2;
import makerscript.geom.mesh.PolyLine;

import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;

//import makerscript.util.Serializable;
//import makerscript.util.Serializer;
//import makerscript.util.XMLSerializer;

public class CmdLoad extends Command {
  //---------------------------------------------------------------------------------
  public         CmdLoad ( CommandStore cs ) { super( cs, "load +string" ); }
  public         CmdLoad ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdLoad(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
    
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    String              fileName  = popString(params); 

    if      ( fileExtensionIs( fileName, "txt" ) ) loadPaths   ( userState.projectPath + "/" + fileName, userState );
    else if ( fileExtensionIs( fileName, "dxf" ) ) loadDXF     ( userState.projectPath + "/" + fileName, userState );
    else if ( fileExtensionIs( fileName, "dae" ) ) loadCOLLADA ( userState.projectPath + "/" + fileName, userState );
    else if ( fileExtensionIs( fileName, "stl" ) ) loadSTL     ( userState.projectPath + "/" + fileName, userState );
    else if ( fileExtensionIs( fileName, "xml" ) ) loadXML     ( userState.projectPath + "/" + fileName, userState );
    
    return state.nextCommand();
  }
  
  // ------------------------------------------------------------------------ //
  private boolean fileExtensionIs( String fileName, String ext ) {
    return fileName.contains("." + ext);
  }
  
  // ------------------------------------------------------------------------ //
  public void loadPaths( String fileName, ScriptableMillState userState ) {
    //userState.load(fileName);
  }

  // ------------------------------------------------------------------------ //
  public void loadDXF( String fileName, ScriptableMillState userState )
  {
    MeshBuilderBase builder = new MeshBuilderBase( userState.app );
    DXFReader dxf = new DXFReader( fileName );
              dxf.convert( builder );
    

    makeActiveLayer( userState );
    userState.activeLayer.add( builder.getMesh() );
  }
  
  // ------------------------------------------------------------------------ //
  public void loadCOLLADA( String fileName, ScriptableMillState userState )
  {
    MeshBuilderBase builder = new MeshBuilderBase( userState.app );
    COLLADAReader   collada = new COLLADAReader(userState.app, fileName);
                    collada.convert(builder);

    makeActiveLayer( userState );
    userState.activeLayer.add( builder.getMesh() );
  }
  
  // ------------------------------------------------------------------------ //
  public void loadSTL( String fileName, ScriptableMillState userState )
  {
  }
  
  // ------------------------------------------------------------------------ //
  public void loadXML( String fileName, ScriptableMillState userState ) 
  {/*
    class XMLTarget extends Vector2 implements Serializable {
      public String    getType ( ) { return "Target"; }
      public XMLTarget clone   ( ) { return new XMLTarget( this ); }
      // ------------------------------------------------------------ //
      public XMLTarget( ) {}
      public XMLTarget( XMLTarget other )    { super( (Vector2)other ); }
      // ------------------------------------------------------------ //
      public void serialize( Serializer s ) {
        s.serialize( "position", (Vector2)this );
      }
    }

    class XMLPolyLine extends Poly2 implements Serializable {
      public XMLPolyLine()                 { super(); }
      public XMLPolyLine( Poly2    other ) { points = other.get().points; }
      // ------------------------------------------------------------ //
      public String      getType ( ) { return "PolyLine"; }
      public XMLPolyLine clone   ( ) { return new XMLPolyLine( this ); }
      // ------------------------------------------------------------ //
      public void  serialize ( Serializer s ) {
        int count = points.size();
            count = s.serialize( "count", count );
        
        if( s.isLoading() )
          points.clear();
        
        for( int i=0; i<count; ++i ) {
          String name = "p_" + i;
          if( s.isLoading() ) {
            Vector2             point = new Vector2();
            s.serialize ( name, point );
            add         ( point.x, point.y );
          } else {
            s.serialize( name, points.get(i) );
          }
        }
      }
    }

    class XMLGeom implements Serializable {
      ArrayList<XMLPolyLine> polys   = new ArrayList<XMLPolyLine>();
      ArrayList<XMLTarget>   targets = new ArrayList<XMLTarget>();
      // ------------------------------------------------------------ //
      public String           getType   ( ) { return "MakerScriptGeom"; }
      public XMLGeom          clone     ( ) { return new XMLGeom( this ); }
      public XMLGeom( )             { }
      public XMLGeom( XMLGeom other ) {
        for( XMLPolyLine poly   : other.polys   ) polys.add   ( poly.clone()   );
        for( XMLTarget   target : other.targets ) targets.add ( target.clone() );
      }
      // ------------------------------------------------------------ //
      public void   serialize ( Serializer s ) {
        s.serialize( "polys",   polys );
        s.serialize( "targets", targets );
      }
    }

    XMLSerializer  store = new XMLSerializer( userState.app );
    XMLGeom        geom  = new XMLGeom();
    
    store.registerType( new XMLGeom()     );
    store.registerType( new XMLPolyLine() );
    store.registerType( new XMLTarget()   );
    store.load        ( fileName, geom    );
    
    if( userState.activeLayer == null ) {
        userState.activeLayer = new Layer();
        userState.layers.add(userState.activeLayer);
    }

    Mesh mesh = new Mesh();
    for( XMLPolyLine poly : geom.polys ) {
      PolyLine newPoly = new PolyLine();
      
      for( Vector2 vertex : poly.points )
        newPoly.add( new Vector3( vertex.x, vertex.y, 0 ) );
      
      mesh.add( newPoly );
    }
    userState.activeLayer.add( mesh );
      
    for( XMLTarget target : geom.targets )
      userState.activeLayer.add( new Vector3( target.x, target.y, 0 ) );
    
    */
  }


  protected void makeActiveLayer( ScriptableMillState userState ) {
    if( userState.activeLayer == null ) {
        userState.activeLayer = new Layer();
        userState.layers.add(userState.activeLayer);
    }
  }
}
