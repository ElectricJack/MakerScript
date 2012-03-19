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

import com.fieldfx.geom.mesh.PolyLine;
import com.fieldfx.geom.mesh.Vertex;
import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;
import com.fieldfx.util.Selectable;

import makerscript.Layer;
import makerscript.MakerScriptState;



public class CmdFlip extends Command {
  //---------------------------------------------------------------------------------
  public         CmdFlip ( CommandStore cs ) { super( cs, "flip | flip stock x | flip stock y" ); }
  public         CmdFlip ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdFlip(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return flipPolyLines ( state, params );
      case 1: return flipStockX    ( state, params );
      case 2: return flipStockY    ( state, params );
    }
    
    return state.nextCommand();
  } 
  
  //---------------------------------------------------------------------------------
  private int flipPolyLines( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    if( userState.selected == null ) return state.nextCommand();
    for( Selectable item : userState.selected )
      if( item instanceof PolyLine ) {
        PolyLine poly = (PolyLine)item;
                 poly.flip();  
      }
    
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int flipStockX( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    for( Layer layer : userState.layers ) {
      for( Vertex v : layer.getVerts() ) {
        v.x = userState.stockSize.x - v.x;
        v.z = userState.stockSize.z - v.z;
      }
    }
    
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------
  private int flipStockY( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    for( Layer layer : userState.layers ) {
      for( Vertex v : layer.getVerts() ) {
        v.y = userState.stockSize.y - v.y;
        v.z = userState.stockSize.z - v.z;
      }
    }
    
    return state.nextCommand();
  }
}
