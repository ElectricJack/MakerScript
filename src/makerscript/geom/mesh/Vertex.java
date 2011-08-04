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

import makerscript.geom.Vector3;
import makerscript.util.NamedMultiMap;
import makerscript.util.Selectable;


public class Vertex extends Vector3 implements Selectable {
  //public float x,  y,  z;
  //public float nx, ny, nz;
  public  Normal n    = null;
  
  private int    index = 0;
  
  public  String                  getType   ( ) { return "vertex"; }
  public  int                     getIndex  ( ) { return index; }
  
  // ----------------------------------------------------------------- //
  public Vertex( float x, float y, float z ) { super(x,y,z); n = new Normal(0.f, 0.f, 1.f); }
  public Vertex( Vector3 v )                 { super(v);     n = new Normal(0.f, 0.f, 1.f); }
  public Vertex( )                           { n = new Normal(0.f, 0.f, 1.f); }
  // ----------------------------------------------------------------- //  
  public ArrayList<Vertex> getVerts() {
    ArrayList<Vertex> verts = new ArrayList<Vertex>();
                      verts.add(this);
    return            verts;
  }
  // ----------------------------------------------------------------- //
  public ArrayList<Edge>         getEdges  ( ) { return null; }
  public NamedMultiMap<PolyLine> getPolys  ( ) { return null; }
  public NamedMultiMap<Face>     getFaces  ( ) { return null; }
  
  // ----------------------------------------------------------------- //
  public Vector3 getMin(Vector3 min) {
    min.set( ( x < min.x ? x : min.x ),
             ( y < min.y ? y : min.y ),
             ( z < min.z ? z : min.z ) );
    return min;
  }
  // ----------------------------------------------------------------- //
  public Vector3 getMax(Vector3 max) {
    max.set( ( x > max.x ? x : max.x ),
             ( y > max.y ? y : max.y ),
             ( z > max.z ? z : max.z ) );
    return max;
  }
}
