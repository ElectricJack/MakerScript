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

public class DXFLine extends DXFObject
{
  private Vector3 v1 = new Vector3();
  private Vector3 v2 = new Vector3();
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFLine( DXFReader parent ) {    
    DXFPair pair = parent.readToCode( 10 );
    v1.x = pair.getFloat();  pair.next();
    v1.y = pair.getFloat();  pair.next();
    v1.z = pair.getFloat();
                             pair.next();
    v2.x = pair.getFloat();  pair.next();
    v2.y = pair.getFloat();  pair.next();
    v2.z = pair.getFloat();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder d ) {
    d.line( v1.x, v1.y, v1.z, v2.x, v2.y, v2.z );
  }
}
