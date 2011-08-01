package makerscript.geom.mesh;

import java.util.ArrayList;

import makerscript.geom.Vector3;
import makerscript.util.SelectableBase;


public class Edge extends SelectableBase {

  protected Vertex  first    = null;
  protected Vertex  second   = null;
  protected Edge    previous = null;
  protected Edge    next     = null;
  protected Face    inside   = null;
  protected Face    outside  = null;
  protected float   bulge    = 0;

  
  public ArrayList<Vertex> getVerts() {
    ArrayList<Vertex> verts = new ArrayList<Vertex>();
    verts.add(first);
    verts.add(second);
    return verts;
  }

  public Vector3 getMin(Vector3 min) {
    return null;
  }

  public Vector3 getMax(Vector3 max) {
    return null;
  }

}
