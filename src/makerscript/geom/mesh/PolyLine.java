/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */
package makerscript.geom.mesh;

//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import makerscript.geom.Vector3;

import makerscript.util.Convertible;
import makerscript.util.Nameable;
import makerscript.util.SelectableBase;

import processing.core.PGraphics;


public class PolyLine extends SelectableBase implements Convertible, Nameable
{
  protected List< Vertex >  verts  = new ArrayList< Vertex >();
  protected List< Edge >    edges  = new ArrayList< Edge >();
  protected String          name   = "";
  public    int             index  = 0;
  
  public              PolyLine ( )              { }
  public              PolyLine ( String name )  { this.name = name; }
  
  public              PolyLine ( PolyLine other ) {
	this.name  = other.name;
	this.index = other.index;
  }
  
  public    String    getName  ( )              { return name; }
  public    void      setName  ( String name )  { this.name = name; }
  public 	String    getType  ( )              { return "path"; }
  public 	int       getIndex ( )              { return index; }
  
  public    void      add      ( Vector3 v )    { this.add( new Vertex(v) ); }
  public    void      add      ( Vertex  v )    {
	int index = verts.size() - 1;
	if( index >= 0 ) {
	  Vertex     first = verts.get( index ); 
	  
	  Edge       e        = new Edge();
	             e.index  = index;
	             e.first  = first; 
	             e.second = v;
	           //e.previous
	           //e.next
	  edges.add( e );  
	}
    verts.add(v);
  }
  public    void      clear    ( )              { verts.clear(); }
 
  
  
  // ------------------------------------------------------------------------------------------------------------- //
  public Vector3 getMin( Vector3 min ) {
    for( Vertex vert : verts )
      min = vert.getMin( min );
    return min;
  }
  public Vector3 getMax( Vector3 max ) {
    for( Vertex vert : verts )
      max = vert.getMax( max );
    return max;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public List<Vertex>   getVerts() { return verts; }
  public List<Edge>     getEdges() { return edges; }

  // ------------------------------------------------------------------------------------------------------------- //

  // ------------------------------------------------------------------------------------------------------------- //
  public void convert( MeshBuilder builder ) {
    builder.beginPoly();
	  for( Vertex v : verts ) {
	    builder.normal( v.n.x, v.n.y, v.n.z );
	    builder.vertex( v.x,  v.y,  v.z  );
	  }
    builder.endPoly();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public Vertex get( int index ) { return get( index, null ); }
  public Vertex get( int index, PGraphics g )
  {
    int i = index % verts.size();
    while( i < 0 ) i += verts.size();
    
    if( g != null )
    {
      Vector3 vert = verts.get(i);
      return new Vertex( g.modelX(vert.x, vert.y, vert.z)
                       , g.modelY(vert.x, vert.y, vert.z)
                       , g.modelZ(vert.x, vert.y, vert.z) );
    }
    return verts.get( i );
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public PathPoint getAt( float distanceAlongPath, ArrayList<Float> pathLineLengths )
  { 
    if( pathLineLengths == null ) return null;
    if( verts.size() == 0 || pathLineLengths.size() == 0 ) return null;

    int  i;
    int  count = pathLineLengths.size();
    for( i = 0; distanceAlongPath > pathLineLengths.get(i); ++i, i %= count )
      distanceAlongPath -= pathLineLengths.get(i);
    
    PathPoint out       = new PathPoint();
              out.t     = distanceAlongPath / pathLineLengths.get(i); 
              out.index = i;
    return    out;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  // The following load/save code should be moved/rewritten as an operation:
  /*
  public void save( PrintWriter out )
  {
    out.println( name );
    out.println( verts.size() );
    for( Vector3 v : verts )
      out.println( v.toString() );
  }
  public int load( String[] loadedFile, int readIndex ) {
        name      = loadedFile[ readIndex++ ]; // Name of this line list
    int vertCount = Integer.parseInt(loadedFile[ readIndex++ ]); // Number of points in the list
    
    verts.clear();
    for( int i=0; i<vertCount; ++i )
    {
      String   sv  = loadedFile[ readIndex++ ];  // Each line has 3 coords now
      String[] out = sv.substring(1,sv.length()-1).split(",");
      verts.add( new Vertex( Float.parseFloat(out[0])
                            , Float.parseFloat(out[1])
                            , Float.parseFloat(out[2]) ) );      
    }
    return readIndex;
  }*/

  // ------------------------------------------------------------------------------------------------------------- //
  public void flip() {
    List<Vertex> flipped = new ArrayList<Vertex>();
    for( Vertex v : verts )
      flipped.add(0, v);
    verts = flipped;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public float length() { return length( null, true ); }
  public float length( ArrayList<Float> pathLineLengths, boolean closed )
  {
    float totalLength = 0;
    int   count       = verts.size() - (closed? 0 : 1);

  
    for( int i=0; i<count; ++i )
    {
      int     k  = (i+1) % verts.size();
      Vector3 v0 = verts.get( i );
      Vector3 v1 = verts.get( k );

      if( v0 == null ) { System.out.println( "Null vert at index " + i + " in path!" ); continue; }
      if( v1 == null ) { System.out.println( "Null vert at index " + k + " in path!" ); continue; }
      
      Vector3 vDiff  = v0.sub(v1);
      if( vDiff.lenlen() < 0.0001 )
      {
        verts.remove(i);
        --i; --count;
        System.out.println( "ERROR, found duplicate point! [" + i + "]" );
        continue;
      }

      float   segmentLength = vDiff.len();
      totalLength += segmentLength;  // Add to the total length
      if( pathLineLengths != null )  // Cache this segment's length to speed up future searching for the correct segment.
          pathLineLengths.add( segmentLength ); 
    }
    
    return totalLength;
  }
}
