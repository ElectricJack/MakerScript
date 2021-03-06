/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */

package makerscript;


//import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
//import javax.media.opengl.GL;
//import javax.media.opengl.glu.*;

import com.fieldfx.math.Vector2;
import com.fieldfx.util.Selectable;


import com.fieldfx.ui.UIManager;
import com.fieldfx.ui.UIButton;
import com.fieldfx.ui.UIWindow;
//import com.fieldfx.gfx.GfxMath;
import com.fieldfx.doc.StringDoc;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;
import peasy.CameraState;
import peasy.PeasyCam;




public class MakerScript extends PApplet {
  private static final long serialVersionUID = 1L;
  
  public  static final int        POINT_EDIT_MODE   = 0x01;
  public  static final int        EDGE_EDIT_MODE    = 0x02; 
  public  static final int        LOOP_EDIT_MODE    = 0x03;
  public  static final int        FACE_EDIT_MODE    = 0x04;
  public  static final int        SOLID_EDIT_MODE   = 0x05;

  

  private MakerScriptState        state;
  private MakerScriptInterpretor  processor;
  private boolean                 shouldRebuild = false;
  private StringDoc               code          = null;

  
  private String                  commandsFilePath;



  public StringDoc getCode() {
    return code;
  }

  // ---------------------------------------------------------------- //
  public static void main(String args[]) {
    PApplet.main(new String[] { "makerscript.MakerScript" });
  }

  // ---------------------------------------------------------------- //
  public void setup () {
    size( screen.width-100, screen.height-100, PGraphicsOpenGL.OPENGL );
    
    frame.setResizable(true);
    frame.setTitle("MakerScript");

    code          = new StringDoc();
    state         = new MakerScriptState( this );
    processor     = new MakerScriptInterpretor( code );
    shouldRebuild = false;

    frameRate( 120 );

    /*
    if( initProject() ) {
      compile();
      execute();
    }*/
    
    state.cam.setActive(false);
  }
  
  // ------------------------------------------------------------------------ //
  public void draw () {
      
    //For one reason or another, calling perspective breaks screen ray / world mouse pos generation.
    perspective( PI/3.0f, (float)width/(float)height, state.nearPlane, state.farPlane );
    
    state.cam.setMinimumDistance(state.nearPlane);
    state.cam.setMaximumDistance(state.farPlane);
    state.cam.feed();
    state.updateGL();

    if( state != null ) {
      rebuild();
      state.draw();
    }
    
    state.cam.beginHUD();
      state.drawGUI();
    state.cam.endHUD();
  }

  // ------------------------------------------------------------------------ //
  public boolean rebuild() {
    if( !shouldRebuild ) return false;

    // Rebuilding destroys the camera state at times because
    //  it uses processing's matrix transformation system when loading meshes. So
    //   we just store the camera state here and restore it if we need to.
    CameraState cs = state.cam.getState();

    compile();
    beginCamera();
      camera();
      execute();
    endCamera();

    state.cam.setState( cs );

    shouldRebuild = false;
    return true;
  }
  
  // ------------------------------------------------------------------------ //
  private void compile() {
    code.clear();
    code.load(commandsFilePath);
    processor.compile();
  }
  
  // ------------------------------------------------------------------------ //
  private void execute() {
    state.reset();
    processor.run( state );
  }

  
  // ------------------------------------------------------------------------ //
  public void mousePressed() {
    if( mouseButton == RIGHT ) shouldRebuild = true;
    if( keyPressed && keyCode == SHIFT ) beginDrag = new Vector2( mouseX, mouseY );
  }
  
  private Vector2 beginDrag = null;
  private Vector2 endDrag   = new Vector2();
  //------------------------------------------------------------------------ //
  public void mouseReleased() {
    if( beginDrag != null ) { 
      float minx = min( beginDrag.x, endDrag.x );
      float maxx = max( beginDrag.x, endDrag.x );
      float miny = min( beginDrag.y, endDrag.y );
      float maxy = max( beginDrag.y, endDrag.y );
      state.selectRegion( minx, miny, maxx, maxy );
      
      java.util.PriorityQueue<Integer> selected_paths = new java.util.PriorityQueue<Integer>();
      for( Selectable sel : state.selected ) {
        if( sel.getType().equals("path") ) {
          selected_paths.add( sel.getIndex() );
        }
      }
      
      if( selected_paths.size() > 0 ) {
  		  List<String> select_lines = new ArrayList<String>();
  		  int cur  = selected_paths.peek();
  		  int from = cur;
  		  for( int pathIndex : selected_paths ){
  			System.out.println(pathIndex);
  		    if( pathIndex - cur != 1 ) {
  		      if( cur - from > 0 ) select_lines.add( "select paths " + from + " " + cur );
  		      else 				   select_lines.add( "select path " + from );
  		    }
  		    cur = pathIndex;
  		  }
  		  saveStrings( "selection.txt", (String[])select_lines.toArray( new String[select_lines.size()]) );
      }
      
      state.clearSelection();

      shouldRebuild = true;
      beginDrag     = null;
    }
  }
  public void mouseMoved() {

  }
  public void mouseDragged()  { 
	if( keyPressed && keyCode == SHIFT && beginDrag != null ) {
	  
	  endDrag.set( mouseX, mouseY );
	  if( state != null ) {
      float minx = min( beginDrag.x, endDrag.x );
      float maxx = max( beginDrag.x, endDrag.x );
      float miny = min( beginDrag.y, endDrag.y );
      float maxy = max( beginDrag.y, endDrag.y );
      state.selectRegion( minx, miny, maxx, maxy );
	  }
	}
  }
  //------------------------------------------------------------------------ //

  public void keyPressed() {
    if( keyCode == ALT ) {
      state.cam.setActive(true);
    }
  }
  //------------------------------------------------------------------------ //
  public void keyReleased() {
    if( keyCode == ALT ) {
      state.cam.setActive(false);
    }
  }


}

