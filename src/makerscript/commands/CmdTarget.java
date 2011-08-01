/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */
package makerscript.commands;

import java.util.ArrayList;
import java.util.Queue;

import makerscript.ScriptableMillState;
import makerscript.geom.Vector3;
import makerscript.geom.mesh.PathPoint;
import makerscript.geom.mesh.PolyLine;
import makerscript.geom.mesh.Vertex;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;
import makerscript.util.Selectable;



public class CmdTarget extends Command {
  //---------------------------------------------------------------------------------
  public         CmdTarget ( CommandStore cs ) { super( cs, "target polyline +int | target center | target centers | target coord +float +float +float | clear target | clear targets" ); }
  public         CmdTarget ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdTarget(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return targetPolyLine ( state, params );
      case 1:
      case 2: return targetCenters  ( state );
      case 3: return targetCoord    ( state, params );
      case 4:
      case 5: return clear          ( state );
    }
    
    return state.nextCommand();
  }  
  
  // ------------------------------------------------------------------------ //
  public int targetPolyLine( LScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    if( userState.selected    == null ) return state.nextCommand();
    // We need an active layer to add the targets to
    if( userState.activeLayer == null ) return state.nextCommand();
    
    float normalizedPathLocation = popFloat(params);
    float offset                 = popFloat(params);
    
      
    // We cannot operate if we don't have a PolyLine
    if( userState.selected.size() == 0 ) return state.nextCommand();
    Selectable item = userState.selected.get(0);
    PolyLine   poly = ( item instanceof PolyLine )? (PolyLine)item : null;
    if( poly == null ) return state.nextCommand();
    

    // Now, first we need to calculate the total PolyLine length, so 
    //  it's possible to figure out what section of the path we are offsetting from
    ArrayList<Float> pathLineLengths = new ArrayList<Float>();  
    float            totalLength     = poly.length(pathLineLengths, true);
    
    // Next, we need to scale the pathLocation by the total length, and
    //  search for the segment that contains the pathLocation
    float distanceToPoint = totalLength * normalizedPathLocation;
    PathPoint where = poly.getAt( distanceToPoint, pathLineLengths );
    if( where != null )
    {
        Vertex  v0       = poly.getVerts().get( where.index );
        Vertex  v1       = poly.getVerts().get( where.index+1 );
        Vector3 vPoint   = new Vector3( lerp( v0.x, v1.x, where.t )
                                      , lerp( v0.y, v1.y, where.t )
                                      , lerp( v0.z, v1.z, where.t ) );            
        Vector3 vTangent = v1.sub(v0);
        Vector3 vNormal  = new Vector3( vTangent.y, -vTangent.x, 0 );
                vNormal.nrmeq();
                vNormal.muleq(offset);
                
        vPoint.inc( vNormal );
        userState.activeLayer.targets.add( vPoint );
    }
    
    return state.nextCommand();
  }
  
  // ------------------------------------------------------------------------ //
  public int targetCenters( LScriptState state )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    if( userState.activeLayer == null ) return state.nextCommand();
    if( userState.selected    == null ) return state.nextCommand();
    
    for( Selectable item : userState.selected )
    {  
      if( item.getVerts() == null || item.getVerts().size() <= 0 ) continue;
      
      // Let's create a copy of a point that's actually part of the selection so
      //  that pathMin/Max behave correctly
      Vector3 itemPt0 = item.getVerts().get(0).get();
      Vector3 itemMin = item.getMin( itemPt0.get() );
      Vector3 itemMax = item.getMax( itemPt0 );
      
      // Now we calculate the selected item's center and add it as a target
      Vector3 itemCenter = itemMin.inc( itemMax.sub(itemMin).muleq(0.5f) );
      userState.activeLayer.targets.add( itemCenter );
    }
    
    return state.nextCommand();
  }
  
  // ------------------------------------------------------------------------ //
  public int targetCoord( LScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( userState.activeLayer == null )
      return state.nextCommand();
    
    float x = popFloat(params);
    float y = popFloat(params);
    float z = popFloat(params);
    
    userState.activeLayer.targets.add( new Vector3(x,y,z) );
    
    return state.nextCommand();
  }
  
  // ------------------------------------------------------------------------ //
  public int clear( LScriptState state ) {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( userState.activeLayer == null )
      return state.nextCommand();
    
    userState.activeLayer.targets.clear();
    
    return state.nextCommand();
  }
}


