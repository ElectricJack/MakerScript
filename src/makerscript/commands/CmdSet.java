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

//import scriptcam.geom.Vector3;

public class CmdSet extends Command {
  //---------------------------------------------------------------------------------
  public         CmdSet ( CommandStore cs ) { super( cs, "set x +float | set y +float | set z +float | "
                                                       + "set target +int x +float | set target +int y +float | set target +int z +float | "
                                                       + "set stock size +float +float +float"); }
  public         CmdSet ( Command copy    ) { super( copy ); }
  public Command clone  ( )                 { return new CmdSet(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
    
    switch( callIndex ) {
      //case 0: return setFeedrate ( state, params );
      case 0: return setX         ( state, params );
      case 1: return setY         ( state, params );
      case 2: return setZ         ( state, params );
      case 3: return setTargetX   ( state, params );
      case 4: return setTargetY   ( state, params );
      case 5: return setTargetZ   ( state, params );
      case 6: return setStockSize ( state, params );
    }
    
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setX( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float value = popFloat( params );
    
    if( userState.selected == null ) return state.nextCommand();
    for( Selectable item  : userState.selected )
      for( Vertex v : item.getVerts() )
        v.x = value;    
        
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setY( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float value = popFloat( params );
    
    if( userState.selected == null ) return state.nextCommand();
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.y = value;
        
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setZ( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float value = popFloat( params );
    
    if( userState.selected == null ) return state.nextCommand();
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
        v.z = value;
        
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setTargetX( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    int   targetIndex = popInt   ( params );
    float value       = popFloat ( params );
    
    if( targetIndex < 0 || targetIndex >= userState.activeLayer.targets.size() ) return state.nextCommand();
    userState.activeLayer.targets.get(targetIndex).x = value;    
    
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setTargetY( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    int   targetIndex = popInt   ( params );
    float value       = popFloat ( params );
    
    if( targetIndex < 0 || targetIndex >= userState.activeLayer.targets.size() ) return state.nextCommand();
    userState.activeLayer.targets.get(targetIndex).y = value;
        
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setTargetZ( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    int   targetIndex = popInt   ( params );
    float value       = popFloat ( params );
    
    if( targetIndex < 0 || targetIndex >= userState.activeLayer.targets.size() ) return state.nextCommand();
    userState.activeLayer.targets.get(targetIndex).z = value;    
        
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int setStockSize ( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float x = popFloat( params );
    float y = popFloat( params );
    float z = popFloat( params );
    userState.stockSize.set( x, y, z );
    
    return state.nextCommand();
  }
}
