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

package makerscript.math;

import makerscript.geom.Vector3;

public class Mathematical {
  protected static final float PI = 3.1415926f;
  
  // ------------------------------------------------------------------------------------------------------------- //
  protected float sin( float theta ) {
    return (float)Math.sin( theta );
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected float cos( float theta ) {
    return (float)Math.cos( theta );
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected float lerp( float a, float b, float t ) {
    return a*(1.f - t) + b*t;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected float map( float value, float fromMin, float fromMax, float toMin, float toMax ) {
    return  toMin + (toMax - toMin) * ((value - fromMin) / (fromMax - fromMin));
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected float bezierPoint( float v0, float v1, float v2, float v3, float t ) {
    float t2 = t*t;
    float t3 = t2*t;
    float c  = 3*(v1 - v0);
    float b  = 3*(v2 - v1) - c;
    float a  = v3 - v0 - c - b;
    return a*t3 + b*t2 + c*t + v0;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected float estimateBezierLength( Vector3 pa, Vector3 pb, Vector3 pc, Vector3 pd ) {
    float  dist  = pb.sub( pa ).len();
           dist += pc.sub( pb ).len();
           dist += pd.sub( pc ).len();
    return dist;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  protected float radians( float degrees ) {
    return degrees * PI / 180.f;
  }

}
