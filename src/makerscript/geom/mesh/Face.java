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
import makerscript.util.Convertible;
import makerscript.util.Nameable;



public class Face extends PolyLine implements Convertible, Nameable {
  
  protected ArrayList< PolyLine > holes  = null;
  protected Vector3               normal = null;
  public    int                   index  = 0;
  
  public 	String    getType  ( )              { return "face"; }
  public 	int       getIndex ( )              { return index; }
  
  public void addHole( PolyLine hole ) {
    if( hole  == null ) return;
    if( holes == null ) holes = new ArrayList< PolyLine >();
    holes.add( hole );
  }
  
  @Override
  public void convert( MeshBuilder builder ) {
    builder.beginPoly();
    for( Vertex v : verts ) {
      builder.normal( v.n.x, v.n.y, v.n.z );
      builder.vertex( v.x,  v.y,  v.z  );
    }
    builder.endPoly();
  }
}
