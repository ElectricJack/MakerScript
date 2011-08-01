/*
  ScriptableMill
  Copyright (c) 2011, Jack W. Kern
 */
package makerscript.geom.mesh;

import makerscript.geom.Vector3;

public class PathPoint
{
  public int   index;
  public float t;
  
  public Vector3 get( PolyLine path )
  {
    Vector3 v0       = path.get( index   );
    Vector3 v1       = path.get( index+1 );
    Vector3 vPoint   = new Vector3();
            vPoint.lerp( v0, v1, t ); 
    return  vPoint;
  }
}
