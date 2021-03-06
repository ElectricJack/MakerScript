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

import com.fieldfx.geom.mesh.Vertex;
import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;
import com.fieldfx.util.Selectable;

import makerscript.MakerScriptState;


public class CmdOffset extends Command {
  //---------------------------------------------------------------------------------
  public         CmdOffset ( CommandStore cs ) { super( cs, "offset x +float | offset y +float | offset z +float" ); }
  public         CmdOffset ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdOffset(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    if( state.jumpElse || state.jumpEndIf ) return state.nextCommand();
    if( userState.selected == null )        return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return offsetX( state, params );
      case 1: return offsetY( state, params );
      case 2: return offsetZ( state, params );
    }
    
    return state.nextCommand();
  }  
  
  // ------------------------------------------------------------------------ //
  public int offsetX( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    float               offset    = popFloat(params);
    
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.x += offset;
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int offsetY( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    float               offset    = popFloat(params);
    
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.y += offset;
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int offsetZ( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    float               offset    = popFloat(params);
    
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.z += offset;
    
    return state.nextCommand();
  }
}
