/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */

package makerscript;


import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.*;

import makerscript.geom.Vector2;
import makerscript.gfx.GfxMath;
import makerscript.util.Selectable;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;
import peasy.CameraState;
import peasy.PeasyCam;


public class ScriptableMill extends PApplet {
  private static final long serialVersionUID = 1L;

/*  private static final int        nF1       = 112;
  private static final int        nF2       = 113;
  private static final int        nF3       = 114;
  private static final int        nF4       = 115;
  private static final int        nF5       = 116;
  private static final int        nF6       = 117;
  private static final int        nF7       = 118;
  private static final int        nF8       = 119;
  private static final int        nF9       = 120;
  private static final int        nF10      = 121;
  private static final int        nF11      = 122;
  private static final int        nF12      = 123;*/
  
  public  static final int        POINT_EDIT_MODE   = 0x01;
  public  static final int        EDGE_EDIT_MODE    = 0x02; 
  public  static final int        LOOP_EDIT_MODE    = 0x03;
  public  static final int        FACE_EDIT_MODE    = 0x04;
  public  static final int        SOLID_EDIT_MODE   = 0x05;
   
  private ScriptableMillState     state;
  private ScriptableMillCompiled  processor;
  

  

  
  private boolean                 shouldRebuild = false;
  //private boolean                 projectLoaded = false;
  
  public static void main(String args[]) {
    PApplet.main(new String[] { "makerscript.ScriptableMill" });
  }
  
  // ---------------------------------------------------------------- //
  public void setState( ScriptableMillState state ) { this.state = state; }
  // ---------------------------------------------------------------- //
  public void setup () {
    size( 800, 600, PGraphicsOpenGL.OPENGL );
    
    frame.setResizable(true);
    frame.setTitle("MakerScript");

    state     = new ScriptableMillState( this );
    state.cam = new PeasyCam( this, 300.f );
    processor = new ScriptableMillCompiled();
    
    frameRate(120);
    
    if( initProject() ) {
      compile();
      execute();
    }

    state.cam.setActive(false);
  }
  
  // ------------------------------------------------------------------------ //
  public void draw () {
      
    //For one reason or another, calling perspective breaks screen ray / world mouse pos generation.
    //perspective( PI/3.0f, (float)width / (float)height, state.nearPlane, state.farPlane );
    
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
    String[] codeLines = loadStrings( state.commandsFilePath );
    processor.compile( codeLines );
  }
  
  // ------------------------------------------------------------------------ //
  private void execute() {
    state.reset();
    processor.run( state );
  }
  
  // ------------------------------------------------------------------------ //
  private boolean initProject()
  {
    String[] setupFile = loadStrings("setup.txt");
    
    boolean result = true;
    for( String line : setupFile ) {
      if( line != null && line.length() > 0 ) {
        
        String[] initTokens = line.split(":");
        
        if( initTokens.length == 2 ) {
          
          String variable = initTokens[0].trim();
          String value    = initTokens[1].trim();
          
          if      ( variable.equals( "project" ) ) result = result && state.setProjectPath( value );
          //else if ( variable.equals( "" ) )
          
          if( !result ) break;
        }
      }
    }

    return result;
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
      beginDrag = null;
    }
  }
  public void mouseMoved()    {}
  public void mouseDragged()  { 
	if( keyPressed && keyCode == SHIFT && beginDrag != null ) {
	  endDrag.set( mouseX, mouseY );
      float minx = min( beginDrag.x, endDrag.x );
      float maxx = max( beginDrag.x, endDrag.x );
      float miny = min( beginDrag.y, endDrag.y );
      float maxy = max( beginDrag.y, endDrag.y );
      state.selectRegion( minx, miny, maxx, maxy );
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

