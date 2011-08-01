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


public class DXFLWPolyLine extends DXFObject
{
  private List<Vector3> verts = new ArrayList<Vector3>();
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFLWPolyLine( DXFReader parent )
  {        
    DXFPair pair = parent.readToCode( 90 );
    int vertexCount = pair.getInt();
    
    pair = parent.readToCode( 10 );
    for( int i=0; i<vertexCount; ++i )
    {
      Vector3 vert = new Vector3();        
      
      if( i != 0 ) pair.next();
      
                   vert.x = pair.getFloat();  
      pair.next(); vert.y = pair.getFloat();  //pair.next();     
      verts.add( vert );
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder builder ) {
    builder.beginPoly();
    for( Vector3 v : verts )
      builder.vertex( v.x, v.y, v.z );
    builder.endPoly();
  }
}