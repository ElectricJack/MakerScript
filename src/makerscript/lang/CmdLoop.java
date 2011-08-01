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

public class CmdLoop extends Command
{
  // Loop I From X To Y
  // Loop X Times
  // End Loop
  //---------------------------------------------------------------------------------
  public         CmdLoop ( CommandStore cs ) { super( cs, "loop +int times | loop +variable from +int to +int | end loop"); }
  public         CmdLoop ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdLoop(this); }  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
      
    switch( callIndex ) {
      case 0: return BeginLoopTimes( state, popInt(params) );
      case 1: return BeginLoopFrom( state, popVarRef(params), popInt(params), popInt(params) );
      case 2: return EndLoop( state );
    }

    return state.nextCommand();
  }
  
  // ---------------------------------------------------------------------------------
  protected int BeginLoopTimes( LScriptState state, int nTimes )
  {
    if( state.loopNest == state.loopInfo.length - 1 )
      return -1; // Too many nested loops
    
    state.loopInfo[ state.loopNest ].varIndex = -1;
    state.loopInfo[ state.loopNest ].index    = 0;
    state.loopInfo[ state.loopNest ].start    = 0;
    state.loopInfo[ state.loopNest ].end      = nTimes - 1;    
    
    state.loopInfo[ state.loopNest ].line = state.nextCommand();
    
    state.loopNest++;
    
    return state.nextCommand();
  }
  // ---------------------------------------------------------------------------------
  protected int BeginLoopFrom( LScriptState state, int varIndex, int start, int end )
  {
    if( state.loopNest == state.loopInfo.length - 1 )
      return -1; // Too many nested loops

    state.loopInfo[ state.loopNest ].varIndex = varIndex;
    state.loopInfo[ state.loopNest ].start    = start;
    state.loopInfo[ state.loopNest ].index    = state.loopInfo[ state.loopNest ].start;
    state.loopInfo[ state.loopNest ].end      = end;
    
    state.vars.setVariable( varIndex, state.loopInfo[ state.loopNest ].start );
     
    state.loopInfo[ state.loopNest ].line = state.nextCommand(); 
    
    state.loopNest++;
    
    return state.nextCommand();
  } 
  // ---------------------------------------------------------------------------------
  protected int EndLoop( LScriptState state )
  {
    int loopInfoIndex = state.loopNest - 1;
    
    // Check if we're done with the loop
    if( state.loopInfo[ loopInfoIndex ].index == state.loopInfo[ loopInfoIndex ].end )
    {
      --state.loopNest;
      return state.nextCommand();
    }
  
    // Increment the counter in the correct direction
    if( state.loopInfo[ loopInfoIndex ].end < state.loopInfo[ loopInfoIndex ].start )
      --state.loopInfo[ loopInfoIndex ].index;
    else
      ++state.loopInfo[ loopInfoIndex ].index;
  
    // Update the variable if it exists
    if( state.loopInfo[ loopInfoIndex ].varIndex != -1 )
      state.vars.setVariable( state.loopInfo[ loopInfoIndex ].varIndex, state.loopInfo[ loopInfoIndex ].index );
  
    return state.loopInfo[ loopInfoIndex ].line;
  }
}
