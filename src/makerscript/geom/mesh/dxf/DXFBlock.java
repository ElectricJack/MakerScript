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

import java.util.ArrayList;

import makerscript.geom.mesh.MeshBuilder;

public class DXFBlock extends DXFObject {
  
  protected ArrayList<DXFObject> objects = new ArrayList<DXFObject>();
  protected String               name;
  protected int                  id;     // Used by convertable
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFBlock( DXFReader parent ) {
    DXFPair  pair = parent.readToCode( 2 );
    name = pair.S;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder builder ) {
    for( DXFObject object : objects ) {
      object.convert( builder );
    }
  }
}
