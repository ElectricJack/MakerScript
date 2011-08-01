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

import java.util.List;
import java.util.ArrayList;

import makerscript.geom.Vector3;
import makerscript.geom.mesh.MeshBuilder;


public class DXFSpline extends DXFObject {
  
  int            flags;  
  int            degree;
  int            knots;
  int            control_points;
  int            fit_points;

  List<Float>    knotValues     = new ArrayList<Float>();
  List<Vector3>  controlPoints  = new ArrayList<Vector3>();
  List<Vector3>  fitPoints      = new ArrayList<Vector3>();

  // ------------------------------------------------------------------------------------------------------------- //
  public DXFSpline( DXFReader parent )
  {
    DXFPair pair = parent.readToCode( 100 );
    if( pair.S.equals( "AcDbEntity" ) )
      pair = parent.readToCode( 100 );
    if( !pair.S.equals( "AcDbSpline" ) )
      return;

    // Load the spline information
    pair.next(); if ( pair.G == 70 ) { flags          = pair.getInt(); }
    pair.next(); if ( pair.G == 71 ) { degree         = pair.getInt(); }
    pair.next(); if ( pair.G == 72 ) { knots          = pair.getInt(); }
    pair.next(); if ( pair.G == 73 ) { control_points = pair.getInt(); }
    pair.next(); if ( pair.G == 74 ) { fit_points     = pair.getInt(); }
    
    // Load the knot values if they're available
    for( int i=0; i<knots; ++i )
    {
      pair.next();
      knotValues.add( pair.getFloat() );
    }
    
    // Load the control points if they're available
    for( int i=0; i<control_points; ++i )
    { 
      Vector3 p = new Vector3();
      pair.next(); p.x = pair.getFloat();
      pair.next(); p.y = pair.getFloat();
      pair.next(); p.z = pair.getFloat(); 
      controlPoints.add( p );
    }
    
    // Load the fit points if they're available
    for( int i=0; i<fit_points; ++i )
    { 
      Vector3 fp = new Vector3();
      pair.next(); fp.x = pair.getFloat(); 
      pair.next(); fp.y = pair.getFloat();
      pair.next(); fp.z = pair.getFloat(); 
      fitPoints.add( fp );
    }
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder d )
  {
    if( controlPoints.size() < 4 ) return;
    
    Vector3 pa = controlPoints.get(0);
    Vector3 pb = controlPoints.get(1);
    Vector3 pc = controlPoints.get(2);
    Vector3 pd = controlPoints.get(3);
    
    float lengthEstimate       = estimateBezierLength( pa, pb, pc, pd );
    float segmentsPerMilimeter = 0.75f;
    int   segments             = (int)(lengthEstimate * segmentsPerMilimeter);
    
    if( segments < 3 ) segments = 3;

    //d.pushMatrix();
      for( int i=0; i<segments; ++i ) {
        float t0 = map( i,   0, segments, 0, 1 );
        float t1 = map( i+1, 0, segments, 0, 1 );
        float x0 = bezierPoint( pa.x, pb.x, pc.x, pd.x, t0 );
        float y0 = bezierPoint( pa.y, pb.y, pc.y, pd.y, t0 );
        float z0 = bezierPoint( pa.z, pb.z, pc.z, pd.z, t0 );
        float x1 = bezierPoint( pa.x, pb.x, pc.x, pd.x, t1 );
        float y1 = bezierPoint( pa.y, pb.y, pc.y, pd.y, t1 );
        float z1 = bezierPoint( pa.z, pb.z, pc.z, pd.z, t1 );
        d.line( x0,y0,z0, x1,y1,z1 );
      }
    //d.popMatrix();
  }
  
}
