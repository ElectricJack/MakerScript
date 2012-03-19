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

import makerscript.MakerScriptState;

import com.fieldfx.geom.mesh.Vertex;
import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;
import com.fieldfx.math.AABounds;
import com.fieldfx.math.Vector3;
import com.fieldfx.util.MathHelper;
import com.fieldfx.util.Selectable;



public class CmdMirror extends Command {
  //---------------------------------------------------------------------------------
  public         CmdMirror ( CommandStore cs ) { super( cs, "mirror x | mirror y | mirror z" ); }
  public         CmdMirror ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdMirror(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
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
  public int mirrorX( ScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    // First calculate/retrieve the bounds of the selection
    AABounds<Vector3> bounds = userState.getSelectionBounds();
    
    // Map every x to it's inverse
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.x = MathHelper.map( v.x, bounds.vMin.x, bounds.vMax.x, bounds.vMax.x, bounds.vMin.x );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int mirrorY( ScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    // First calculate/retrieve the bounds of the selection
    AABounds<Vector3> bounds = userState.getSelectionBounds();
    
    // Map every y to it's inverse
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.y = MathHelper.map( v.y, bounds.vMin.y, bounds.vMax.y, bounds.vMax.y, bounds.vMin.y );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int mirrorZ( ScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    // First calculate/retrieve the bounds of the selection
    AABounds<Vector3> bounds = userState.getSelectionBounds();
    
    // Map every z to it's inverse
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.z = MathHelper.map( v.z, bounds.vMin.z, bounds.vMax.z, bounds.vMax.z, bounds.vMin.z );
    
    return state.nextCommand();
  }
}

