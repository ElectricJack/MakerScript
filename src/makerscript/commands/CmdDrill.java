/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
 */

package makerscript.commands;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

import makerscript.MakerScriptState;

import com.fieldfx.geom.mesh.PolyLine;
import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;
import com.fieldfx.math.Vector3;


//import scriptcam.geom.mesh.PathBridge;


public class CmdDrill extends Command {
  //---------------------------------------------------------------------------------
  public         CmdDrill ( CommandStore cs ) { super( cs, "drill targets" ); }
  public         CmdDrill ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdDrill(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();

    switch( callIndex ) {
      case 0: return drillTargets( state, params );
    }
    
    return state.nextCommand();
  }  

  
  //------------------------------------------------------------------------ //
  public int drillTargets( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    if( userState.activeLayer == null ) return state.nextCommand();
      
    float drillDepth      = -userState.stockSize.z;
    float drillIncrement  = 1.0f;
    int   targetIndex     = 0;
    
    List<Vector3> targets = userState.activeLayer.targets;
    for( Vector3 drillTarget : targets )
    {
      PolyLine drillPath = new PolyLine();
               drillPath.setName( "Drilling target " + targetIndex );
                              drillTarget.z = 0; //@TODO: Figure out why this target's z is so high?
               drillPath.add( drillTarget );
         
      // Create the drill path
      float  currentDepth = 0; // 0 is always the surface of the stock
      while( currentDepth > drillDepth )
      {
        if( currentDepth - drillIncrement < drillDepth ) currentDepth  = drillDepth;      // Add a the final point and we're done
        else                                             currentDepth -= drillIncrement;  // Cut down to the next depth
    
        drillPath.add( new Vector3( drillTarget.x, drillTarget.y, currentDepth ) );

        userState.cncToolPath.add( drillPath );
        
        Vector3                    recedeFrom = drillPath.getVerts().get(1);
        Vector3                    recedeTo   = drillTarget.get();
        
        PolyLine                   recedePath = new PolyLine();
                                   recedePath.setName( "Receding" );
                                   recedePath.add( recedeFrom );
                                   recedePath.add( recedeTo );
        userState.cncToolPath.add( recedePath );
        
       drillPath = new PolyLine();
       drillPath.setName( "Drilling" );
       drillPath.add( drillTarget );
      }
      
      ++targetIndex;
    }
    
    return state.nextCommand();
  }

}
