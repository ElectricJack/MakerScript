package makerscript.gfx;

import javax.media.opengl.GL;

import makerscript.geom.Vector2;
import makerscript.geom.Vector3;


import com.sun.opengl.util.BufferUtil;
import java.util.ArrayList;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class GfxDynamicMesh
{
  
  private   FloatBuffer            normals        = null;
  private   FloatBuffer            verts          = null;
  private   FloatBuffer            colors         = null;
  private   FloatBuffer            uvs            = null;
  private   IntBuffer              indices        = null;

  public    ArrayList<GfxTexture>  textures       = null;
  private   ArrayList<GeomElement> elements       = new ArrayList<GeomElement>();  
  private   ArrayList<GeomElement> deadElements   = null;  

  private   int                    activeIndex    = 0;
  private   GeomElement            activeElement  = null;
  private   int                    activeTexture  = -1;
  protected Vector3                minimum        = null;
  protected Vector3                maximum        = null;
  private   boolean                hasFog         = true;
  private   boolean                hasLights      = true;
  private   boolean                hasTextures    = false;
  private   boolean                hasNormals     = true;
  private   boolean                hasColors      = false;  
  private   boolean                hasIndices     = false;
  

  
  // --------------------------------------------------------- //
  private class GeomElement
  {
    public int nType    = 0;
    public int nFirst   = 0;
    public int nTexture = -1;
    
    public int nVertexCount = 0; // Currently using two different counters for debugging reasons
    public int nIndexCount  = 0;
  }
  
  // --------------------------------------------------------- //
  public GfxDynamicMesh( int nMaxVerts, int nMaxInds )
  {
    int nVertFloats  = nMaxVerts * 3;
    int nNormFloats  = nMaxVerts * 3;
    int nColorFloats = nMaxVerts * 4;
    int nUVFloats    = nMaxVerts * 2;
    
    normals = BufferUtil.newFloatBuffer ( nNormFloats  );
    verts = BufferUtil.newFloatBuffer ( nVertFloats  );
     colors = BufferUtil.newFloatBuffer ( nColorFloats );
     indices = BufferUtil.newIntBuffer   ( nMaxInds     );
        uvs = BufferUtil.newFloatBuffer ( nUVFloats    );
    
    for( int i=0; i<nNormFloats;  ++i ) normals.put(0);
    for( int i=0; i<nVertFloats;  ++i ) verts.put(0);  
    for( int i=0; i<nColorFloats; ++i )  colors.put(0);  
    for( int i=0; i<nMaxInds;     ++i )  indices.put(0);
    for( int i=0; i<nUVFloats;    ++i )     uvs.put(0);
    
    normals.rewind();
    verts.rewind();
     indices.rewind();
     colors.rewind();
        uvs.rewind();
  }
  
  
  public Vector3 getMin() { return minimum; }
  public Vector3 getMax() { return maximum; }
  
  // --------------------------------------------------------- //
  public void enable( boolean bTextures, boolean bNormals, boolean bIndices, boolean bColors, boolean bLights ) {
    if( bTextures && textures == null ) {
      textures = new ArrayList<GfxTexture>();
    }
    
    this.hasTextures = bTextures;
    this.hasNormals  = bNormals;
    this.hasColors   = bColors;
    this.hasLights   = bLights;
    this.hasIndices  = bIndices;
  }

  // --------------------------------------------------------- //
  public void reset() {
    clearElements();
    
    normals.rewind();
    verts.rewind();
     indices.rewind();
     colors.rewind();
        uvs.rewind();    
    
    activeIndex    = 0;
    
    minimum       = null;
    maximum       = null;
  }
  
  // --------------------------------------------------------- //
  public GeomElement newElement() {
    if( deadElements != null && deadElements.size() > 0 )
      return (GeomElement)deadElements.remove( deadElements.size()-1 );
 
    return new GeomElement();
  }
  
  // --------------------------------------------------------- //
  public void clearElements() {
    if( deadElements == null ) {
      deadElements = elements;
      elements     = new ArrayList<GeomElement>();
    } else {
      deadElements.addAll( elements );
      elements.clear();
    }
  }

  // --------------------------------------------------------- //
  public void texture( int nIndex ) {
    // Make sure that the index is valid and that we have a current element
    //  to assign this texture to.
    if( nIndex < 0 || nIndex > textures.size() ) return;
    if( activeElement == null )                        return;

    activeElement.nTexture = nIndex;
  }
  
  // --------------------------------------------------------- //
  public void beginVerts() { beginShape(-1); }
  public void endVerts()   { endShape();     }
  
  // --------------------------------------------------------- //
  public void beginShape( int nType ) {
    if( activeElement == null )
    {
      boolean bAddNew = false;
      if( elements.size() > 0 )
      {
        activeElement = (GeomElement)elements.get( elements.size()-1 );
        if( activeElement.nType != nType || 
            nType == GL.GL_POLYGON ||
            nType == GL.GL_LINE_STRIP  ||
            nType == GL.GL_TRIANGLE_STRIP )
          bAddNew = true;
          
      }
      else bAddNew = true;
      
      if( bAddNew )
      {
        activeElement = newElement();
            
        elements.add( activeElement );
      
        activeElement.nType        = nType;
        activeElement.nFirst       = activeIndex;
        activeElement.nVertexCount = 0;
        activeElement.nIndexCount  = 0;
      }
    }
  }

  // --------------------------------------------------------- //
  public void normal( Vector3 vNorm )                     { this.normal( vNorm.x, vNorm.y, vNorm.z ); }
  public void normal( float fNX, float fNY, float fNZ )
  {
    if( hasNormals && normals.position() < normals.limit() )
    {
      normals.put( fNX );
      normals.put( fNY );
      normals.put( fNZ );
    }
  }
  
  // --------------------------------------------------------- //
  public void index( int nVertIndex )
  {
    if( activeElement != null && hasIndices && indices.position() < indices.limit()  )
    {
      indices.put( nVertIndex );
      
      ++activeElement.nIndexCount;
      ++activeIndex;      
    }
  }
  
  // --------------------------------------------------------- //
  public void vertex( Vector3 vPos )                                                   { this.vertex( vPos.x, vPos.y, vPos.z,  0,  0, 255,255,255,255 ); }
  public void vertex( Vector3 vPos,                 Vector2 vTex,       GfxColor col ) { this.vertex( vPos.x, vPos.y, vPos.z, vTex.x, vTex.y, col.fRed, col.fGreen, col.fBlue, col.fAlpha ); }
  public void vertex( float fX, float fY, float fZ, Vector2 vTex,       GfxColor col ) { this.vertex( fX, fY, fZ, vTex.x, vTex.y, col.fRed, col.fGreen, col.fBlue, col.fAlpha ); }
  public void vertex( Vector3 vPos,                 float fU, float fV )               { this.vertex( vPos.x, vPos.y, vPos.z, fU, fV, 255,255,255,255 ); }
  public void vertex( float fX, float fY, float fZ, float fU, float fV, GfxColor col ) { this.vertex( fX, fY, fZ, fU, fV, col.fRed, col.fGreen, col.fBlue, col.fAlpha ); }
  public void vertex( float fX, float fY, float fZ
                    , float fU, float fV
                    , float fR, float fG, float fB, float fA )
  {
    if( activeElement != null )
    {
      // Update min/max of the mesh
      if( minimum == null ) minimum = new Vector3( fX, fY, fZ );
      else               minimum.set( Math.min( minimum.x, fX ), Math.min( minimum.y, fY ), Math.min( minimum.z, fZ ) );
      if( maximum == null ) maximum = new Vector3( fX, fY, fZ );
      else               maximum.set( Math.max( maximum.x, fX ), Math.max( maximum.y, fY ), Math.max( maximum.z, fZ ) );
      

      
      if( hasTextures && uvs.position() < uvs.limit() )
      {
        uvs.put ( fU );
        uvs.put ( fV );
      }
       
      if( hasColors && colors.position() < colors.limit() )
      { 
        colors.put ( fR );
        colors.put ( fG );
        colors.put ( fB );
        colors.put ( fA );
      }
      
      if( verts.position() < verts.limit() )
      {
        // Add the vertex info
        verts.put ( fX );
        verts.put ( fY );
        verts.put ( fZ );
        
        ++activeElement.nVertexCount;
        if( !hasIndices ) ++activeIndex;
      }
    }
  }
  
  // ------------------------------------------------------------------------------------------ //
  public void endShape()
  {
    activeElement = null;
  }

  // ------------------------------------------------------------------------------------------ //
  public void draw( GL gl )
  { 
    GfxDrawState ds = GfxBulkDraw.begin( gl, hasTextures, hasNormals, hasIndices, hasColors, hasLights, hasFog );
    this.draw( ds );
    GfxBulkDraw.end( ds );
  }

  // ------------------------------------------------------------------------------------------ //
  public void draw( GfxDrawState ds )
  {
    verts.rewind();
    colors.rewind();
    normals.rewind();
    uvs.rewind();
    indices.rewind();
    
    
                       ds.gl.glVertexPointer     ( 3, GL.GL_FLOAT, 0, verts );
    if( ds.bTextures ) ds.gl.glTexCoordPointer   ( 2, GL.GL_FLOAT, 0, uvs   );
    if( ds.bNormals  ) ds.gl.glNormalPointer     (    GL.GL_FLOAT, 0, normals );
    if( ds.bColors   ) ds.gl.glColorPointer      ( 4, GL.GL_FLOAT, 0, colors  );    
    
    // Draw all of the elements        
    if( ds.bIndices )
    {
      for( int i=0; i<elements.size(); ++i )
      {
        GeomElement ge = (GeomElement)elements.get(i);
        
        // If we're rendering with textures, handle textures now
        if( ds.bTextures ) handleTextures( ds, ge );
        
        // Skip over vertex info element
        if( ge.nType == -1 ) continue;
        
        // Set the correct index buffer position for this element
        indices.position( ge.nFirst );
        
        // Draw the element
        ds.gl.glDrawElements( ge.nType, ge.nIndexCount, GL.GL_UNSIGNED_INT, indices );  
      }      
    }
    else
    {
      for( int i=0; i<elements.size(); ++i )
      {
        GeomElement ge = (GeomElement)elements.get(i);
        
        // If we're rendering with textures, handle textures now
        if( ds.bTextures ) handleTextures( ds, ge );
        
        // Draw the element
        ds.gl.glDrawArrays( ge.nType, ge.nFirst, ge.nVertexCount );  
      }
    }
  }
  
  // ------------------------------------------------------------------------------------------ //
  private void handleTextures( GfxDrawState ds, GeomElement ge )
  {
    // If this element has a texture
    if( ge.nTexture > -1 ) {
      // And we need to change textures
      if( activeTexture != ge.nTexture && ge.nTexture < textures.size() ) {
        // Save the current texture
        activeTexture = ge.nTexture;
      }
      if( activeTexture > 0 ) {
        // Then bind the texture for the element
        GfxTexture tex = (GfxTexture)textures.get( ge.nTexture );
                   tex.bind( ds.gl );      
      }
    }
  }
}
