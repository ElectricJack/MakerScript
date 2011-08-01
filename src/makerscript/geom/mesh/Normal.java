package makerscript.geom.mesh;

import makerscript.geom.Vector3;

public class Normal extends Vector3 {

  public Normal()                          { super(0.f,0.f,1.f); }
  public Normal(float x, float y, float z) { super(x,y,z); this.nrmeq(); }

}
