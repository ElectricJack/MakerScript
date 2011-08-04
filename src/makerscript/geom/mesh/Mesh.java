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
package makerscript.geom.mesh;

import makerscript.geom.Vector3;
import makerscript.util.NamedMultiMap;

import makerscript.util.Convertible;
import makerscript.util.Nameable;
import makerscript.util.SelectableBase;

import java.util.ArrayList;
import java.util.List;


public class Mesh extends SelectableBase implements Convertible, Nameable {
	
  //protected NamedMultiMap < PolyLine > polys   = new NamedMultiMap < PolyLine >();
  private NamedMultiMap < Face     > faces   = new NamedMultiMap < Face     >();
  private String                     name    = "";
  private int                        index   = 0;
  
  public 	String    getType  ( )              { return "mesh"; }
  public 	int       getIndex ( )              { return index; }
  public    void      setIndex ( int index )    { this.index = index; }
  // ------------------------------------------------------------------------------------------------------------- //
  public void add( Face face ) {  
    faces.add(face);
  }
  // ------------------------------------------------------------------------------------------------------------- //
  //public void add( PolyLine polyLine ) {
    //polys.add( polyLine );
  //}
  
  
  public Vertex getVertex( int index ) {
	return null;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public void clear( ) { faces.clear(); }
  // ------------------------------------------------------------------------------------------------------------- //
  // Convertible Methods:
  public void convert( MeshBuilder builder ) {
      for( PolyLine poly : faces ) {
        builder.beginPoly();
          poly.convert( builder );
        builder.endPoly();
      }
  }
  // ------------------------------------------------------------------------------------------------------------- //
  // Nameable Methods:
  public String getName ( )                { return name; }
  public void   setName ( String name )    { this.name = name; }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public Vector3 getMin() {
    if( faces.size() == 0 || faces.get(0).getVerts().size() == 0 )
      return null;
    
    Vector3 minimum = faces.get(0).getVerts().get(0).get();
    for( PolyLine poly : faces )
      poly.getMin( minimum );
    
    return minimum;       
  }
  public Vector3 getMax() {
    if( faces.size() == 0 || faces.get(0).getVerts().size() == 0 ) return null;
    
    Vector3 maximum = faces.get(0).getVerts().get(0).get();
    for( PolyLine poly : faces )
      poly.getMax(maximum);

    return maximum;       
  }
  public Vector3 getMin( Vector3 min ) {
    for( PolyLine poly : faces )
      min = poly.getMin( min );
    return min;
  }
  public Vector3 getMax( Vector3 max ) {
    for( PolyLine poly : faces )
      max = poly.getMax( max );
    return max;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public ArrayList<Vertex> getVerts() {
    ArrayList<Vertex> verts = new ArrayList<Vertex>();
    for( PolyLine poly : faces ) verts.addAll( poly.getVerts() );
    return verts;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  //public NamedMultiMap<PolyLine> getPolys()   { return faces.getPolys(); }
  public NamedMultiMap<Face>     getFaces()   { return faces; }

  
  
  // ------------------------------------------------------------------------------------------------------------- //
  // The following should probably be moved into a "mesh" operator object that performs the
  // clean operation on a mesh. This way we can have lots of operations, without cluttering up basic
  // mesh functionality.
  // ------------------------------------------------------------------------------------------------------------- //
  public void clean( )
  {
    while( connectAdjacentPaths() );

    // Remove any adjacent duplicates from the list, until none are found]
    List<Face> cleanedPolyLines = new ArrayList<Face>();
    for( Face poly : faces ) {
      while( removeAdjacentDuplicates(poly) );
      if( poly.getVerts().size() > 1 ) cleanedPolyLines.add(poly);
    }
    
    faces.clear();
    faces.addAll( cleanedPolyLines );
  }
  private boolean connectAdjacentPaths()
  {
    NamedMultiMap<Face>  joinedLines = new NamedMultiMap<Face>();

    if( faces.size() < 1 ) return false;

    // Add our first line list    
    joinedLines.add( faces.get(0) );
    faces.remove(0);
    
    // Now check every one in the source list
    boolean merged = false;
    for( Face src : faces )
    {
      FindResults results = new FindResults();
      if( findAdjacentPath( src, joinedLines, results ) ) {
                 merged = true;
        Face dest   = joinedLines.get( results.index );
        combinePaths( dest, src, results );
      }
      else joinedLines.add( src );      
    }
    
    faces = joinedLines;
    
    return merged;
  }
  private boolean removeAdjacentDuplicates( Face testPoly )
  {
    float             threshold = 0.01f;
    boolean           found     = false;
    ArrayList<Vertex> newVerts  = new ArrayList<Vertex>();
    Vertex            compare   = testPoly.verts.get(0);
    
    newVerts.add( compare );
    
    for( int i=1; i<testPoly.verts.size(); ++i ) {
      if( compare.sub( testPoly.verts.get(i) ).len() > threshold ) {
        compare = testPoly.verts.get(i);
        newVerts.add( compare );
      }
      else found = true;
    }
    
    if( found )
      testPoly.verts = newVerts;
    
    return found;
  }
  private boolean findAdjacentPath( Face test, NamedMultiMap<Face> from, FindResults out )
  {
    float threshold = 0.01f;
    
    // Store off the first and last verts of the line we're adding
    Vector3 first = test.verts.get( 0 );
    Vector3 last  = test.verts.get( test.verts.size() - 1 );
    
    // Lets look through all the line lists and see if this
    //  line list should be inserted before or after any others in the list.
    boolean found  = false;
    out.index  = 0;
    out.before = false;
    out.flip   = false;       
    
    for( Face l : from )
    {
      if( l == test ) continue;
      
      Vector3 l0 = l.verts.get( 0 );
      Vector3 l1 = l.verts.get( l.verts.size() - 1 );
      
      if     ( l0.sub(last).len()  < threshold )  { found = true; out.before = true; break; }
      else if( l1.sub(first).len() < threshold )  { found = true; break; }
      else if( l0.sub(first).len() < threshold )  { found = true; out.flip = true; out.before = true; break; }
      else if( l1.sub(last).len()  < threshold )  { found = true; out.flip = true; break; }
      
      ++out.index;
    }    
    
    return found;
  }
  private void combinePaths( Face dest, Face src, FindResults how )
  {
    if( how.flip )
      src.flip();
      
    // Add them forwards
    int addAt = 0;
    for( Vertex v : src.verts )
    {
      if( how.before ) dest.verts.add( addAt++, v );
      else             dest.verts.add( v );
    }
  }
  private class FindResults
  {
      int     index  = 0;
      boolean before = false;
      boolean flip   = false;    
  }

 
}
