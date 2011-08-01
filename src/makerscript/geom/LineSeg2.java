/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
 */
package makerscript.geom;

import processing.core.PGraphics;

public class LineSeg2 extends Line2
{
  protected Vector2 v1 = new Vector2();
  
  // --------------------------------------------------------- //
  public LineSeg2() {}
  public LineSeg2( Vector2 v0, Vector2 v1 )
  {
    super( v0, v1 );
    this.v1.set( v1 );
  }
  // --------------------------------------------------------- //  
  public LineSeg2 flip() {
    Vector2 v3 = v0;
            v0 = v1;
            v1 = v3;
    return this;
  }
  // --------------------------------------------------------- //
  public LineSeg2 set( Vector2 v0, Vector2 v1 ) { 
	super.set( v0, v1 );
	if( this.v1 != null )
	    this.v1.set( v1 );
	return this;
  }
  // --------------------------------------------------------- //
  public LineSeg2 set( int index,  Vector2 v  ) {
    super.set( index == 0 ? v : v0
             , index == 1 ? v : v1 );
    return this;
  }  
  
  // --------------------------------------------------------- //
  public Vector2 getPoint( int index ) {
    return index == 0 ? v0.get() : v1.get();
  }
  
  // --------------------------------------------------------- //
  public boolean contains( Vector2 point ) {
    float t = getTime( point );
    return ( t >= 0.f && t <= 1.f );
  }
  
  // --------------------------------------------------------- //
  public float getTime( Vector2 point ) {
    float dy = v1.y - v0.y;
    float dx = v1.x - v0.x;    
    if( Math.abs( dx ) > Math.abs( dy ) ) {
      return (point.x - v0.x) / dx;
    } else {
      return (point.y - v0.y) / dy;
    }
  }

  // --------------------------------------------------------- //
  public Vector2 intersect( LineSeg2 segment )              { return intersect( new Vector2(), segment ); }
  public Vector2 intersect( Vector2 out, LineSeg2 segment ) {
    if( out == null || segment == null ) return null;

    // http://en.wikipedia.org/wiki/Line-line_intersection
    float x1 = v0.x,         y1 = v0.y;
    float x2 = v1.x,         y2 = v1.y;
    float x3 = segment.v0.x, y3 = segment.v0.y;
    float x4 = segment.v1.x, y4 = segment.v1.y;
    
    float d = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
    if( Math.abs(d) < 0.001f ) {
      return null;
    }
    
    float s1 = (x1 * y2 - y1 * x2);
    float s2 = (x3 * y4 - y3 * x4);
    float nx = s1*(x3 - x4) - (x1 - x2)*s2;
    float ny = s1*(y3 - y4) - (y1 - y2)*s2;
    
    out.set( nx / d, ny / d );
    //out = project( out );
    
    // If the result is contained in the first line segment
    //  and the second line segment
    if( contains(out) &&  segment.contains(out) ) {
      // Then we have a winner!
      return out;
    }

    // Boo hoo.. no intersect
    return null;
  }
  
  // --------------------------------------------------------- //
  public Vector2 project( Vector2 point ) {
    return super.project( point );
  }
  
  public void draw( PGraphics g ) {
    g.line( v0.x, v0.y, v1.x, v1.y );
  }
}
