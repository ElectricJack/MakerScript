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

public class DXFInsert extends DXFObject {
  
  private Vector3     pos       = new Vector3();        // Location
  private Vector3     scale     = new Vector3(1,1,1);   // Scale
  private float       rotation  = 0.f;                  // Rotation angle;
  private String      name;
  private DXFReader   parent;
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFInsert( DXFReader parent ) {
    this.parent = parent;
    
    // Get the position of the insert
    DXFPair           pair = parent.readToCode( 2 );
    name = pair.S;    pair.next();
    
    pos.x = pair.getFloat();  pair.next();
    pos.y = pair.getFloat();  pair.next();
    pos.z = pair.getFloat();  pair.next();
    
    if( pair.G == 41 ) { scale.x  = pair.getFloat(); pair.next(); }
    if( pair.G == 42 ) { scale.y  = pair.getFloat(); pair.next(); }
    if( pair.G == 43 ) { scale.z  = pair.getFloat(); pair.next(); }
    if( pair.G == 50 ) { rotation = radians( pair.getFloat() ); pair.next(); }
  }  
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder builder ) {
    builder.pushMatrix();
      
      builder.translate ( pos.x, pos.y, pos.z );
      builder.rotate    ( rotation );
      builder.scale     ( scale.x, scale.y, scale.z );
      builder.instance  ( name );
      
    builder.popMatrix();
  }
}
