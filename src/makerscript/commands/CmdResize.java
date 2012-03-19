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



public class CmdResize extends Command {
  //---------------------------------------------------------------------------------
  public         CmdResize ( CommandStore cs ) { super( cs, "resize x +float | resize y +float | resize z +float | resize xy +float +float | " +
                                                            "scale x +float | scale y +float | scale z +float | scale xy +float +float" ); }
  public         CmdResize ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdResize(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    if( state.jumpElse || state.jumpEndIf ) return state.nextCommand();
    if( userState.selected == null )        return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return resize( state, params, true,  false, false );
      case 1: return resize( state, params, false, true,  false );
      case 2: return resize( state, params, false, false, true  );
      case 3: return resize( state, params, true,  true,  false );
      case 4: return scale ( state, params, true,  false, false );
      case 5: return scale ( state, params, false, true,  false );
      case 6: return scale ( state, params, false, false, true  );
      case 7: return scale ( state, params, true,  true,  false );
    }
    
    return state.nextCommand();
  }  
  
  // ------------------------------------------------------------------------ //
  public int resize( ScriptState state, Queue<ExpressionElement> params, boolean x, boolean y, boolean z )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    Vector3             newSize   = new Vector3();
    
    if( x ) newSize.x = popFloat(params);
    if( y ) newSize.y = popFloat(params);
    if( z ) newSize.z = popFloat(params);
    
    // First calculate/retrieve the bounds of the selection
    AABounds<Vector3> bounds = userState.getSelectionBounds();

    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
      {
        if( x ) v.x = MathHelper.map( v.x, bounds.vMin.x, bounds.vMax.x, bounds.vMin.x, bounds.vMin.x + newSize.x );
        if( y ) v.y = MathHelper.map( v.y, bounds.vMin.y, bounds.vMax.y, bounds.vMin.y, bounds.vMin.y + newSize.y );
        if( z ) v.z = MathHelper.map( v.z, bounds.vMin.z, bounds.vMax.z, bounds.vMin.z, bounds.vMin.z + newSize.z );
      }
    
    return state.nextCommand();
  }
  
  // ------------------------------------------------------------------------ //
  public int scale( ScriptState state, Queue<ExpressionElement> params, boolean x, boolean y, boolean z  )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    Vector3             scalar    = new Vector3();
    
    if( x ) scalar.x = popFloat(params);
    if( y ) scalar.y = popFloat(params);
    if( z ) scalar.z = popFloat(params);
    
    // First calculate/retrieve the bounds of the selection
    AABounds<Vector3> bounds   = userState.getSelectionBounds();
    Vector3       old_size = bounds.vMax.sub( bounds.vMin );
    for( Selectable item : userState.selected )
      for( Vertex v : item.getVerts() )
      {
        if( x ) v.x = MathHelper.map( v.x, bounds.vMin.x, bounds.vMax.x, bounds.vMin.x, bounds.vMin.x + old_size.x*scalar.x );
        if( y ) v.y = MathHelper.map( v.y, bounds.vMin.y, bounds.vMax.y, bounds.vMin.y, bounds.vMin.y + old_size.y*scalar.y );
        if( z ) v.z = MathHelper.map( v.z, bounds.vMin.z, bounds.vMax.z, bounds.vMin.z, bounds.vMin.z + old_size.z*scalar.z );
      }
    
    return state.nextCommand();
  }
  

}
