/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */

package makerscript.commands;

import java.util.ArrayList;
import java.util.Queue;

import makerscript.ScriptableMillState;
import makerscript.geom.Line2;
import makerscript.geom.Vector2;
import makerscript.geom.Vector3;
import makerscript.geom.mesh.PathPoint;
import makerscript.geom.mesh.PolyLine;
import makerscript.geom.mesh.Vertex;
import makerscript.lang.Command;
import makerscript.lang.CommandStore;
import makerscript.lang.ExpressionElement;
import makerscript.lang.LScriptState;
import makerscript.util.Selectable;

//import scriptcam.geom.mesh.PathBridge;


public class CmdMill extends Command {
  //---------------------------------------------------------------------------------
  public         CmdMill ( CommandStore cs ) { super( cs, "mill along paths | mill offset paths +float" ); }
  public         CmdMill ( Command copy    ) { super( copy ); }
  public Command clone   ( )                 { return new CmdMill(this); }
  
  //---------------------------------------------------------------------------------
  public int call( LScriptState state, Queue<ExpressionElement> params, int callIndex )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    if( state.jumpElse || state.jumpEndIf )  return state.nextCommand();
    if( userState.selected == null )         return state.nextCommand();
    
    switch( callIndex ) {
      case 0: return millAlongPaths  ( state, params );
      case 1: return millOffsetPaths ( state, params );
    }
    
    return state.nextCommand();
  }  
  // ------------------------------------------------------------------------ //
  int millAlongPaths( LScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    for( Selectable item : userState.selected ) {
      PolyLine millPath           = new PolyLine();
      
      for( Vertex v : item.getVerts() )
        millPath.add( v.get() );
      
      userState.cncToolPath.add( millPath );
    }
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  int millOffsetPaths( LScriptState state, Queue<ExpressionElement> params ) {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    float   offset = popFloat( params );
    
    for( Selectable item : userState.selected )
    {
      if( item instanceof PolyLine ) {
        PolyLine            path        = (PolyLine)item;
        PolyLine            offsetPath  = getOffsetPath( path, offset );
        userState.cncToolPath.add( offsetPath );
      }
    }
    
    return state.nextCommand();
  }

  //------------------------------------------------------------------------ //
  Vector3 getOffsetVector( Vector3 tangentSeg, float offset )
  {
    Vector2 pathNormalOffset = tangentSeg.getXY();
   
    pathNormalOffset.set( pathNormalOffset.y, -pathNormalOffset.x );  // Get the normal on the x/y plane from the tangent
    pathNormalOffset.nrmeq();                                         // Normalize
    pathNormalOffset.muleq( offset );                                 // Multiply by the offset
   
    return new Vector3( pathNormalOffset.x, pathNormalOffset.y, tangentSeg.z );
  }
  //------------------------------------------------------------------------ //
  Vector3 getPathOffsetPoint( PolyLine path, int index, float offset )
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
    else                 return b.get();
  }
  //------------------------------------------------------------------------ //
  PolyLine getOffsetPath( PolyLine path, float offset ) {
    PolyLine offsetPath  = new PolyLine();

    for( int i=0; i<=path.getVerts().size(); ++i )
      offsetPath.add( getPathOffsetPoint( path, i, offset ) );

    return offsetPath;
  }
  
  
  // ------------------------------------------------------------------------ //
  public int millSpiralDown( LScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
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
      float t     = map( i, 0, segments-1, 0, 1 );
      float angle = 2 * PI * ((float)i / circleSegs);
      
      cutPath.add( new Vertex( location.x + cos( angle ) * radius
                             , location.y + sin( angle ) * radius
                             , location.z + totalDepth * t ) );
    }
    
    userState.cncToolPath.add( cutPath );
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public int millWidePaths( LScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
    float   offset        = popFloat ( params );
    float   spiralRadius  = popFloat ( params ); 

    for( Selectable item : userState.selected )
    {
      // We only care about polylines in the selection
      PolyLine path = (item instanceof PolyLine)? (PolyLine)item : null;
      if( path == null ) continue;
      
      PolyLine offsetPath = getOffsetPath( path, offset );
      PolyLine cutPath    = getWideOffsetPath( offsetPath, spiralRadius );
      userState.cncToolPath.add( cutPath );
    }
    
    return state.nextCommand();
  }
  // ------------------------------------------------------------------------ //
  public PolyLine getWideOffsetPath( PolyLine offsetPath, float spiralRadius )
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
          float     pathDistance     = map(currentIteration, 0, totalIterations, 0, pathLength );  
          float     angle            = map(seg, 0,9, 0, PI*2 );
          PathPoint where            = offsetPath.getAt( pathDistance, pathLineLengths );
          if( where == null ) continue; 
          
          wherePoint.lerp( offsetPath.get(where.index), offsetPath.get(where.index+1), where.t );
          cutPath.add( new Vertex( wherePoint.x + cos(angle)*spiralRadius
                                 , wherePoint.y + sin(angle)*spiralRadius
                                 , wherePoint.z ) );
        }
      }
      
      return cutPath;
  }
  // ------------------------------------------------------------------------ //
  public int pocketPaths( LScriptState state, Queue<ExpressionElement> params )
  {
    // Get the current scriptable mill state from the lscript state
    ScriptableMillState userState = (ScriptableMillState)state.userState;
    
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
            float interpolatedOffset = lerp( currentOffset, currentOffset - cutIncrement, t );
            
            cutPath.add( getPathOffsetPoint( path, i, interpolatedOffset ) );
          }
          currentOffset -= cutIncrement;
          
        } else {
          
          for( int i=0; i<path.getVerts().size(); ++i ) {
            
            float t                  = (float)i / (float)(path.getVerts().size() - 1);
            float interpolatedOffset = lerp( currentOffset, endOffset, t );
            
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