package makerscript.geom.mesh.collada;

import makerscript.geom.mesh.Face;
import makerscript.geom.mesh.Mesh;
import makerscript.geom.mesh.Vertex;
import processing.xml.XMLElement;

public class COLLADAMesh extends Mesh {
  public String materialName;
  public String id;
  
  // ------------------------------------------------------------------------ //
  public COLLADAMesh( XMLElement mesh, String id ) {

    this.id = id;
    
    String positionArrayID = "";
    String normalArrayID   = "";
    XMLElement verts = mesh.getChild( "vertices" );
    if( verts != null ) {
      for( XMLElement child : verts.getChildren() ) {
        if( COLLADAHelpers.isNameOf( child, "input" ) ) {
          if      ( COLLADAHelpers.isAttribOf( child, "semantic", "POSITION" ) ) positionArrayID = child.getString("source");
          else if ( COLLADAHelpers.isAttribOf( child, "semantic", "NORMAL"   ) ) normalArrayID   = child.getString("source");
        }
      }
    }

    for( XMLElement child : mesh.getChildren() ) {
      if( COLLADAHelpers.isNameOf( child, "source" ) ) {
        String childID = ("#" + child.getString("id"));
        if      ( childID.equals( positionArrayID ) ) loadPositions ( child );
        else if ( childID.equals( normalArrayID   ) ) loadNormals   ( child );
      }
      else if ( COLLADAHelpers.isNameOf( child, "triangles" ) ) loadTriangles ( child );
      else if ( COLLADAHelpers.isNameOf( child, "polylist"  ) ) loadPolylist  ( child );
      else if ( COLLADAHelpers.isNameOf( child, "polygons"  ) ) loadPolygons  ( child );
    }
  }

  // ------------------------------------------------------------------------ //
  private void loadPositions( XMLElement positions ) {
    if( positions == null ) return;
    float[] values = COLLADAHelpers.getFloatArray( positions.getChild("float_array") );
    
    for( int i=0; i<values.length; i += 3 ) {
      Vertex v = new Vertex();
             v.x = values[i+0] * 25.4f;
             v.y = values[i+1] * 25.4f;
             v.z = values[i+2] * 25.4f;    
      verts.add( v );
    }
  }
  // ------------------------------------------------------------------------ //
  private void loadNormals( XMLElement normals ) {
    if( normals == null ) return;
    float[] values = COLLADAHelpers.getFloatArray( normals.getChild("float_array") );
    
    for( int i=0; i<values.length; i += 3 ) {
      int n = i / 3;
      if( n >= verts.size() ) continue;
      Vertex v = verts.get(n);
             v.n.x = values[i+0];
             v.n.y = values[i+1];
             v.n.z = values[i+2];
    }
  }
  // ------------------------------------------------------------------------ //
  private void loadTriangles( XMLElement triangles ) {
    int   count = triangles.getInt("count");
    int[] data  = COLLADAHelpers.getIntArray( triangles.getChild("p") );
    
    if( count != data.length/3 )
      System.out.println( "specified triangle count and point indicies are out of proportion!" );
    
    for( int i=0; i<count; ++i ) {
      int n = i*3;
      int a = data[n+0];
      int b = data[n+1];
      int c = data[n+2];
      
      Face p = new Face();
              p.add( verts.get(a) );
              p.add( verts.get(b) );
              p.add( verts.get(c) );
      
      polys.add( p );
    }
  }
  
  // ------------------------------------------------------------------------ //
  private void loadPolylist( XMLElement polygons ) {
    int   count           = polygons.getInt("count");
    int[] pointIndicies   = COLLADAHelpers.getIntArray( polygons.getChild("p") );
    int[] polyPointCounts = COLLADAHelpers.getIntArray( polygons.getChild("vcount") );
    
    if( count != polyPointCounts.length )
      System.out.println( "specified poly count and poly point counts are out of proportion!" );
      
    // For each polygon
    int pointIndex = 0;
    for( int polyIndex=0; polyIndex<polyPointCounts.length; ++polyIndex ) {
      
      // Get the number of points in the polygon, and create our polygon object
      int     points = polyPointCounts[polyIndex];
      Face poly   = new Face();
      
      // Add the coresponding number of points into the polygon
      for( int i=0; i<points && pointIndex<pointIndicies.length; ++i, ++pointIndex ) {
        Vertex vert = verts.get( pointIndicies[pointIndex] );
        poly.add( vert );
      }
      
      // Finally, add the new polygon
      polys.add( poly );
    }
  }

  // ------------------------------------------------------------------------ //
  private void loadPolygons( XMLElement polygons ) {
    //int count = polygons.getInt("count");
    for( XMLElement child : polygons.getChildren() ) {
      if( COLLADAHelpers.isNameOf( child, "ph" ) ) {

        Face poly = null;
        for( XMLElement phChild : child.getChildren() ) {
          if      ( COLLADAHelpers.isNameOf( phChild, "p" ) ) poly = loadPolygon( phChild );
          else if ( COLLADAHelpers.isNameOf( phChild, "h" ) && poly != null ) poly.addHole( loadPolygon( phChild ) );
        }
        
        // Finally, add the new polygon
        polys.add( poly );
      }
    }
  }
  
  // ------------------------------------------------------------------------ //
  private Face loadPolygon( XMLElement polygon ) {
    if( !COLLADAHelpers.isNameOf( polygon, "p" ) 
     && !COLLADAHelpers.isNameOf( polygon, "h" ) ) return null;
    
    Face poly          = new Face();
    int[]   pointIndicies = COLLADAHelpers.getIntArray( polygon );
    
    for( int i=0; i<pointIndicies.length; ++i )
      poly.add( verts.get( pointIndicies[i] ) );
    
    return  poly;
  }
}
