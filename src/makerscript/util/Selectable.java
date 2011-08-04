/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
 */
package makerscript.util;

import java.util.List;

import makerscript.geom.Vector3;
import makerscript.geom.mesh.Edge;
import makerscript.geom.mesh.Face;
import makerscript.geom.mesh.PolyLine;
import makerscript.geom.mesh.Vertex;


public interface Selectable {
  public  List<Vertex>            getVerts  ( );
  public  List<Edge>              getEdges  ( );
  public  NamedMultiMap<PolyLine> getPolys  ( );
  public  NamedMultiMap<Face>     getFaces  ( );

  public  String                  getType   ( );
  public  int                     getIndex  ( );
  public  Vector3                 getMin    ( Vector3 min );
  public  Vector3                 getMax    ( Vector3 max );
}
