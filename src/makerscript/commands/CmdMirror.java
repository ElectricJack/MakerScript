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
import makerscript.geom.AABB;
import makerscript.geom.Vector3;
import makerscript.geom.mesh.Vertex;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;
import makerscript.util.Selectable;


public class CmdMirror extends Command {
  //---------------------------------------------------------------------------------
  public         CmdMirror ( CommandStore cs ) { super( cs, "mirror x | mirror y | mirror z" ); }
  public         CmdMirror ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdMirror(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    if( state.jumpElse || state.jumpEndIf ) return state.nextCommand();
    if( userState.selected == null )        return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return mirrorX( state );
      case 1: return mirrorY( state );
      case 2: return mirrorZ( state );
    }
    
    return state.nextCommand();
  }  
  
  // ------------------------------------------------------------------------ //  
  public int mirrorX( LScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();
    
    // Map every x to it's inverse
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.x = map( v.x, bounds.vMin.x, bounds.vMax.x, bounds.vMax.x, bounds.vMin.x );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int mirrorY( LScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();
    
    // Map every y to it's inverse
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.y = map( v.y, bounds.vMin.y, bounds.vMax.y, bounds.vMax.y, bounds.vMin.y );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int mirrorZ( LScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();
    
    // Map every z to it's inverse
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.z = map( v.z, bounds.vMin.z, bounds.vMax.z, bounds.vMax.z, bounds.vMin.z );
    
    return state.nextCommand();
  }
}

