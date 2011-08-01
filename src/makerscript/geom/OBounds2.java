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
package makerscript.geom;

public class OBounds2
{
  // No frontin' ( public data )
  public Line2 A;
  public Line2 B;
  public Line2 C;
  public Line2 D;
  
  // --------------------------------------------------------------------------- //
  public           OBounds2 ( float width, float height )                       { this( new Vector2(), new Vector2( width, height ) ); }
  public           OBounds2 ( Vector2 topLeft, Vector2 bottomRight )            { set( topLeft, new Vector2(topLeft.x, bottomRight.y), bottomRight, new Vector2(bottomRight.x, topLeft.y) );  }
  public           OBounds2 ( Vector2 vA, Vector2 vB, Vector2 vC, Vector2 vD )  { set( vA, vB, vC, vD );  }
  public           OBounds2 ( Line2    A, Line2    B, Line2    C, Line2    D )  { set(  A,  B,  C,  D );  }

  // --------------------------------------------------------------------------- //
  // Setters
  public OBounds2  set      ( Vector2 vA, Vector2 vB, Vector2 vC, Vector2 vD )  { return set( new Line2( vA, vB ), new Line2( vB, vC ), new Line2( vC, vD ), new Line2( vD, vA ) ); }
  public OBounds2  set      ( Line2    A, Line2    B, Line2    C, Line2    D )  { this.A = A; this.B = B; this.C = C; this.D = D; return this; }
  
  
  // --------------------------------------------------------------------------- //
  // Utility
  public boolean contains( Vector2 vPoint )   { return contains( vPoint.x, vPoint.y ); }
  public boolean contains( float x, float y )
  {
    return  A.inside( x,y ) &&
            B.inside( x,y ) &&
            C.inside( x,y ) &&
            D.inside( x,y );
  }
  
  //--------------------------------------------------------------------------- //
  public LineSeg2 clip( LineSeg2 out, Line2 line ) {
	  Vector2 resultA = A.intersect( line );
	  Vector2 resultB = B.intersect( line );
	  Vector2 resultC = C.intersect( line );
	  Vector2 resultD = D.intersect( line );
	  
	  if	  ( resultA != null && resultB != null && contains(resultA) && contains(resultB) ) return out.set(resultA, resultB);
	  else if ( resultA != null && resultC != null && contains(resultA) && contains(resultC) ) return out.set(resultA, resultC);
      else if ( resultA != null && resultD != null && contains(resultA) && contains(resultD) ) return out.set(resultA, resultD);
      else if ( resultB != null && resultC != null && contains(resultB) && contains(resultC) ) return out.set(resultB, resultB);
      else if ( resultB != null && resultD != null && contains(resultB) && contains(resultD) ) return out.set(resultB, resultB);
	  
	  return null;
  }
  
  
  //public boolean draw( PGraphics g )
  //{
  //}
  
}
