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

public interface MeshBuilder {
  
  public void pushMatrix  ( );
  public void popMatrix   ( );
  
  public void applyMatrix ( float[] matrix );
  public void translate   ( float x, float y, float z ); 
  public void rotate      ( float a );
  public void scale       ( float x, float y, float z );
  
  public void ellipse     ( float x, float y, float d1, float d2 );
  public void arc         ( float x, float y, float d1, float d2, float a1, float a2 );
  public void line        ( float x, float y, float z, float x2, float y2, float z2 );

  public void beginObject ( String name );
  public void endObject   ( );
  public void instance    ( String name );
  
  public void beginPoly   ( );
  public void endPoly     ( );
  
  public void vertex      ( float x, float y );
  public void vertex      ( float x, float y, float z );
  public void normal      ( float x, float y, float z );
}
