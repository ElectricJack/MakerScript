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


import java.util.ArrayList;
import java.util.Queue;

import makerscript.ScriptableMillState;
import makerscript.geom.mesh.PathPoint;
import makerscript.geom.mesh.PolyLine;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;


public class CmdSelect extends Command {
  //---------------------------------------------------------------------------------
  public         CmdSelect ( CommandStore cs ) { super( cs, "select all | select none | select path +int | select paths +int +int | select layer +int | select path section +int +float +float" ); }
  public         CmdSelect ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdSelect(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return selectAll         ( state );
      case 1: return selectNone        ( state );
      case 2: return selectPath        ( state, params );
      case 3: return selectPaths       ( state, params );
      case 4: return selectLayer       ( state, params );
      case 5: return selectPathSection ( state, params );
    }
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectAll( LScriptState state ) {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( userState.activeLayer != null )
      userState.selected.addAll( userState.activeLayer.getPolys() );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectNone( LScriptState state ) {
    
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
                        userState.selected.clear();
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectPath( LScriptState state, Queue<ExpressionElement> params ) {
    
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    int                 pathIndex = popInt(params);
    
    if( userState.activeLayer != null && pathIndex >= 0 && pathIndex < userState.activeLayer.getPolys().size() )
      userState.selected.add( userState.activeLayer.getPolys().get( pathIndex ) );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectPaths( LScriptState state, Queue<ExpressionElement> params ) {
    
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState      = (ScriptableMillState)state.userState;
    int                 pathIndexStart = popInt( params );
    int                 pathIndexEnd   = popInt( params );
    
    if( userState.activeLayer != null && 
        pathIndexStart >= 0 &&
        pathIndexEnd   <  userState.activeLayer.getPolys().size() &&
        pathIndexStart <= pathIndexEnd )
    {
      for( int i = pathIndexStart; i <= pathIndexEnd; ++i )
        userState.selected.add( userState.activeLayer.getPolys().get( i ) );
    }
    
    return state.nextCommand();
  }
  
  private int selectLayer( LScriptState state, Queue<ExpressionElement> params ) {
    
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState  = (ScriptableMillState)state.userState;
    int                 layerIndex = popInt( params );
    
    if( layerIndex >= 0 && layerIndex < userState.layers.size() )
      userState.activeLayer = userState.layers.get( layerIndex );
    
    return state.nextCommand();
  }
  
  private int selectPathSection( LScriptState state, Queue<ExpressionElement> params ) {

    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    int                 pathIndex = popInt   ( params );
    float               start     = popFloat ( params );
    float               distance  = popFloat ( params );
    
    if( userState.activeLayer != null && pathIndex >= 0 && pathIndex < userState.activeLayer.getPolys().size() )
    {
      PolyLine path = userState.activeLayer.getPolys().get( pathIndex );
      
      ArrayList<Float> pathSegmentLengths = new ArrayList<Float>();
      path.length( pathSegmentLengths, true );
      
      PathPoint pathStart = path.getAt( start, pathSegmentLengths );
      PathPoint pathEnd   = path.getAt( start+distance, pathSegmentLengths );
      
      PolyLine subPath = new PolyLine();
           subPath.add( pathStart.get(path) );
      for( int i = pathStart.index+1; i<=pathEnd.index; ++i )
           subPath.add( path.get(i) );
           subPath.add( pathEnd.get(path) );
      
      userState.selected.add( subPath );
    }
    
    return state.nextCommand();
  }
  
  
}

