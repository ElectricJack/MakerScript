/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */
package makerscript;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;


import com.fieldfx.math.AABounds;
import com.fieldfx.math.Poly2;
import com.fieldfx.math.Vector2;
import com.fieldfx.math.Vector3;
import com.fieldfx.util.MathHelper;
import com.fieldfx.util.Selectable;
//import makerscript.geom.mesh.Mesh;

import com.fieldfx.geom.mesh.Face;
import com.fieldfx.geom.mesh.PolyLine;
import com.fieldfx.geom.mesh.Vertex;
import com.fieldfx.gfx.GfxMath;
import com.fieldfx.gfx.GfxFrustum;
import com.fieldfx.gfx.GfxViewMatrices;

//import makerscript.gfx.GfxDynamicMesh;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;


public class MakerScriptState {
  // ------------------------------------------------------------------------ //
  private void setStyle_Path               () { g.strokeWeight(2); g.stroke(0,0,0);       }
  private void setStyle_SelectedPath       () { g.strokeWeight(3); g.stroke(255,0,0);     }
  private void setStyle_SelectedPathNormal () { g.strokeWeight(1); g.stroke(255,0,0);     }
  private void setStyle_Targets            () { g.strokeWeight(2); g.stroke(0,200,0);     }
  private void setStyle_ToolPath           () { g.strokeWeight(1); g.stroke(50,100,255);  }

  public  PGraphicsOpenGL   g;
  public  GL                gl;
  public  GLU               glu;
  
  public  PApplet           app;

  public  PeasyCam          cam;
  public  GfxFrustum        frustum;
  public  GfxViewMatrices   matrices;

  public  float             nearPlane =    0.1f;
  public  float             farPlane  = 1000.0f;

  public List<Selectable>   selected;
  public List<PolyLine>     cncToolPath;
  
  public List<Layer>        layers;
  public Layer              activeLayer;

  public List<Grid>         grids;
  
  public Vector3            stockSize = new Vector3();

  // Selection variables
  Vector3                   v0,v1,v2,v3;
  Vector3                   worldMouse;

  
  // Visibility
  protected boolean         gridVisible           = true;
  protected boolean         inactiveLayersVisible = true;
  protected boolean         targetsVisible        = true;
  protected boolean         toolPathsVisible      = true;
  protected boolean         stockVisible          = true;


  public String             projectPath;

  // ------------------------------------------------------------------------ //
  public MakerScriptState( MakerScript app ) {
    this.g        = (PGraphicsOpenGL)app.g;
    this.app      = app;
    this.frustum  = new GfxFrustum();
    this.matrices = new GfxViewMatrices();
    this.cam      = new PeasyCam( app, 300.f );
    //g.hint(PApplet.ENABLE_OPENGL_4X_SMOOTH);
    reset();
  }

  // ------------------------------------------------------------------------ //
  public void reset() {
    activeLayer    = null;
    layers         = new ArrayList<Layer>();
    grids          = new ArrayList<Grid>();
    selected       = new ArrayList<Selectable>();
    cncToolPath    = new ArrayList<PolyLine>();
  }

  //------------------------------------------------------------------------ //
  public void updateGL() {
    gl  = g.beginGL();
    glu = g.glu;
    
    GfxMath.updateMatrices         ( gl, matrices );
    GfxMath.calculateFrustumPlanes ( frustum );
    
    //gl.glEnable                    ( GL.GL_CULL_FACE );
    //gl.glDisable                   ( GL.GL_MULTISAMPLE );
    
    float[] plane = new float[] { 0.f, 0.f, 1.f, 0.f };
    worldMouse = GfxMath.calculateIntersect( glu, matrices, app.mouseX, app.mouseY, app.width, app.height, plane );
    
    g.endGL();
  }
  
  // ------------------------------------------------------------------------ //
  public void draw () {
    if( g == null ) return;

    g.background  ( 255 );
    g.noFill      ( );
    g.smooth();
    
    
    gl = g.beginGL();
    gl.glEnable( GL.GL_DEPTH_TEST );
    g.endGL();

  
    if( worldMouse != null ) {
        g.stroke(0);
        g.pushMatrix();
          g.translate(worldMouse.x, worldMouse.y, worldMouse.z);
          g.box(10);
        g.popMatrix();    	
    }

    if( v0 != null && v1 != null && v2 != null && v3 != null ) {
      g.beginShape();
        g.vertex(v0.x,v0.y,v0.z);
        g.vertex(v1.x,v1.y,v1.z);
        g.vertex(v2.x,v2.y,v2.z);
        g.vertex(v3.x,v3.y,v3.z);
        g.vertex(v0.x,v0.y,v0.z);
      g.endShape();
    }
    
    drawGrids();

    if( inactiveLayersVisible ) drawInactiveLayers();
                                drawActiveLayer();
    if( targetsVisible        ) drawTargets();
    if( toolPathsVisible      ) drawToolPath();
    if( stockVisible          ) drawStock();
                                drawSelections();

  }
  
  public void clearSelection() {
    selected.clear();
    v0 = v1 = v2 = v3 = null;
  }
  
  public void drawGUI() {
    
  }

  //------------------------------------------------------------------------ //
  public void  select ( List< Selectable > selectables ) {
    for( Selectable s : selectables ) {
      select(s);     
    }
  }
  //------------------------------------------------------------------------ //
  public void select( Selectable selectable ) {
    if( !selected.contains( selectable ) ) {
      selected.add( selectable );
    } 
  }

