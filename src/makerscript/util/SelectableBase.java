package makerscript.util;


import java.util.List;

import makerscript.geom.Vector3;
import makerscript.geom.mesh.Edge;
import makerscript.geom.mesh.Face;
import makerscript.geom.mesh.PolyLine;
import makerscript.geom.mesh.Vertex;


public abstract class SelectableBase implements Selectable {
  public          List<Vertex>            getVerts  ( ) { return null; }
  public          List<Edge>              getEdges  ( ) { return null; }
  public          NamedMultiMap<PolyLine> getPolys  ( ) { return null; }
  public          NamedMultiMap<Face>     getFaces  ( ) { return null; }

  public abstract Vector3                 getMin    ( Vector3 min );
  public abstract Vector3                 getMax    ( Vector3 max );
}
