/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
 */

package makerscript.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import makerscript.ScriptableMillState;
import makerscript.geom.Vector3;
import makerscript.geom.mesh.PolyLine;
import makerscript.geom.mesh.Vertex;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;


public class CmdExport extends Command {
  //---------------------------------------------------------------------------------
  public         CmdExport ( CommandStore cs ) { super( cs, "export gcode +string" ); }
  public         CmdExport ( Command copy    ) { super( copy ); }
  public Command clone     ( )                 { return new CmdExport(this); }
  
  // --------------------------------------------------------------------------------- //
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    if( userState.selected == null )         return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return exportGCode ( state, params );
    }
    
    return state.nextCommand();
  } 
  
  // --------------------------------------------------------------------------------- //
  int exportGCode( LScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    float epsilon = 0.001f;
    
    
    String      fileName  = popString(params);
    String      feedRate  = "600";
    float       jogHeight = 5.f;
    
    PrintWriter out;
    try {
      FileOutputStream os   = new FileOutputStream( new File( fileName ) );
                       out  = new PrintWriter( os );
    } catch( Exception e ) { return state.nextCommand(); }
    

    
    out.println( "%"   );                               // Program start
    out.println( "G21 (Using millimeters)" );           // We're using millimeters
    out.println( "G90 (Using absolute coordinates)" );  // Set to absolute coordinates
    out.println( "G0 Z"+roundToStr(jogHeight) );                    // Jog up to correct Z height
    
    Vector3 lastCNCPosition = new Vector3(0,0,jogHeight);
    
    boolean first = true;
    for( PolyLine path : userState.cncToolPath ) {
      out.println( "\n(Path '" + path.getName() + "' )" );
            
      // Jog to next path ( And make sure it's valid first )
      List<Vertex> verts = path.getVerts();
      if( verts.size() > 0 ) {
        Vertex v = verts.get(0);
        // If the location of the next cut is different than our last location, then we actually need to jog
        if( v.sub(lastCNCPosition).len() > epsilon ) {
          // Jog up, but only if this is not the first time through
          if( !first ) {
            out.println( "G0 Z" + roundToStr(jogHeight) );
            lastCNCPosition.z = jogHeight;
          }
          // Jog over
          out.println( "G0 X"+roundToStr(v.x)+" Y"+roundToStr(v.y) );
          lastCNCPosition.x = v.x;
          lastCNCPosition.y = v.y;
        }
      }
      
      //out.println( "F"+l.feedRate );
      for( Vector3 v : path.getVerts() )
      {
        if( lastCNCPosition.sub(v).len() < epsilon ) continue;
        
        boolean need_x = Math.abs( lastCNCPosition.x - v.x ) > epsilon;
        boolean need_y = Math.abs( lastCNCPosition.y - v.y ) > epsilon;
        boolean need_z = Math.abs( lastCNCPosition.z - v.z ) > epsilon;
        
        lastCNCPosition.set( v );
        
        String x = roundToStr( v.x );
        String y = roundToStr( v.y );
        String z = roundToStr( v.z );
        
                     out.print( "G1 F"+feedRate );
        if( need_x ) out.print( " X"+x );
        if( need_y ) out.print( " Y"+y );
        if( need_z ) out.print( " Z"+z );
        out.print( "\n" );        
      }
      
      first = false;
    }
    out.println( "M30" ); // Done
    
    out.flush();
    out.close();
    
    
    userState.cncToolPath.clear();
    
    return state.nextCommand();
  }
  //------------------------------------------------------------------------ //
  String roundToStr( float x ) {
    return Float.toString( (int)(0.5f + x * 1000.0f) / 1000.0f );
  }

  
  // ------------------------------------------------------------------------ //
  public void jog( Vector3 lastPoint, Vector3 nextPoint, String objectName )
  {
    /*Path jogPath = new Path();
         jogPath.objName = "Jogging to " + objectName;
         jogPath.getVerts().add( lastPoint.get() );
         jogPath.getVerts().add( new Vector3( lastPoint.x, lastPoint.y, 0 ) );
         jogPath.getVerts().add( new Vector3( nextPoint.x, nextPoint.y, 0 ) );
         jogPath.feedRate = jogRate;
    
    cncToolPath.add(jogPath);*/
  }
}
