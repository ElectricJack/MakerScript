/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
 */
package makerscript.geom;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Comparator;

public class Poly2 {

  public class Point2 extends Vector2 {
    boolean  entry    = false;
    boolean  visited  = false;
    Point2   neighbor = null;
    Point2   next     = null;
    Point2   prev     = null;

    public   Point2  ( )                  { super(); }
    public   Point2  ( Vector2 other )    { super( other ); }
    public   Point2  ( float x, float y ) { super( x, y ); }
    public   Point2  ( Point2 other )      { super( other.x, other.y ); }
  }
  @SuppressWarnings("serial")
  public class PointList extends ArrayList<Point2> {}

  public PointList points = new PointList();

  // -------------------------------------------------------------------------------- //
  public void add( float x, float y ) { 
    Point2 p = new Point2(x, y);

    if ( points.isEmpty() ) {
      p.next = p;
      p.prev = p;
    } else {
      p.next      = points.get( 0 );
      p.next.prev = p;
      p.prev      = points.get( points.size() - 1 );
      p.prev.next = p;
    }

    points.add( p );
  }
  // -------------------------------------------------------------------------------- //
  public Poly2 get() {
    Poly2  clone = new Poly2();
    clone.points.addAll( points );
    return clone;
  }
  // -------------------------------------------------------------------------------- //
  public ArrayList<LineSeg2> getEdges() {
    ArrayList<LineSeg2> edges = new ArrayList<LineSeg2>();
    int                 count = points.size();

    for ( int i=0; i<count; ++i )
      edges.add( getEdge( i, count ) );

    return edges;
  }
  // -------------------------------------------------------------------------------- // 
  public  LineSeg2 getEdge( int index ) { 
    return getEdge( index, points.size() );
  }
  // -------------------------------------------------------------------------------- //
  private LineSeg2 getEdge( int index, int count ) {
    return new LineSeg2( points.get( index%count ), points.get( (index + 1)%count ) );
  }

