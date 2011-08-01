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

import makerscript.geom.Vector3;
import makerscript.geom.mesh.MeshBuilder;

public class DXFCircle extends DXFObject {
  
  protected Vector3     center = new Vector3();
  protected float       radius;
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFCircle( DXFReader parent ) {
    //this.parent = parent;
    DXFPair pair = parent.readToCode( 10 );
    center.x = pair.getFloat();  pair.next();
    center.y = pair.getFloat();  pair.next();
    center.z = pair.getFloat();  pair.next();
    radius   = pair.getFloat() * 2;    
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder builder ) {
    builder.pushMatrix();
      builder.translate ( center.x, center.y, center.z );
      builder.ellipse   ( 0, 0, radius, radius );
    builder.popMatrix();
  }
}
