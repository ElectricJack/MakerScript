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
import java.util.List;

import makerscript.geom.Vector3;
import makerscript.geom.mesh.MeshBuilder;

public class DXFPolyLine extends DXFObject
{
  private List<Vector3> verts = new ArrayList<Vector3>();
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFPolyLine( DXFReader parent )
  {        
    DXFPair pair = parent.readToCode(0);
    while( parent.pairIsValid( pair ) ) {
      String entityType = pair.S;
      if( entityType.equals("VERTEX") ) {
        
        // First we read the x and y values for this vertex
        float x, y;
        pair = parent.readToCode(10);
        x    = pair.getFloat();
        pair = parent.readToCode(20);
        y    = pair.getFloat();
        
        // Then we add it to the list!
        verts.add( new Vector3(x, y, 0) );
        
      } else if( entityType.equals("SEQEND") ) break;
      pair = parent.readToCode(0);
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder d )
  {
    for( int i=0; i<verts.size()-1; ++i ) {
      Vector3 v1 = verts.get(i);
      Vector3 v2 = verts.get(i+1);
      d.line( v1.x, v1.y, v1.z, v2.x, v2.y, v2.z );
    }
  }
}


