package makerscript.commands;

import java.util.Queue;

import makerscript.ScriptableMillState;
import makerscript.geom.AABB;
import makerscript.geom.Vector3;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;



public class CmdView extends Command {
  //---------------------------------------------------------------------------------
  public         CmdView ( CommandStore cs ) { super( cs, "view top | view front | view right | view lock | view unlock | view selected | view +float +float +float | view distance +float" ); }
  public         CmdView ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdView(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {   
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    switch( callIndex ) {
      case 0: viewTop      ( userState );          break;
      case 1: viewFront    ( userState );          break;
      case 2: viewRight    ( userState );          break;
      case 3: viewLock     ( userState );          break;
      case 4: viewUnlock   ( userState );          break;
      case 5: viewSelected ( userState );          break;
      case 6: viewPosition ( userState, params );  break;
      case 7: viewDistance ( userState, params );  break;
    }
    
    return state.nextCommand();
  }
  
  //---------------------------------------------------------------------------------  
  public void viewTop( ScriptableMillState userState ) {
    //userState.cam.setRotations(0,0,0);
  }
  //---------------------------------------------------------------------------------  
  public void viewFront( ScriptableMillState userState ) {
    //userState.cam.setRotations(PI/2,0,0);
  }
  //---------------------------------------------------------------------------------  
  public void viewRight( ScriptableMillState userState ) {
    //userState.cam.setRotations(0,PI/2,0);
  }
  //---------------------------------------------------------------------------------  
  public void viewLock( ScriptableMillState userState ) {
    //userState.cam.setActive(false);
  }
  //---------------------------------------------------------------------------------  
  public void viewUnlock( ScriptableMillState userState ) {
    //userState.cam.setActive(true);
  }
  //---------------------------------------------------------------------------------  
  public void viewSelected( ScriptableMillState userState ) {
    
    AABB<Vector3> bounds   = userState.getSelectionBounds();
    if( bounds == null ) return;
    
    Vector3       center   = bounds.vMax.add( bounds.vMin ).mul( 0.5f );
    float         distance = bounds.vMax.sub( bounds.vMin ).len() * 0.5f;
    userState.cam.lookAt( center.x, center.y, center.z, distance );
  }
  //---------------------------------------------------------------------------------  
  public void viewPosition( ScriptableMillState userState, Queue<ExpressionElement> params ) {
    
    float x = popFloat(params);
    float y = popFloat(params);
    float z = popFloat(params);
    
    userState.cam.lookAt( x, y, z );
  }
  //---------------------------------------------------------------------------------  
  public void viewDistance( ScriptableMillState userState, Queue<ExpressionElement> params ) {    
    userState.cam.setDistance( popFloat(params) );
  }
}


