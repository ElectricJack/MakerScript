package makerscript.gfx;

import javax.media.opengl.GL;

import makerscript.geom.Vector3;

class GfxLight
{
  private GfxColor  ambi        = new GfxColor( 10.f, 10.f, 10.f );
  private GfxColor  diff        = new GfxColor( 255.f );
  private GfxColor  spec        = new GfxColor( 0.f   );
  
  private int       nIndex      = 0;
  private Vector3   vLightDir   = new Vector3( 0.f, -0.3f, 0.7f );
  private int       nLightType  = 0;
  
  // -------------------------------------------------------------------------------------- //
  public      GfxLight         ( )               { vLightDir.nrmeq(); }
  
  // -------------------------------------------------------------------------------------- //
  public void setAmbientColor  ( GfxColor col )  { ambi.set( col ); }
  public void setDiffuseColor  ( GfxColor col )  { diff.set( col ); }
  public void setSpecularColor ( GfxColor col )  { spec.set( col ); }
  
  // -------------------------------------------------------------------------------------- //
  public void init             ( GL gl )         { gl.glEnable  ( GL.GL_LIGHTING ); }
  public void term             ( GL gl )         { gl.glDisable ( GL.GL_LIGHTING ); }
   
  // -------------------------------------------------------------------------------------- //
  public void enable( GL gl ) {
    gl.glEnable  ( GL.GL_LIGHT0 + nIndex );    
    gl.glLightfv ( GL.GL_LIGHT0 + nIndex, GL.GL_AMBIENT,  ambi.get(),  0 );
    gl.glLightfv ( GL.GL_LIGHT0 + nIndex, GL.GL_DIFFUSE,  diff.get(),  0 );
    gl.glLightfv ( GL.GL_LIGHT0 + nIndex, GL.GL_SPECULAR, spec.get(),  0 );
    
    if( nLightType == 0 )
    {
      float[] fPosition = new float[] { vLightDir.x, vLightDir.y, vLightDir.z, 0 };
      gl.glLightfv ( GL.GL_LIGHT0 + nIndex, GL.GL_POSITION, fPosition, 0 );   
    }
  }
  
  // -------------------------------------------------------------------------------------- //
  public void disable( GL gl ) {
    gl.glDisable( GL.GL_LIGHT0 + nIndex );
  }
}
