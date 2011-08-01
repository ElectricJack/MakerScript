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
package makerscript.geom.mesh;

import makerscript.util.Convertible;
import makerscript.geom.mesh.MeshBuilder;
import java.util.ArrayList;
import java.util.List;


public class MeshBuilderStore implements MeshBuilder, Convertible {
  
  class Command {
    int     type;
    float[] params      = null;
    String  stringParam = null;
    
    Command( int type )                      { this.type = type; }
    Command( int type, float[] params      ) { this.type = type; this.params      = params;      }
    Command( int type, String  stringParam ) { this.type = type; this.stringParam = stringParam; }
    
    
    float x  () { return params[0]; }
    float y  () { return params[1]; }
    float z  () { return params[2]; }
    float x2 () { return params[3]; }
    float y2 () { return params[4]; }
    float z2 () { return params[5]; }
    float w  () { return params[2]; }
    float h  () { return params[3]; }
    float a1 () { return params[4]; }
    float a2 () { return params[5]; }
  }
  
  List<Command> process = new ArrayList<Command>();
  String        name    = "";
  
  public      MeshBuilderStore( String name ) { this.name = name; }

  public void pushMatrix  ( )                                                             { process.add( new Command( PUSH_MATRIX) ); }
  public void popMatrix   ( )                                                             { process.add( new Command( POP_MATRIX) ); }
    
  public void applyMatrix ( float[] matrix )                                              { process.add( new Command( APPLY_MATRIX, matrix) ); }
  public void translate   ( float x, float y, float z )                                   { process.add( new Command( TRANSLATE, new float[] {x,y,z}) ); }
  public void rotate      ( float a )                                                     { process.add( new Command( ROTATE, new float[] {a}) ); }
  public void scale       ( float x, float y, float z )                                   { process.add( new Command( SCALE, new float[] {x,y,z}) ); }
  
  public void ellipse     ( float x, float y, float w, float h )                          { process.add( new Command( ELLIPSE, new float[] {x,y,w,h}) ); }
  public void arc         ( float x, float y, float w, float h, float a1, float a2 )      { process.add( new Command( ARC, new float[] {x,y,w,h,a1,a2}) ); }
  public void line        ( float x, float y, float z, float x2, float y2, float z2 )     { process.add( new Command( LINE, new float[] {}) ); }

  public void beginObject ( String name )                                                 { process.add( new Command( BEGIN_OBJECT, name) ); }
  public void endObject   ( )                                                             { process.add( new Command( END_OBJECT) ); }
  public void instance    ( String name )                                                 { process.add( new Command( INSTANCE, name) ); }
  
  public void beginPoly   ( )                                                             { process.add( new Command( BEGIN_POLY) ); }
  public void endPoly     ( )                                                             { process.add( new Command( END_POLY) ); }
  
  public void vertex      ( float x, float y )                                            { process.add( new Command( VERTEX2, new float[] {x,y}) ); }
  public void vertex      ( float x, float y, float z )                                   { process.add( new Command( VERTEX3, new float[] {x,y,z}) ); }
  public void normal      ( float x, float y, float z )                                   { process.add( new Command( NORMAL,  new float[] {x,y,z}) ); }
  
  public void convert( MeshBuilder builder ) {
    for( Command c : process ) {
      if      ( c.type == PUSH_MATRIX   ) builder.pushMatrix  ( );
      else if ( c.type == POP_MATRIX    ) builder.popMatrix   ( );
      else if ( c.type == APPLY_MATRIX  ) builder.applyMatrix ( c.params );
      else if ( c.type == TRANSLATE     ) builder.translate   ( c.x(), c.y(), c.z() );
      else if ( c.type == ROTATE        ) builder.rotate      ( c.params[0] );
      else if ( c.type == SCALE         ) builder.scale       ( c.x(), c.y(), c.z() );
      else if ( c.type == ELLIPSE       ) builder.ellipse     ( c.x(), c.y(), c.w(), c.h() );
      else if ( c.type == ARC           ) builder.arc         ( c.x(), c.y(), c.w(), c.h(),  c.a1(), c.a2() );
      else if ( c.type == LINE          ) builder.line        ( c.x(), c.y(), c.z(),  c.x2(), c.y2(), c.z2() );
      else if ( c.type == BEGIN_OBJECT  ) builder.beginObject ( c.stringParam );
      else if ( c.type == END_OBJECT    ) builder.endObject   ( );
      else if ( c.type == INSTANCE      ) builder.instance    ( c.stringParam );
      else if ( c.type == BEGIN_POLY    ) builder.beginPoly   ( );
      else if ( c.type == END_POLY      ) builder.endPoly     ( );
      else if ( c.type == VERTEX2       ) builder.vertex      ( c.x(), c.y() );
      else if ( c.type == VERTEX3       ) builder.vertex      ( c.x(), c.y(), c.z() );
      else if ( c.type == NORMAL        ) builder.normal      ( c.x(), c.y(), c.z() );
    }
  }
  
  private static final int PUSH_MATRIX  =  1;
  private static final int POP_MATRIX   =  2;
  private static final int APPLY_MATRIX =  3;
  private static final int TRANSLATE    =  4;
  private static final int ROTATE       =  5;
  private static final int SCALE        =  6;
  private static final int ELLIPSE      =  7;
  private static final int ARC          =  8;
  private static final int LINE         =  9;
  private static final int BEGIN_OBJECT = 10;
  private static final int END_OBJECT   = 11;
  private static final int INSTANCE     = 12;
  private static final int BEGIN_POLY   = 13;
  private static final int END_POLY     = 14;
  private static final int VERTEX2      = 15;
  private static final int VERTEX3      = 16;
  private static final int NORMAL       = 17;
}