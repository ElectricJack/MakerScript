/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */
package makerscript;

import java.util.ArrayList;
import java.util.List;

import makerscript.geom.Vector3;
import makerscript.geom.mesh.DrawWriter;
import makerscript.geom.mesh.Edge;
import makerscript.geom.mesh.Face;
import makerscript.geom.mesh.Mesh;
import makerscript.geom.mesh.PolyLine;
import makerscript.geom.mesh.Vertex;
import makerscript.util.NamedMultiMap;
import makerscript.util.SelectableBase;

import processing.core.PGraphics;


public class Layer extends SelectableBase {
  public List<Mesh>        meshes  = new ArrayList<Mesh>();
  public List<Vector3>     targets = new ArrayList<Vector3>();
  
  public void add( Layer other ) {
    meshes.addAll( other.meshes );
    targets.addAll( other.targets );
  }
  public void add( Mesh       mesh   ) { meshes.add( mesh ); }
  public void add( Vector3    target ) { targets.add( target ); }
  

  public void clear() {
    meshes.clear();
    targets.clear();
  }
  
  public void clean() {
    for( Mesh mesh : meshes ) {
      mesh.clean();
    }
  }
  
  public ArrayList<Vertex>   	   getVerts()
  {
    ArrayList<Vertex>      verts = new ArrayList<Vertex>();
    for( Mesh m : meshes ) verts.addAll( m.getVerts() );
    return                 verts;
  }
  public NamedMultiMap<PolyLine> getPolys()
  {
    NamedMultiMap<PolyLine> polys = new NamedMultiMap<PolyLine>();
    for( Mesh m : meshes )  polys.addAll( m.getPolys() );
    return                  polys;
  }
  public NamedMultiMap<Face> 	   getFaces( ) {
    NamedMultiMap<Face>    faces = new NamedMultiMap<Face>();
    for( Mesh m : meshes ) faces.addAll( m.getFaces() );
    return                 faces;
  }
  public ArrayList<Edge> 	       getEdges()
  {
    ArrayList<Edge>        edges = new ArrayList<Edge>();
    for( Mesh m : meshes ) edges.addAll( m.getEdges() );
    return                 edges;
  }

  
  public Vector3 getMin(Vector3 min) { for( Mesh mesh : meshes ) min = mesh.getMin(min); return min; }
  public Vector3 getMax(Vector3 max) { for( Mesh mesh : meshes ) max = mesh.getMax(max); return max; }
  
  public void draw(  PGraphics g  ) { 
    DrawWriter meshDrawer = new DrawWriter( g );
    g.pushMatrix();
      g.scale(1.f,1.f,-1.f);
      g.fill(0.f,64.f);
      for( Mesh mesh : meshes )
        meshDrawer.write( mesh );
    g.popMatrix();
  }

}
