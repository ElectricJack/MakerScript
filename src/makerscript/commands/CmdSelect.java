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

import com.fieldfx.geom.mesh.Face;
import com.fieldfx.geom.mesh.PathPoint;
import com.fieldfx.geom.mesh.PolyLine;
import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;

import makerscript.MakerScriptState;


public class CmdSelect extends Command {
  //---------------------------------------------------------------------------------
  public         CmdSelect ( CommandStore cs ) { super( cs, "select all | select none | select path +int | select paths +int +int | select layer +int | select path section +int +float +float" ); }
  public         CmdSelect ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdSelect(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
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
  private int selectAll( ScriptState state ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    if( userState.activeLayer != null )
      for( Face s : userState.activeLayer.getFaces().getAll() )
        userState.select( s );
    
    System.out.println( "Selected " + userState.selected.size() + " paths." );
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectNone( ScriptState state ) {
    
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
                     userState.clearSelection();
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectPath( ScriptState state, Queue<ExpressionElement> params ) {
    
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    int                 pathIndex = popInt(params);
    
    if( userState.activeLayer != null && pathIndex >= 0 && pathIndex < userState.activeLayer.getFaces().size() )
      userState.select( userState.activeLayer.getFaces().get( pathIndex ) );
    
    System.out.println( "Selected " + userState.selected.size() + " paths." );
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int selectPaths( ScriptState state, Queue<ExpressionElement> params ) {
    
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState      = (MakerScriptState)state.userState;
    int                 pathIndexStart = popInt( params );
    int                 pathIndexEnd   = popInt( params );
    
    if( userState.activeLayer != null && 
        pathIndexStart >= 0 &&
        pathIndexEnd   <  userState.activeLayer.getFaces().size() &&
        pathIndexStart <= pathIndexEnd )
    {
      for( int i = pathIndexStart; i <= pathIndexEnd; ++i )
        userState.select( userState.activeLayer.getFaces().get( i ) );
    }
    
    System.out.println( "Selected " + userState.selected.size() + " paths." );
    return state.nextCommand();
  }
  
  private int selectLayer( ScriptState state, Queue<ExpressionElement> params ) {
    
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState  = (MakerScriptState)state.userState;
    int                 layerIndex = popInt( params );
    
    if( layerIndex >= 0 && layerIndex < userState.layers.size() )
      userState.activeLayer = userState.layers.get( layerIndex );
    
    return state.nextCommand();
  }
  
  private int selectPathSection( ScriptState state, Queue<ExpressionElement> params ) {

    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    int                 pathIndex = popInt   ( params );
    float               start     = popFloat ( params );
    float               distance  = popFloat ( params );
    
    if( userState.activeLayer != null && pathIndex >= 0 && pathIndex < userState.activeLayer.getFaces().size() )
    {
      PolyLine path = userState.activeLayer.getFaces().get( pathIndex );
      
      ArrayList<Float> pathSegmentLengths = new ArrayList<Float>();
      path.length( pathSegmentLengths, true );
      
      PathPoint pathStart = path.getAt( start, pathSegmentLengths );
      PathPoint pathEnd   = path.getAt( start+distance, pathSegmentLengths );
      
      PolyLine subPath = new PolyLine();
           subPath.add( pathStart.get(path) );
      for( int i = pathStart.index+1; i<=pathEnd.index; ++i )
           subPath.add( path.get(i) );
           subPath.add( pathEnd.get(path) );
      
      userState.select( subPath );
    }
    
    System.out.println( "Selected " + userState.selected.size() + " paths." );
    return state.nextCommand();
  }
  
  
}

