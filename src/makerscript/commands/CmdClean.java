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
package makerscript.commands;

import java.util.Queue;

import makerscript.ScriptableMillState;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;


public class CmdClean extends Command {
  //---------------------------------------------------------------------------------
  public         CmdClean ( CommandStore cs ) { super( cs, "clean" ); }
  public         CmdClean ( Command copy    ) { super( copy ); }
  public Command clone    ( )                 { return new CmdClean(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    if( userState.selected == null )         return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return clean( state, params );
    }
    
    return state.nextCommand();
  }  
  
  //------------------------------------------------------------------------ //
  public int clean(  LScriptState state, Queue<ExpressionElement> params  )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( userState.activeLayer != null ) {
      userState.activeLayer.clean();
    }
    
    return state.nextCommand();
  }
  

}

