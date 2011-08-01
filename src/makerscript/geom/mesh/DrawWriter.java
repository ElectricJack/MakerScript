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
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class DrawWriter implements MeshBuilder {
  private PGraphics                     drawer;
  private List<MeshBuilderStore>        objects         = new ArrayList<MeshBuilderStore>();
  private Map<String,MeshBuilderStore>  objectsByName   = new HashMap<String,MeshBuilderStore>();
  private MeshBuilderStore              activeObject    = null;
  
  
  public       DrawWriter  ( PGraphics g )                                                 { this.drawer = g; }
  
  public void  pushMatrix  ( )                                                             { drawer.pushMatrix(); }
  public void  popMatrix   ( )                                                             { drawer.popMatrix();  }
  public void  translate   ( float x, float y, float z )                                   { drawer.translate(x,y,z);  }
  
  public void  ellipse     ( float x, float y, float d1, float d2 )                        { drawer.ellipse(x,y,d1,d2);   }
  public void  arc         ( float x, float y, float d1, float d2, float a1, float a2 )    { drawer.arc(x,y,d1,d2,a1,a2); }
  public void  line        ( float x, float y, float z,  float x2, float y2, float z2 )    { drawer.line(x,y,z,x2,y2,z2); }
  public void  rotate      ( float a )                                                     { drawer.rotate(a); }
  public void  scale       ( float x, float y, float z )                                   { drawer.scale(x,y,z); }
  
  public void  beginPoly   ( )                                                             { drawer.beginShape();   }
  public void  endPoly     ( )                                                             { drawer.endShape(PGraphics.CLOSE); }
  
  public void  vertex      ( float x, float y )                                            { drawer.vertex(x,y); }
  public void  vertex      ( float x, float y, float z )                                   { drawer.vertex(x,y,z); }
  public void  normal      ( float x, float y, float z )                                   { drawer.normal(x,y,z); }
  
  public void  beginObject ( String name )                                                 { activeObject = new MeshBuilderStore( name ); }
  public void  endObject   ( )                                                             { if( activeObject == null ) return; objects.add( activeObject ); objectsByName.put( activeObject.name, activeObject ); activeObject = null; }
  public void  instance    ( String name )                                                 { MeshBuilderStore mbs = objectsByName.get(name); if( mbs != null ) write(mbs); }
  public void  write       ( Convertible out )                                             { out.convert( this ); }
  
  public void  applyMatrix ( float[] matrix ) {
    if( matrix.length < 16 ) return;
    drawer.applyMatrix(  matrix[0],  matrix[1],  matrix[2],  matrix[3]
                      ,  matrix[4],  matrix[5],  matrix[6],  matrix[7]
                      ,  matrix[8],  matrix[9], matrix[10], matrix[11]
                      , matrix[12], matrix[13], matrix[14], matrix[15] );
  }
}
