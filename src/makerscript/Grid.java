/*
  MakerScript
  copyright (c) 2011, Jack W. Kern
 */

package makerscript;

public class Grid {
  private void setStyle_GridMinor() { g.strokeWeight(1); g.stroke(200); }
  private void setStyle_GridMajor() { g.strokeWeight(2); g.stroke(150); }

  public Grid() {

  }

  public void draw() {
    float y0   = 0;
    float y1   = 600;
    float x0   = 0;
    float x1   = 800;
    float step = 10; 
    
    int cols       = (int)((x1-x0) / step);
    int rows       = (int)((y1-y0) / step);
    int majorCount = 10;
    
    g.pushStyle();  
      g.pushMatrix();
        g.translate( 0,0,-stockSize.z );
        setStyle_GridMinor();
        for( int ix=0; ix<=cols; ++ix )
          if( ix % majorCount != 0 ) {
            float x = x0 + ix*step;
            g.line( x, y0, x, y1 );
          }
      
        for( int iy=0; iy<=rows; ++iy )
          if( iy % majorCount != 0 ) {
            float y = y0 + iy*step;
            g.line( x0, y, x1, y );
          }
        
        setStyle_GridMajor();
        for( int ix=0; ix<=cols; ix += majorCount ) { float x = x0 + ix*step;  g.line( x, y0, x, y1 ); }
        for( int iy=0; iy<=rows; iy += majorCount ) { float y = y0 + iy*step;  g.line( x0, y, x1, y ); }
        
      g.popMatrix();
    g.popStyle();
  }
}