  //------------------------------------------------------------------------ //
  public AABounds<Vector3> getSelectionBounds() {
    if( selected == null ||
        selected.size() == 0 || 
        selected.get(0).getVerts() == null ||
        selected.get(0).getVerts().size() == 0 ) return null;
       
    AABounds<Vector3> bounds = new AABounds<Vector3>( Vector3.class );

    bounds.vMin = selected.get(0).getVerts().get(0).get();
    bounds.vMax = bounds.vMin.get();
    for( Selectable item : selected ) {
      bounds.vMin = item.getMin( bounds.vMin );
      bounds.vMax = item.getMax( bounds.vMax );
    }
   
    return bounds;
  }

  // ------------------------------------------------------------------------ //
  public void selectRegion( float minx, float miny, float maxx, float maxy ) {
    //gl  = g.beginGL();
    if( activeLayer != null ) {
      float[] plane = new float[] {0.f,0.f,1.f,0.f};
      v0 = GfxMath.calculateIntersect( glu, matrices, minx, miny, app.width, app.height, plane );
      v1 = GfxMath.calculateIntersect( glu, matrices, maxx, miny, app.width, app.height, plane );
      v2 = GfxMath.calculateIntersect( glu, matrices, maxx, maxy, app.width, app.height, plane );
      v3 = GfxMath.calculateIntersect( glu, matrices, minx, maxy, app.width, app.height, plane );
    
      if( v0 != null && v1 != null && v2 != null && v3 != null ) {
        Poly2 p = new Poly2();
              p.add( v0.x, v0.y );
              p.add( v1.x, v1.y );
              p.add( v2.x, v2.y );
              p.add( v3.x, v3.y );

        selected.clear();
        for( PolyLine poly : activeLayer.getFaces() ) {
          boolean contained = true;
          for( Vertex v : poly.getVerts() ) {
            if( p.contains( new Vector2(v.x,v.y) ) ) {
              contained = false;
              break;
            }
          }
          if( contained ) {
            selected.add( poly );
          }
        }
      }
    }
  }
  
  //------------------------------------------------------------------------ //
  protected void drawInactiveLayers() {
    
  }
  // ------------------------------------------------------------------------ //
  protected void drawActiveLayer() {
    // Draw the current layer
    setStyle_Path();
    if( activeLayer != null ) activeLayer.draw( g );
  }
  //------------------------------------------------------------------------ //
  protected void drawStock() {
    // Draw the stock
    if( stockSize.len() > 0.001 ) {
      g.stroke( 0 );
      g.strokeWeight( 2 );
      g.pushMatrix();
        g.translate( stockSize.x * 0.5f, stockSize.y * 0.5f, -stockSize.z * 0.5f );
        g.box( stockSize.x, stockSize.y, stockSize.z );
      g.popMatrix();
    }
  }
  // ------------------------------------------------------------------------ //
  protected void drawSelections() {
    gl = g.beginGL();
    gl.glDisable( GL.GL_DEPTH_TEST );
    g.endGL();
      
    g.noFill();
    g.pushMatrix();
    g.scale( 1.f, 1.f, -1.f );
    for( Selectable item : selected )
    {
      // Draw any selected line lists with a thicker red border
      setStyle_SelectedPath();
      g.beginShape();
        for( Vector3 v : item.getVerts() )
          g.vertex( v.x, v.y, v.z );
      g.endShape( PApplet.CLOSE );

      // Draw the normals
      setStyle_SelectedPathNormal();
      for( int i=1; i<item.getVerts().size(); ++i )
      {
        Vector3 v0       = item.getVerts().get(i-1);
        Vector3 v1       = item.getVerts().get(i);
        Vector3 vTangent = v1.sub(v0);
        
        Vector3 vCenter = v0.add( vTangent.mid().x, vTangent.mid().y, vTangent.z*0.5f );
                vTangent.nrmeq().muleq(2);
        g.line( vCenter.x, vCenter.y, vCenter.z,  vCenter.x + vTangent.y, vCenter.y - vTangent.x, vCenter.z );
      }
    }
    g.popMatrix();
  }

  // ------------------------------------------------------------------------ //
  protected void drawTargets() {
    if( activeLayer == null || activeLayer.targets == null || activeLayer.targets.size() == 0 ) return;
    
    float s2 = (float)Math.sqrt(1.f) / 2.f;
      
    setStyle_Targets();
    for( Vector3 target : activeLayer.targets ) {
      g.pushMatrix();
        g.translate(target.x, target.y, target.z);
        
        // Draw the arrow lines
        g.line    ( 0,0,0,   0,   0, -6 );
        g.line    ( 0,0,0, -s2, -s2, -2 );
        g.line    ( 0,0,0, +s2, -s2, -2 );
        g.line    ( 0,0,0, +s2, +s2, -2 );
        g.line    ( 0,0,0, -s2, +s2, -2 );
        
        // Draw the intersecting circle for the arrow head
        g.translate( 0, 0, -2 );
        g.ellipse( 0, 0, 2, 2 );      
      g.popMatrix();
    }
  }

  // ------------------------------------------------------------------------ //
  protected void drawToolPath() {
    //System.out.println( cncToolPath.size() );
    for( PolyLine polyline : cncToolPath ) {
      setStyle_ToolPath();
      g.noFill();
      g.beginShape();
        for( Vector3 v : polyline.getVerts() ) {
          g.vertex( v.x, v.y, v.z ); 
        }
      g.endShape();
    }
  }

  // ------------------------------------------------------------------------ //
  protected void drawGrids() {
    for( Grid grid : grids ) {
      grid.draw(g);
    }
  }
}
