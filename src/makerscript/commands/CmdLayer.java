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

import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;

import makerscript.Layer;
import makerscript.MakerScriptState;



public class CmdLayer extends Command {
  //---------------------------------------------------------------------------------
  public         CmdLayer ( CommandStore cs ) { super( cs, "add layer | remove layer +int | merge layers +int +int" ); }
  public         CmdLayer ( Command copy    ) { super( copy ); }
  public Command clone      ( )                 { return new CmdLayer(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )
      return state.nextCommand();
    
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    if      ( callIndex == 0 ) addLayer( userState );
    else if ( callIndex == 1 ) {
      int layerIndex = popInt( params );
      removeLayer( userState, layerIndex );
    }
    else if ( callIndex == 2 ) {
      int firstLayerIndex  = popInt( params );
      int secondLayerIndex = popInt( params );
      mergeLayers( userState, firstLayerIndex, secondLayerIndex );
    }
    
    return state.nextCommand();
  }

  //---------------------------------------------------------------------------------  
  private void addLayer( MakerScriptState userState ) {
    userState.layers.add( new Layer() );
  }

  //---------------------------------------------------------------------------------  
  private void removeLayer( MakerScriptState userState, int layerIndex ) {
    if( layerIndexIsValidRange(userState, layerIndex) ) {
      Layer toRemove = userState.layers.get( layerIndex );
      
      // Make sure if we're removing the active layer that it get's set to null 
      //  before removing it, so we can later try to set it back to one of the other layers. 
      if( userState.activeLayer == toRemove )
        userState.activeLayer = null;
        
      userState.layers.remove(toRemove);
      
      // If we deleted the active layer, then we should activate the next
      //  top layer if it exists
      if( userState.activeLayer == null && userState.layers.size() > 0 )
        userState.activeLayer = userState.layers.get(0);
    }
    
  }
  
  //---------------------------------------------------------------------------------
  private void mergeLayers( MakerScriptState userState, int firstLayerIndex, int secondLayerIndex ) {
    
    // First make sure we received valid indices
    if( layerIndexIsValidRange(userState, firstLayerIndex) &&
        layerIndexIsValidRange(userState, secondLayerIndex ) &&
        layersNotEqual(firstLayerIndex, secondLayerIndex) ) {

      // Add all the line lists from the second layer into the first layer  
      Layer layerFirst  = userState.layers.get( firstLayerIndex );
      Layer layerSecond = userState.layers.get( secondLayerIndex );
            layerFirst.add( layerSecond );
      
      // Remove the second layer, and select the first
      userState.layers.remove( secondLayerIndex );
      userState.activeLayer = layerFirst;
    }
  }
  
  private boolean layerIndexIsValidRange( MakerScriptState userState, int layer ) {
    return ( layer >= 0 && layer < userState.layers.size() );
  }
  
  private boolean layersNotEqual( int layerA, int layerB ) {
    return ( layerA != layerB );
  }

}
