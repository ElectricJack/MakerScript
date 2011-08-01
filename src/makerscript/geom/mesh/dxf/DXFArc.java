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

import makerscript.geom.mesh.MeshBuilder;

public class DXFArc extends DXFCircle {
  private float a0, a1;
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFArc( DXFReader parent ) {
    // Get the circle props
    super( parent );
    
    // Get the arc props
    DXFPair pair = parent.readToCode( 50 );
    a0 = radians( pair.getFloat() ); pair.next();
    a1 = radians( pair.getFloat() );

    if( a0 > a1 ) a0 -= 2*PI;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void build( MeshBuilder builder )
  {
    builder.pushMatrix();
      builder.translate ( center.x, center.y, center.z );
      builder.arc       ( 0, 0, radius, radius, a0, a1 );
    builder.popMatrix();
  }
}
