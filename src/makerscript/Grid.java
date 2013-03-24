/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */

package makerscript;

import com.fieldfx.math.Vector2;
import processing.opengl.PGraphicsOpenGL;

public class Grid {

  Vector2 vMin = new Vector2();
  Vector2 vMax = new Vector2(800,600);
  float   step = 10;

  public Grid() {
  }

  public void draw(PGraphicsOpenGL g) {
    
    int cols       = (int)((vMax.x-vMin.x) / step);
    int rows       = (int)((vMax.y-vMin.y) / step);
    int majorCount = 10;
    
    g.pushStyle();
      g.pushMatrix();
        
        //@Todo handle this in a non-hacky way
        //g.translate( 0,0,-stockSize.z );

        setStyle_GridMinor(g);
        for( int ix=0; ix<=cols; ++ix ) {
          if( ix % majorCount != 0 ) {
            float x = vMin.x + ix*step;
            g.line( x, vMin.y, x, vMax.y );
          }
        }
        for( int iy=0; iy<=rows; ++iy ) {
          if( iy % majorCount != 0 ) {
            float y = vMin.y + iy*step;
            g.line( vMin.x, y, vMax.x, y );
          }
        }
        
        setStyle_GridMajor(g);
        for( int ix=0; ix<=cols; ix += majorCount ) {
          float x = vMin.x + ix*step;
          g.line( x, vMin.y, x, vMax.y );
        }
        for( int iy=0; iy<=rows; iy += majorCount ) {
          float y = vMin.y + iy*step;
          g.line( vMin.x, y, vMax.x, y );
        }
        
      g.popMatrix();
    g.popStyle();
  }
  private void setStyle_GridMinor( PGraphicsOpenGL g ) {
    g.strokeWeight(1);
    g.stroke(200);
  }
  private void setStyle_GridMajor( PGraphicsOpenGL g ) {
    g.strokeWeight(2);
    g.stroke(150);
  }

}