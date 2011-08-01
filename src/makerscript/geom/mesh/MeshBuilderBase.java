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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import makerscript.geom.Vector3;
import makerscript.math.Mathematical;
import makerscript.util.Convertible;
import processing.core.PGraphics;
import processing.core.PApplet;



public class MeshBuilderBase extends Mathematical implements MeshBuilder {

  protected   PolyLine             working = null;
  protected   Mesh                 mesh;
  protected   Vector3              curNormal;
  protected   int                  integrationSteps = 25;
  protected   float                resolution       = 2.0f;
  protected   PGraphics            g; 
  protected   PApplet              app;
  
  private List<MeshBuilderStore>        objects         = new ArrayList<MeshBuilderStore>();
  private Map<String,MeshBuilderStore>  objectsByName   = new HashMap<String,MeshBuilderStore>();
  private MeshBuilderStore              activeObject    = null;
  
  public                           MeshBuilderBase( PApplet app )
  {
    this.app  = app;
    this.g    = app.g;
    mesh      = new Mesh();
    curNormal = new Vector3();
  }
  
  public Mesh  getMesh     ( ) { return mesh; }
  public void  pushMatrix  ( )                            { g.pushMatrix();     }
  public void  popMatrix   ( )                            { g.popMatrix();      }
  public void  translate   ( float x, float y, float z )  { g.translate(x,y,z); }
  public void  rotate      ( float a )                    { g.rotate(a);        }
  public void  scale       ( float x, float y, float z )  { g.scale(x,y,z);     }
  public void  beginPoly   ( )                            { working = new PolyLine(); }
  public void  endPoly     ( )                            { if( working != null ) mesh.add( working ); }
  
  //@TODO: Handle object instances in the future...
  public void  beginObject ( String name )                { activeObject = new MeshBuilderStore( name ); }
  public void  endObject   ( )                            { if( activeObject == null ) return; objects.add( activeObject ); objectsByName.put( activeObject.name, activeObject ); activeObject = null; }
  public void  instance    ( String name )                { MeshBuilderStore mbs = objectsByName.get(name); if( mbs != null ) write(mbs); }
  public void  write       ( Convertible out )            { out.convert( this ); }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void applyMatrix( float[] matrix ) {
    if( matrix.length < 16 ) return;
    g.applyMatrix(  matrix[0],  matrix[1],  matrix[2],  matrix[3]
                 ,  matrix[4],  matrix[5],  matrix[6],  matrix[7]
                 ,  matrix[8],  matrix[9], matrix[10], matrix[11]
                 , matrix[12], matrix[13], matrix[14], matrix[15] );
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public void vertex( float x, float y ) {
    if( working != null ) {
      Vertex v = new Vertex( g.modelX(x,y,0), g.modelY(x,y,0), g.modelZ(x,y,0) );
      v.n.x = 0; v.n.y = 0; v.n.z = 1;
      working.add( v );
    }
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public void vertex( float x, float y, float z ) {
    if( working != null ) {
      Vertex v = new Vertex( g.modelX(x,y,z), g.modelY(x,y,z), g.modelZ(x,y,z) );
      v.n.x = curNormal.x;
      v.n.y = curNormal.y;
      v.n.z = curNormal.z;
      working.add( v );
    }
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public void normal( float x, float y, float z ) {
    curNormal.set(x,y,z);
  }
 
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void ellipse( float x, float y, float d1, float d2 )
  {
    Vector3 pos      = new Vector3();
    float   length   = arcLengthAprox(x,y,d1,d2,0,2*PI);
    int     segments = (int)(length * resolution);
    
    beginPoly();
      for( int i=0; i<segments; ++i )
      {
        float a = map( i, 0, segments-1, 0, 2*PI );
        pos.set( x + cos(a) * d1 * 0.5f
               , y + sin(a) * d2 * 0.5f, 0.f );

        vertex( g.modelX( pos.x, pos.y, pos.z )
              , g.modelY( pos.x, pos.y, pos.z )
              , g.modelZ( pos.x, pos.y, pos.z ) );
      }
    endPoly();
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public void arc( float x, float y, float d1, float d2, float a1, float a2 )
  {
    Vector3 pos      = new Vector3();
    float   length   = arcLengthAprox(x,y,d1,d2,a1,a2);
    int     segments = (int)(length * resolution);
    
    beginPoly();
      for( int i=0; i<segments; ++i ) {
        float a = map( i, 0, segments-1, a1, a2 );
        pos.set( x + cos(a) * d1 * 0.5f
               , y + sin(a) * d2 * 0.5f, 0.f );
        
        vertex( g.modelX( pos.x, pos.y, pos.z )
              , g.modelY( pos.x, pos.y, pos.z )
              , g.modelZ( pos.x, pos.y, pos.z ) );
      }
    endPoly();
  }
  // ------------------------------------------------------------------------------------------------------------- //  
  public void line( float x, float y, float z, float x2, float y2, float z2 )
  {
    beginPoly();
      vertex( g.modelX(x,y,z),    g.modelY(x,y,z),    g.modelZ(x,y,z)    );
      vertex( g.modelX(x2,y2,z2), g.modelY(x2,y2,z2), g.modelZ(x2,y2,z2) );
    endPoly();
  }
  // ------------------------------------------------------------------------------------------------------------- //  
  private float arcLengthAprox( float x, float y, float d1, float d2, float a1, float a2 ) {
    
    float arcLength = 0;
    float px = cos(a1) * d1 * 0.5f;
    float py = sin(a1) * d2 * 0.5f;
    
    for( int i=1; i<integrationSteps; ++i ) {
      float angle = map( i, 0, 99, a1, a2 );
      float nx = cos(angle) * d1 * 0.5f;
      float ny = sin(angle) * d2 * 0.5f;
      float dx = nx - px;
      float dy = ny - py;
            px = nx;
            py = ny;
      arcLength += (float)Math.sqrt( dx*dx + dy*dy );
    }
    
    return arcLength;
  }

}
