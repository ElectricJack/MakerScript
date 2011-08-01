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

public class CmdCreateVar extends Command
{
  // --------------------------------------------------------------------------------- //
  public         CmdCreateVar ( CommandStore cs ) { super( cs, "int +variable = +@int | float +variable = +@float | boolean +variable = +@bool" ); }
  public         CmdCreateVar ( Command copy    ) { super( copy ); }
  public Command clone        ( )                 { return new CmdCreateVar(this); }  
  // --------------------------------------------------------------------------------- //
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex ) {
    
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
      
    switch( callIndex ) {
      case 0: return createInteger  ( state, popVarRef(params), popInt(params) );
      case 1: return createFloat    ( state, popVarRef(params), popFloat(params) );
      case 2: return createBoolean  ( state, popVarRef(params), popBool(params) );
    }
    
    return -1;
  }
  
  // --------------------------------------------------------------------------------- //
  private int createInteger( LScriptState state, int varIndex, int value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createFloat( LScriptState state, int varIndex, float value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }
  // --------------------------------------------------------------------------------- //
  private int createBoolean( LScriptState state, int varIndex, boolean value ) {
    state.vars.setVariable( varIndex, value );
    return state.nextCommand();
  }

}