  // -------------------------------------------------------------------------------- // 
  /*
   Union of two complex polygons:
   http://davis.wpi.edu/~matt/courses/clipping/
   */
  public void unioneq( Poly2 other ) {

    Poly2 us = this.get();
    ArrayList<Integer> thisCuts    = this.cuteq( other );       
    if ( thisCuts.isEmpty() ) return;
    ArrayList<Integer> otherCuts   = other.cuteq( us );
    
    for ( Point2 p : points       ) p.visited = false;
    for ( Point2 p : other.points ) p.visited = false;

    bindIntersections( points, thisCuts, other.points, otherCuts );
    markEntryExit( this.points, other, true );
    markEntryExit( other.points, this, true );

    PointList  newPoly    = new PointList();
    Point2      p          = points.get( thisCuts.get(0) );
    boolean    entry      = p.entry;

    while ( !p.visited ) {

      p.visited = true;
      newPoly.add( new Point2(p) );

      if ( p.neighbor != null ) { 
        p     = p.neighbor; 
        entry = p.entry;
      }

      if( !entry ) p = p.next;
      else         p = p.prev;
      
    }

    points = newPoly;
  }
  // -------------------------------------------------------------------------------- //
  public boolean contains( Vector2 point ) {

    // Create a "ray" at least until we have a real ray data structure
    Vector2  outside   = point.get();
    outside.x = 999999.0f;
    LineSeg2 ray       = new LineSeg2( point, outside );

    int                 intersects = 0;
    ArrayList<LineSeg2> edges      = getEdges();
    for ( LineSeg2 edge : edges ) {
      if ( edge.intersect( ray ) != null ) ++intersects;
    }

    return (intersects % 2) == 0;
  }
  // -------------------------------------------------------------------------------- //
  public boolean containsVertsFrom( Poly2 other, ArrayList<Integer> contained ) {

    int     pointIndex   = 0;
    boolean thisContains = false;
    for ( Point2 point : other.points ) {
      if ( this.contains( point ) ) {
        thisContains = true;
        contained.add( pointIndex );
      }
      ++pointIndex;
    }

    return thisContains;
  }
  // -------------------------------------------------------------------------------- //
  public int findFirstContainingEdge( Vector2 point ) {
    ArrayList<LineSeg2> edges = getEdges(); // This polygon's edges
    int                 index = 0;
    for ( LineSeg2 edge : edges ) {
      if ( edge.contains( point ) )
        return index;
      ++index;
    }
    return -1;
  }
  // -------------------------------------------------------------------------------- //
  public ArrayList<Integer> cuteq( Poly2 other ) {
    // Some local classes to make dealing with the generic types somewhat clearer
    @SuppressWarnings("serial")
	class EdgeIntersectionMap extends TreeMap<Integer, PointList> {
      public void add( int edgeIndex, Point2 intersection ) {
        if ( containsKey( edgeIndex ) ) {
          PointList     intersections = get( edgeIndex );
          intersections.add( intersection );
        } else {
          PointList     intersections = new PointList();
          intersections.add( intersection );
          put( edgeIndex, intersections );
        }
      }
    }

    class TimeComparator implements Comparator<Float> {
      public int compare( Float o1, Float o2 ) {
        if ( o1 < o2 ) return -1;
        if ( o1 > o2 ) return  1;
        return 0;
      }
    }

    List<LineSeg2>       edges         = getEdges();               // This polygon's edges
    List<LineSeg2>       otherEdges    = other.getEdges();         // The other polygon's edges
    EdgeIntersectionMap  intersections = new EdgeIntersectionMap();

    // Attempt to intersect each of our edges edge with every
    //  edge from the other polygon, and store the results into the hashmap
    //   assigning intersections points to the edge they lie on.
    int edgeIndex = 0;
    for ( LineSeg2 edge : edges ) {
      for ( LineSeg2 otherEdge : otherEdges ) {  
        Vector2 result = edge.intersect( otherEdge );
        if ( result != null ) {
          intersections.add( edgeIndex, new Point2(result) );
        }
      }
      ++edgeIndex;
    }

    // Create the storage for our new points
    PointList newPoints = new PointList();

    // Add the intersection points to the proper location inside the poly and
    //  update the intersection indices as required
    int                lastIndex        = 0;
    ArrayList<Integer> intersectIndices = new ArrayList<Integer>(); // The indices of our new intersection points
    for ( Entry<Integer,PointList> entry : intersections.entrySet() ) {

      int       index  = entry.getKey();
      PointList points = entry.getValue();
      LineSeg2  edge   = getEdge( index );

      // Calculate the time value for the point on the edge
      //  and insert them into the tree map so they will be sorted
      TreeMap<Float, Point2> timeSorted = new TreeMap<Float, Point2>( new TimeComparator() );
      for ( Point2 point : points )
        timeSorted.put( edge.getTime(point), point );

      // Add any existing points up to the edge index (leading point index)
      for ( int i = lastIndex; i <= index && i < this.points.size(); ++i )
        newPoints.add( this.points.get(i) );

      // Update the last index
      lastIndex = index + 1;

      // Add all of the intersections for this polygon edge, sorted
      for ( Point2 point : timeSorted.values() ) {
        newPoints.add( point );
        intersectIndices.add( newPoints.size()-1 );
      }
    }

    // Finally add any remaining points
    for ( int i=lastIndex; i < this.points.size(); ++i )
      newPoints.add( this.points.get(i) );

    // Link up the points
    int count = newPoints.size();
    for ( int i=0; i<count; ++i ) {
      Point2 p0 = newPoints.get(i);
      Point2 p1 = newPoints.get((i+1)%count);
      p0.next = p1;
      p1.prev = p0;
    }

    // Assign the new points array
    points = newPoints;

    // Return the intersection point indices
    return intersectIndices;
  }
  
  // -------------------------------------------------------------------------------- //
  /*public void draw( PGraphics g ) {
    g.beginShape();
    for ( Vector2 p : points )
      g.vertex( p.x, p.y );
    g.endShape( PGraphics.CLOSE );
  }*/
  
  // -------------------------------------------------------------------------------- // 
  private void bindIntersections( PointList a, ArrayList<Integer> ints_a, PointList b, ArrayList<Integer> ints_b ) {
    for( Point2 p : a ) p.neighbor = null;
    for( Point2 p : b ) p.neighbor = null;
    
    for ( Integer index_a : ints_a ) {
      Point2 pa = a.get( index_a );
      for ( Integer index_b : ints_b ) {
        Point2 pb = b.get( index_b );
        if ( pb.sub(pa).len() < 0.1f ) {
          pa.neighbor = pb;
          pb.neighbor = pa;
        }
      }
    }
  }
  // -------------------------------------------------------------------------------- // 
  private void markEntryExit( PointList pts, Poly2 other, boolean nextEntry ) {
    for ( int i=0; i<pts.size(); ++i ) {
      Point2 p = pts.get( i );
      if ( p.neighbor != null ) {
        p.entry          = nextEntry;
        nextEntry        = !nextEntry;
      }
    }
  }
  // -------------------------------------------------------------------------------- //
  private void removeIndex( int index, ArrayList<Integer> indices ) {
    for ( Integer contained : indices )
      if ( index == contained ) {
        indices.remove( contained );
        break;
      }
  }
  //private int findIndex( int index, ArrayList<Integer> indices ) {
  // -------------------------------------------------------------------------------- //
  private boolean containsIndex( int index, ArrayList<Integer> indices ) {
    for ( Integer contained : indices )
      if ( index == contained )
        return true;
    return false;
  }
}
