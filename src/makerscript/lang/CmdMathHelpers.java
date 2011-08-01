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

import java.util.Queue;

class CmdMathHelpers extends Command
{
  // Lerp
  //---------------------------------------------------------------------------------
  public         CmdMathHelpers ( CommandStore cs ) { super( cs, "interpolate +variable from +float to +float"); }
  public         CmdMathHelpers ( Command copy    ) { super( copy ); }
  public Command clone          ( )                 { return new CmdMathHelpers(this); }  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
      
    switch( callIndex ) {
      case 0: return LerpVar( state, popVarRef(params), popFloat(params), popFloat(params) );
    }
    
    return state.nextCommand();
  } 
  
  protected int LerpVar( LScriptState state, int varIndex, float fV1, float fV2 )
  {     
    if( state.loopNest == 0 )
      return -1; // Lerp must be in a loop

    // Calculate the t of the loop
    LoopState li = state.loopInfo[state.loopNest-1];
    float t = (float)( li.index - li.start ) / (float)( li.end - li.start + 1 );
    
    // set the new value
    state.vars.setVariable( varIndex, fV1 * (1.0f - t) + fV2 * t );
    
    return state.nextCommand();
  }  
}
