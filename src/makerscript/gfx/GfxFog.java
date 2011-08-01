package makerscript.gfx;

import javax.media.opengl.GL;

class GfxFog
{
  private GfxColor  fogColor    = new GfxColor(255.f); //200,210,255
  private float     fogDensity  = 0.000595f;
  
  public void init( GL gl ) {}
  public void term( GL gl ) {}
  
  public void enable( GL gl )
  {
    gl.glEnable ( GL.GL_FOG );
    gl.glFogi   ( GL.GL_FOG_MODE,    GL.GL_EXP2  );
    gl.glFogf   ( GL.GL_FOG_DENSITY, fogDensity );
    gl.glFogfv  ( GL.GL_FOG_COLOR,   fogColor.get(), 0 );
  }
  
  public void disable( GL gl )
  {
    gl.glDisable( GL.GL_FOG );  
  }
}
