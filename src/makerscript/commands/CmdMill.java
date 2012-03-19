/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */

package makerscript.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import makerscript.MakerScriptState;

import com.fieldfx.geom.mesh.Edge;
import com.fieldfx.geom.mesh.PathPoint;
import com.fieldfx.geom.mesh.PolyLine;
import com.fieldfx.geom.mesh.Vertex;
import com.fieldfx.gfx.GfxMath;
import com.fieldfx.lang.Command;
import com.fieldfx.lang.CommandStore;
import com.fieldfx.lang.ExpressionElement;
import com.fieldfx.lang.ScriptState;

import com.fieldfx.math.Line2;
import com.fieldfx.math.LineSeg2;
import com.fieldfx.math.Poly2;
import com.fieldfx.math.Vector2;
import com.fieldfx.math.Vector3;

import com.fieldfx.util.MathHelper;
import com.fieldfx.util.Selectable;


//import scriptcam.geom.mesh.PathBridge;


public class CmdMill extends Command {
  //---------------------------------------------------------------------------------
  public         CmdMill ( CommandStore cs ) { super( cs, "mill along paths | mill offset paths +float" ); }
  public         CmdMill ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdMill(this); }
  
  //---------------------------------------------------------------------------------
  public int call( ScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    if( userState.selected == null )         return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return millAlongPaths  ( state, params );
      case 1: return millOffsetPaths ( state, params );
    }
    
    return state.nextCommand();
  }  
  // ------------------------------------------------------------------------ //
  private int millAlongPaths( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    for( Selectable item : userState.selected ) {
      PolyLine millPath           = new PolyLine();
      
      for( Vertex v : item.getVerts() )
        millPath.add( v.get() );
      
      userState.cncToolPath.add( millPath );
    }
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private int millOffsetPaths( ScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float   offset = popFloat( params );
    
    for( Selectable item : userState.selected )
    {
      if( item instanceof PolyLine ) {
        PolyLine            path        = (PolyLine)item;
        PolyLine            offsetPath  = getOffsetPath( userState, path, offset );
        userState.cncToolPath.add( offsetPath );
      }
    }
    
    return state.nextCommand();
  }


  //------------------------------------------------------------------------ //
  private PolyLine getOffsetPath( MakerScriptState userState, PolyLine path, float offset ) {
    PolyLine offsetPath  = new PolyLine();

    Poly2 poly = new Poly2();
    float z    = 0;
    for( Vertex v : path.getVerts() ) {
      poly.add( v.getXY() );
      z = v.z;
    }
    
    userState.gl  = userState.g.beginGL();
    userState.glu = userState.g.glu;
    

    poly = poly.offset( userState.glu, offset, 7 ).get(0);

    userState.g.endGL();
    

    for( Vector2 v : poly.points )
      offsetPath.add( new Vector3( v, z ) );
    
    return offsetPath;
    
  }
  
  
  
  //------------------------------------------------------------------------ //
  private Vector3 getPathOffsetPoint( PolyLine path, int index, float offset )
  {
    Vector3 a,b,c,d;
     
    // We need to select four points along the polygon at index i (none of which can be equal)
    a = path.get( index-1 ).get();
    b = path.get( index ).get();

    for( int ii = index-1; b.sub(a).len() < 0.01; --ii )
    {
      a = path.get( ii-1 ).get();
      b = path.get( ii ).get();
    }
    c = path.get( index ).get();
    
    d = path.get( index+1 ).get();
    for( int ii = index+1; d.sub(c).len() < 0.01; ++ii )
    {
      c = path.get( ii ).get();
      d = path.get( ii+1 ).get();
    }
     
    //Point order:  a-b c-d
    //Start seg:   (b-a)
    //End seg:     (d-c)
    // Calculate the offsets for the two line segments
    Vector3 offset1 = getOffsetVector( b.sub(a), offset );
    Vector3 offset2 = getOffsetVector( d.sub(c), offset );
     
    // Update our four points by the offsets
    a.inc( offset1 );
    b.inc( offset1 );
    c.inc( offset2 );
    d.inc( offset2 );
     
    // Check for intersection between the line segments
    Line2    line2  = new Line2( c.getXY(), d.getXY() );
    Vector2  result = (new Line2( a.getXY(), b.getXY() )).intersect( line2 );
     
    if( result != null ) return new Vector3( result.x, result.y, b.z );
    else                 return b;
  }
  
  //------------------------------------------------------------------------ //
  private Vector3 getOffsetVector( Vector3 tangentSeg, float offset )
  {
    Vector2 pathNormalOffset = tangentSeg.getXY();
   
    pathNormalOffset.set( pathNormalOffset.y, -pathNormalOffset.x );  // Get the normal on the x/y plane from the tangent
    pathNormalOffset.nrmeq();                                         // Normalize
    pathNormalOffset.muleq( offset );                                 // Multiply by the offset
   
    return new Vector3( pathNormalOffset.x, pathNormalOffset.y, tangentSeg.z );
  }
  
  
  // ------------------------------------------------------------------------ //
  @SuppressWarnings("unused")
  private int millSpiralDown( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    int   targetIndex    = popInt   ( params );
    float radius         = popFloat ( params );
    float depthIncrement = popFloat ( params );
    float totalDepth     = popFloat ( params );
    
    if( userState.activeLayer == null ) return state.nextCommand();
    if( targetIndex < 0 || targetIndex >= userState.activeLayer.targets.size() ) return state.nextCommand();
    
    int       circleSegs        = 16;
    PolyLine  cutPath           = new PolyLine();
    
    Vector3 location    = userState.activeLayer.targets.get( targetIndex );
    int     segments    = (int)( circleSegs * totalDepth / depthIncrement );
    
    for( int i=0; i<segments; ++i )
    {
      float t     = MathHelper.map( i, 0, segments-1, 0, 1 );
      float angle = 2 * MathHelper.PI * ((float)i / circleSegs);
      
      cutPath.add( new Vertex( location.x + MathHelper.cos( angle ) * radius
                             , location.y + MathHelper.sin( angle ) * radius
                             , location.z + totalDepth * t ) );
    }
    
    userState.cncToolPath.add( cutPath );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  @SuppressWarnings("unused")
  private int millWidePaths( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float   offset        = popFloat ( params );
    float   spiralRadius  = popFloat ( params ); 

    for( Selectable item : userState.selected )
    {
      // We only care about polylines in the selection
      PolyLine path = (item instanceof PolyLine)? (PolyLine)item : null;
      if( path == null ) continue;
      
      PolyLine offsetPath = getOffsetPath( userState, path, offset );
      PolyLine cutPath    = getWideOffsetPath( offsetPath, spiralRadius );
      userState.cncToolPath.add( cutPath );
    }
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  private PolyLine getWideOffsetPath( PolyLine offsetPath, float spiralRadius )
  {
      // Get all the segment lengths
      ArrayList<Float> pathLineLengths = new ArrayList<Float>();  
      float            pathLength      = offsetPath.length( pathLineLengths, false );
      int              spiralCount     = (int)( pathLength / spiralRadius );
      int              totalIterations = spiralCount * 9;
      Vector3          wherePoint      = new Vector3();
      
      //println( pathLength + " : " + offsetPath.length() );
      
      PolyLine  cutPath = new PolyLine();
                //cutPath.objName   = offsetPath.objName;
                //cutPath.feedRate  = offsetPath.feedRate;

     
      for( int i=0; i<spiralCount; ++i ) {
        for( int seg=0; seg<9; ++seg ) {
          
          int       currentIteration = i*9 + seg;
          float     pathDistance     = MathHelper.map(currentIteration, 0, totalIterations, 0, pathLength );  
          float     angle            = MathHelper.map(seg, 0,9, 0, MathHelper.PI*2 );
          PathPoint where            = offsetPath.getAt( pathDistance, pathLineLengths );
          if( where == null ) continue; 
          
          wherePoint.lerp( offsetPath.get(where.index), offsetPath.get(where.index+1), where.t );
          cutPath.add( new Vertex( wherePoint.x + MathHelper.cos(angle)*spiralRadius
                                 , wherePoint.y + MathHelper.sin(angle)*spiralRadius
                                 , wherePoint.z ) );
        }
      }
      
      return cutPath;
  }
  // ------------------------------------------------------------------------ //
  @SuppressWarnings("unused")
  private int pocketPaths( ScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    MakerScriptState userState = (MakerScriptState)state.userState;
    
    float cutIncrement = popFloat(params);
    float startOffset  = popFloat(params);
    float endOffset    = popFloat(params);
    
    for( Selectable item : userState.selected )
    {  
      
      PolyLine path     = (item instanceof PolyLine)? (PolyLine)item : null;
      if( path == null ) continue;
      
      PolyLine cutPath  = new PolyLine();
               //cutPath.objName   = path.objName;
               //cutPath.feedRate  = path.feedRate;
             
      // First create the tool path, offseting by the toolDiameter
      float currentOffset = startOffset;
      while( currentOffset > endOffset ) {
        
        if( currentOffset - cutIncrement > endOffset ) {
          for( int i=0; i<path.getVerts().size(); ++i ) {
            
            float t                  = (float)i / (float)(path.getVerts().size() - 1);
            float interpolatedOffset = MathHelper.lerp( currentOffset, currentOffset - cutIncrement, t );
            
            cutPath.add( getPathOffsetPoint( path, i, interpolatedOffset ) );
          }
          currentOffset -= cutIncrement;
          
        } else {
          
          for( int i=0; i<path.getVerts().size(); ++i ) {
            
            float t                  = (float)i / (float)(path.getVerts().size() - 1);
            float interpolatedOffset = MathHelper.lerp( currentOffset, endOffset, t );
            
            cutPath.add( getPathOffsetPoint( path, i, interpolatedOffset ) );
          }
          
          for( int i=0; i<path.getVerts().size(); ++i )
            cutPath.add( getPathOffsetPoint( path, i, endOffset ) );
            
          currentOffset = endOffset;
        }
      }
      
      userState.cncToolPath.add( cutPath );
    }
    
    return state.nextCommand();
  }
}


/*
// ------------------------------------------------------------------------ //
public ArrayList<PolyLine> getBridgeClippedPaths( PolyLine path, ArrayList<PathBridge> bridges )
{
  ArrayList<PolyLine> offsetPaths        = new ArrayList<PolyLine>();
  ArrayList<PolyLine> bridgeClippedPaths = new ArrayList<PolyLine>();
                      offsetPaths.add( path );
  
  if( bridges.size() > 0 ) {
    for( PathBridge bridge : bridges ) {
      
      bridgeClippedPaths.clear();

      for( PolyLine toClip : offsetPaths )
        bridgeClippedPaths.addAll( getBridgeClippedPaths( toClip, bridge ) );

      offsetPaths.clear();
      offsetPaths.addAll( bridgeClippedPaths );
      
    }
    return bridgeClippedPaths;
  }
  return offsetPaths;
}

// ------------------------------------------------------------------------ //
public ArrayList<PolyLine> getBridgeClippedPaths( PolyLine path, PathBridge bridge )
{

  ArrayList<PolyLine> offsetPaths = new ArrayList<PolyLine>();
  PolyLine            offsetPath  = new PolyLine();
  if( bridge == null ) {
           offsetPaths.add( path );
    return offsetPaths;
  }

  for( int i=0; i<path.getVerts().size(); ++i )
  {
    Vertex currentPoint = offsetPath.getVerts().size() > 0? offsetPath.getVerts().get( offsetPath.getVerts().size()-1 ) : null;
    Vertex nextPoint    = path.getVerts().get(i);
    
    if( currentPoint == null ) offsetPath.add( nextPoint );
    else // If we have current point and next point
    {
      // If current point and next point are on the same side of either line, we don't want to do the intersection
      boolean curInA  = bridge.bridgeLineA.inside( currentPoint.getXY() );
      boolean nextInA = bridge.bridgeLineA.inside( nextPoint.getXY() );
      boolean curInB  = bridge.bridgeLineB.inside( currentPoint.getXY() );
      boolean nextInB = bridge.bridgeLineB.inside( nextPoint.getXY() );
      
      if( nextInA && nextInB ) continue;
      if( ( curInA && nextInA ) || ( curInB && nextInB ) )
      {
        offsetPath.getVerts().add( nextPoint );
        continue;
      }
      
      LineSeg2 testSeg = new LineSeg2( currentPoint.getXY(), nextPoint.getXY() );
      Vector2  resultA = Geometry.intersection( testSeg, bridge.bridgeLineA );
      Vector2  resultB = Geometry.intersection( testSeg, bridge.bridgeLineB );
      
      
      if( resultA != null && resultB != null )
      {
        float sqrdDistToResultA = currentPoint.getXY().sub( resultA ).lenlen();
        float sqrdDistToResultB = currentPoint.getXY().sub( resultB ).lenlen();
        
        PolyLine nextPath = new PolyLine();
        if( sqrdDistToResultA < sqrdDistToResultB ) {
          offsetPath.add ( new Vertex( resultA.x, resultA.y, currentPoint.z ) ); // TODO: These Z values should be interpolated as well
          nextPath.add   ( new Vertex( resultB.x, resultB.y, nextPoint.z    ) ); // TODO: These Z values should be interpolated as well
        } else {
          offsetPath.add ( new Vertex( resultB.x, resultB.y, currentPoint.z ) ); // TODO: These Z values should be interpolated as well
          nextPath.add   ( new Vertex( resultA.x, resultA.y, nextPoint.z    ) ); // TODO: These Z values should be interpolated as well
        }
        nextPath.add( nextPoint );
        
        offsetPaths.add( offsetPath );
        offsetPath = nextPath;
      }
    }
  }
  offsetPaths.add( offsetPath );
  
  return offsetPaths;
}*/


//------------------------------------------------------------------------ //
/*
//------------------------------------------------------------------------ //
String roundToStr( float x ) {
  return Float.toString( round(x * 1000.0f) / 1000.0f );
}








} */