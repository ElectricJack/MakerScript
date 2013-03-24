
package makerscript.commands;

import java.util.Queue;
import makerscript.MakerScriptState;

import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;


public class CmdGrid extends Command {
  //---------------------------------------------------------------------------------
  public CmdGrid ( CommandStore cs ) { 
    super( cs,
      "add grid xz +float | add grid xy +float | add grid yz +float | " +
      "set grid +int from +float +float to +float +float | " +
      "hide grid +int | " + 
      "show grid +int "
    );
  }
  public CmdGrid ( Command copy    ) { super( copy ); }
  public Command clone( ) { 
    return new CmdGrid(this);
  }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex ) {
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();

    MakerScriptState userState = (MakerScriptState)state.userState;

    switch( callIndex ) {
      case 0: addGridXZ ( userState, params ); break;
      case 1: addGridXY ( userState, params ); break;
      case 2: addGridYZ ( userState, params ); break;
      case 3: setGrid   ( userState, params ); break;
      case 4: showGrid  ( userState, params ); break;
      case 5: hideGrid  ( userState, params ); break;
    }
    
    return state.nextCommand();
  }
  //---------------------------------------------------------------------------------
  public String describe( int callIndex ) {
    switch( callIndex ) {
      case 0: return "Adds a new grid object aligned on the XZ plane to the scene at the specified Y coordinate.";
      case 1: return "Adds a new grid object aligned on the XY plane to the scene at the specified Z coordinate.";
      case 2: return "Adds a new grid object aligned on the YZ plane to the scene at the specified X coordinate.";
      case 3: return "Sets a specified grid object's extents.";
      case 4: return "Makes a hidden grid object that is already in the scene visible.";
      case 5: return "Hides a grid object that is already in the scene.";
    }

    return "";
  }
  //---------------------------------------------------------------------------------
  void addGridXZ( MakerScriptState state, Queue<ExpressionElement> params ) {
    float y = popFloat(params);
  }
  void addGridXY( MakerScriptState state, Queue<ExpressionElement> params ) {
    float z = popFloat(params);
  }
  void addGridYZ( MakerScriptState state, Queue<ExpressionElement> params ) {
    float x = popFloat(params);
  }
  void setGrid( MakerScriptState state, Queue<ExpressionElement> params ) {
    int   index = popInt(params);
    float a     = popFloat(params);
    float b     = popFloat(params);
    float c     = popFloat(params);
    float d     = popFloat(params);
  }
  void hideGrid( MakerScriptState state, Queue<ExpressionElement> params ) {
    int index = popInt(params);
  }
  void showGrid( MakerScriptState state, Queue<ExpressionElement> params ) {
    int index = popInt(params);
  }

}