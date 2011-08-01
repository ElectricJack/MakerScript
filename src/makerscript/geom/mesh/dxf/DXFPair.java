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
package makerscript.geom.mesh.dxf;


public class DXFPair
{
  public  int       G;
  public  String    S;
  private DXFReader parent;
  
  // ------------------------------------------------------------------------------------------------------------- //
  public DXFPair( DXFReader parent )
  {
    this.parent = parent;
    G = parent.readNextInt();
    S = parent.readNextString();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public boolean is       ( int G, String S ) { return this.G == G && (this.S != null && this.S.equals(S)); }
  public String  toString ()                  { return G + " - " + S; }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public boolean next() {
    G = parent.readNextInt();
    S = parent.readNextString();    
    return S != null;
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public int getInt() {
    try                  { return Integer.parseInt( S ); }
    catch( Exception e ) { return 0; }    
  }
  // ------------------------------------------------------------------------------------------------------------- //
  public float getFloat() {
    try                  { return Float.parseFloat( S ); }
    catch( Exception e ) { return 0.f; }    
  }
}