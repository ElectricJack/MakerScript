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



public class CmdAlign extends Command {
  //---------------------------------------------------------------------------------
  public         CmdAlign ( CommandStore cs ) { super( cs, "align x +float | align y +float | align z +float | align x +float y +float | align x +float y +float z +float" ); }
  public         CmdAlign ( Command copy    ) { super( copy ); }
  public Command clone    ( )                 { return new CmdAlign(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    if( userState.selected == null )         return state.nextCommand();
    
    
    float x = popFloat(params);
    if      ( callIndex == 0 ) alignX( userState, x );
    else if ( callIndex == 1 ) alignY( userState, x );
    else if ( callIndex == 2 ) alignZ( userState, x );
    else {
      float y = popFloat(params);
      if    ( callIndex == 3 ) alignXY( userState, x, y );
      else {
        float z = popFloat(params);
        if  ( callIndex == 4 ) alignXYZ( userState, x, y, z );
      }
    }
    
    return state.nextCommand();
  }  
  
  //------------------------------------------------------------------------ //
  public void alignX( ScriptableMillState userState, float x )
  {
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();
    
    // Now align the selection to its new minimum value
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.x = (v.x - bounds.vMin.x) + x;

  }
  
  // ------------------------------------------------------------------------ //
  public void alignY( ScriptableMillState userState, float y )
  {
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();
    
    // Now align the selection to its new minimum value
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.y = (v.y - bounds.vMin.y) + y;

  }
  
  // ------------------------------------------------------------------------ //
  public void alignZ( ScriptableMillState userState, float z )
  {    
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();

    // Now align the selection to its new minimum value
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.z = (v.z - bounds.vMin.z) + z;
  }
  
  // ------------------------------------------------------------------------ //
  public void alignXY( ScriptableMillState userState, float x, float y )
  {
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();

    // Now align the selection to its new minimum value
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() ) {
        v.x = (v.x - bounds.vMin.x) + x;
        v.y = (v.y - bounds.vMin.y) + y;
      }
  }
  
  // ------------------------------------------------------------------------ //
  public void alignXYZ( ScriptableMillState userState, float x, float y, float z )
  {
    // First calculate/retrieve the bounds of the selection
    AABB<Vector3> bounds = userState.getSelectionBounds();

    // Now align the selection to its new minimum value
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() ) {
        v.x = (v.x - bounds.vMin.x) + x;
        v.y = (v.y - bounds.vMin.y) + y;
        v.z = (v.z - bounds.vMin.z) + z;
      }
  }
}
