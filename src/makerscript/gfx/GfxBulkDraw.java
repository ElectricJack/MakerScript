package makerscript.gfx;

import javax.media.opengl.GL;

public class GfxBulkDraw
{
  // ------------------------------------------------------------------------------------------ //
  public static GfxDrawState begin( GL gl, boolean bTextures, boolean bNormals, boolean bIndices, boolean bColors, boolean bLights, boolean bFog )
  {
    GfxDrawState    ds           = new GfxDrawState();
                    ds.gl        = gl;
                    ds.bFog      = bFog;
                    ds.bLights   = bLights;
                    ds.bTextures = bTextures;
                    ds.bNormals  = bNormals;
                    ds.bColors   = bColors;
                    ds.bIndices  = bIndices;
                    
    
    //if( bFog )      fog.Enable    ( ds.gl );
    //if( bLights )   light.Enable  ( ds.gl );

                    ds.gl.glEnableClientState ( GL.GL_VERTEX_ARRAY           );

    
    if( bColors   )
    {
                    ds.gl.glColorMaterial     ( GL.GL_FRONT, GL.GL_DIFFUSE );
                    ds.gl.glEnable            ( GL.GL_COLOR_MATERIAL       );
                    ds.gl.glEnableClientState ( GL.GL_COLOR_ARRAY          );
    }
    
    if( bNormals  ) ds.gl.glEnableClientState ( GL.GL_NORMAL_ARRAY         );
    
    if( bTextures )
    {
                    ds.gl.glEnable            ( GL.GL_TEXTURE_2D           ); 
                    ds.gl.glEnableClientState ( GL.GL_TEXTURE_COORD_ARRAY  );
                    //txStone.Bind              ( ds.gl );
    }
    
    if( bIndices  ) ds.gl.glEnableClientState ( GL.GL_INDEX_ARRAY          );

    return ds;
  }

  // ------------------------------------------------------------------------------------------ //
  public static void end( GfxDrawState ds )
  {
    if( ds.bTextures ) {
                       ds.gl.glDisableClientState( GL.GL_TEXTURE_COORD_ARRAY );
                       ds.gl.glDisable           ( GL.GL_TEXTURE_2D          ); 
    }
    if( ds.bNormals  ) ds.gl.glDisableClientState( GL.GL_NORMAL_ARRAY        );
    if( ds.bColors   ) {
                       ds.gl.glDisable           ( GL.GL_COLOR_MATERIAL      );
                       ds.gl.glEnableClientState ( GL.GL_COLOR_ARRAY         );
    }
    if( ds.bIndices  ) ds.gl.glDisableClientState( GL.GL_INDEX_ARRAY         );
                       ds.gl.glDisableClientState( GL.GL_VERTEX_ARRAY        );
    
    //if( ds.bFog      ) fog.Disable      ( ds.gl );     
    //if( ds.bLights   ) light.Disable    ( ds.gl );
   
    ds.gl  = null;
    ds     = null;
  }
}
