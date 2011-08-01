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
package makerscript.geom;

import processing.core.PGraphics;

public class Line2
{
  // No frontin' ( public data )
  public float  A, B, C;
  public Vector2 v0 = new Vector2();
  
  public Line2() {}
  public Line2( float x0, float y0, float x1, float y1 ) { set( x0, y0, x1, y1 ); }
  public Line2( Vector2 v0, Vector2 v1 )                 { set( v0, v1 ); }
  
  // --------------------------------------------------------- //
  public Line2 set( Vector2 v0, Vector2 v1 )                 { return this.set( v0.x, v0.y, v1.x, v1.y ); }
  public Line2 set( float x0, float y0, float x1, float y1 ) {
    v0.set(x0,y0);
  
    A = y0 - y1;
    B = x1 - x0;
    C = -A*x0 - B*y0;
    
    return this;
  }
  
  // --------------------------------------------------------- //
  public float A() { return A; }
  public float B() { return B; }
  public float C() { return C; }

  // --------------------------------------------------------- //
  public Vector2 getTangent() { return (new Vector2(-B,A)).nrmeq(); }
//--------------------------------------------------------- //
  public Vector2 getNormal()  { return (new Vector2(A,B)).nrmeq(); }
  
  // --------------------------------------------------------- //
  public Vector2 project( Vector2 point ) {
    //Ax + By + C = 0
    //px = -B/A y - C/A
    //py = -A/B x - C/B
    Vector2 AB = getTangent();
    float   prj = AB.dot( point.sub( v0 ) );
    return  v0.add( AB.muleq( prj ) );
  }
  
  // --------------------------------------------------------- //
  public boolean inside( Vector2 v ) { return v == null ? null : inside( v.x, v.y ); }
  public boolean inside( float x, float y ) {
    return A*x + B*y + C <= 0;
  }
  
  // --------------------------------------------------------- //
  public boolean colinear( Vector2 v ) { return colinear( v.x, v.y ); }
  public boolean colinear( float x, float y ) {
    return Math.abs( (float)( A*x + B*y + C ) ) < 0.001f;
  }
  
  // --------------------------------------------------------- //
  public Vector2 intersect( Line2 other )              { return intersect( new Vector2(), other ); }
  public Vector2 intersect( Vector2 out, Line2 other ) {
    if( out == null || other == null ) return null;

    Vector2 t1 = getTangent();
    Vector2 t2 = other.getTangent();
    
    // http://en.wikipedia.org/wiki/Line-line_intersection
    float x1 = v0.x,          		y1 = v0.y;
    float x2 = v0.x + t1.x,   		y2 = v0.y + t1.y;
    float x3 = other.v0.x, 	  		y3 = other.v0.y;
    float x4 = other.v0.x + t2.x, 	y4 = other.v0.y + t2.y;
    
    float d = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
    if( Math.abs(d) < 0.001f ) {
      return null;
    }
    
    float s1 = (x1 * y2 - y1 * x2);
    float s2 = (x3 * y4 - y3 * x4);
    float nx = s1*(x3 - x4) - (x1 - x2)*s2;
    float ny = s1*(y3 - y4) - (y1 - y2)*s2;

    out.set( nx / d, ny / d );
    out = project( out );
    
    // Return the result
    return out;
  }
  
  // --------------------------------------------------------- //
  public void draw( PGraphics g )  { draw( new OBounds2( g.width, g.height ), g ); }
  public void draw( OBounds2 bounds, PGraphics g ) {
	LineSeg2 result = bounds.clip( new LineSeg2(), this );
    if( result != null ) result.draw( g );
  }
}