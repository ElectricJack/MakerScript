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
package makerscript.lang;


public class LScriptState
{
  public VariableBank vars        = null;
  public LoopState[]  loopInfo    = new LoopState[8];
  public int          loopNest    = 0;
  public int          ifNest      = 0;
  public int          jumpNest    = 0;
  public boolean      jumpElse    = false;
  public boolean      jumpEndIf   = false;
  public int          codeLine    = 0;
  public Object       userState   = null;
  
  public LScriptState()
  {
    reset(null);
    for( int i=0; i<loopInfo.length; ++i )
      loopInfo[i] = new LoopState();
  }
  
  public void reset(Object userState)
  {
    this.codeLine    = 0;
    this.loopNest    = 0;
    this.ifNest      = 0;
    this.jumpNest    = 0;
    this.jumpElse    = false;
    this.jumpEndIf   = false;
    this.userState   = userState;
  }

  public int nextCommand() { return codeLine + 1; }
}
