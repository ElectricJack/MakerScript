package makerscript.gfx;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import makerscript.geom.Vector3;


public class GfxMath
{
  public static GfxViewMatrices  glViewMats = new GfxViewMatrices();

  private static final int n11 =  0, n12 =  4, n13 =  8, n14 = 12,
                           n21 =  1, n22 =  5, n23 =  9, n24 = 13,
                           n31 =  2, n32 =  6, n33 = 10, n34 = 14,
                           n41 =  3, n42 =  7, n43 = 11, n44 = 15;

  // ------------------------------------------------------------------------------------------ //
  public static float distanceToPlane( Vector3 vPoint, float[] plane )
  {
    Vector3 vNorm = new Vector3();
            vNorm.set( plane );
    return  vNorm.dot( vPoint ) + plane[3];  
  }

  // ------------------------------------------------------------------------------------------ //
  public static void updateMatrices( GL gl )                                      { updateMatrices( gl, glViewMats ); }
  public static void updateMatrices( GL gl, GfxViewMatrices mats ) {
    gl.glGetDoublev  ( GL.GL_MODELVIEW_MATRIX,  mats.modelview,  0 );
    gl.glGetDoublev  ( GL.GL_PROJECTION_MATRIX, mats.projection, 0 );
    gl.glGetIntegerv ( GL.GL_VIEWPORT,          mats.viewport,   0 );  
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateLeftPlane( float[] left )                           { calculateLeftPlane( glViewMats, left ); }
  public static void calculateLeftPlane( GfxViewMatrices mats, float[] left )
  {
    double[] mat = new double[16];
    multiplyMatrix44( mat, mats.modelview, mats.projection );
    
    calculateLeftPlane( mat, left );
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateLeftPlane( double[] mat, float[] left )
  {
    left[0]   = (float)( mat[n41] + mat[n11] ); // A = m41 + m11
    left[1]   = (float)( mat[n42] + mat[n12] ); // B = m42 + m12
    left[2]   = (float)( mat[n43] + mat[n13] ); // C = m43 + m13
    left[3]   = (float)( mat[n44] + mat[n14] ); // D = m44 + m14  
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateRightPlane( float[] right )                         { calculateRightPlane( glViewMats, right ); }
  public static void calculateRightPlane( GfxViewMatrices mats, float[] right )
  {
    double[] mat = new double[16];
    multiplyMatrix44( mat, glViewMats.modelview, glViewMats.projection );
    
    calculateRightPlane( mat, right );
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateRightPlane( double[] mat, float[] right )
  {
    right[0]  = (float)( mat[n41] - mat[n11] ); // A = m41 - m11
    right[1]  = (float)( mat[n42] - mat[n12] ); // B = m42 - m12
    right[2]  = (float)( mat[n43] - mat[n13] ); // C = m43 - m13
    right[3]  = (float)( mat[n44] - mat[n14] ); // D = m44 - m14  
  }

  // ------------------------------------------------------------------------------------------ //
  public static void calculateBottomPlane( float[] bottom )                       { calculateBottomPlane( glViewMats, bottom ); }
  public static void calculateBottomPlane( GfxViewMatrices mats, float[] bottom )
  {
    double[] mat = new double[16];
    multiplyMatrix44( mat, mats.modelview, mats.projection );
    
    calculateBottomPlane( mat, bottom );
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateBottomPlane( double[] mat, float[] bottom )
  {
    bottom[0] = (float)( mat[n41] + mat[n21] ); // A = m41 + m21
    bottom[1] = (float)( mat[n42] + mat[n22] ); // B = m42 + m22
    bottom[2] = (float)( mat[n43] + mat[n23] ); // C = m43 + m23
    bottom[3] = (float)( mat[n44] + mat[n24] ); // D = m44 + m24  
  }

  // ------------------------------------------------------------------------------------------ //
  public static void calculateTopPlane( float[] top )                             { calculateTopPlane( glViewMats, top ); }
  public static void calculateTopPlane( GfxViewMatrices mats, float[] top )
  {
    double[] mat = new double[16];
    multiplyMatrix44( mat, mats.modelview, mats.projection );
    
    calculateTopPlane( mat, top );
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateTopPlane( double[] mat, float[] top )
  {
    top[0]    = (float)( mat[n41] - mat[n21] ); // A = m41 - m21
    top[1]    = (float)( mat[n42] - mat[n22] ); // B = m42 - m22
    top[2]    = (float)( mat[n43] - mat[n23] ); // C = m43 - m23
    top[3]    = (float)( mat[n44] - mat[n24] ); // D = m44 - m24  
  }

  // ------------------------------------------------------------------------------------------ //
  public static void calculateNearPlane( float[] near )                           { calculateNearPlane( glViewMats, near ); }
  public static void calculateNearPlane( GfxViewMatrices mats, float[] near )
  {
    double[] mat = new double[16];
    multiplyMatrix44( mat, mats.modelview, mats.projection );
    
    calculateNearPlane( mat, near );
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateNearPlane( double[] mat, float[] near )
  {
    near[0]   = (float)( mat[n41] + mat[n31] ); // A = m41 + m31
    near[1]   = (float)( mat[n42] + mat[n32] ); // B = m42 + m32
    near[2]   = (float)( mat[n43] + mat[n33] ); // C = m43 + m33
    near[3]   = (float)( mat[n44] + mat[n34] ); // D = m44 + m34  
  }

  // ------------------------------------------------------------------------------------------ //
  public static void calculateFarPlane( float[] far )                             { calculateFarPlane( glViewMats, far ); }
  public static void calculateFarPlane( GfxViewMatrices mats, float[] far )
  {
    double[] mat = new double[16];
    multiplyMatrix44( mat, mats.modelview, mats.projection );
    
    calculateFarPlane( mat, far );
  }
  // ------------------------------------------------------------------------------------------ //
  public static void calculateFarPlane( double[] mat, float[] far )
  {
    far[0]    = (float)( mat[n41] - mat[n31] ); // A = m41 - m31
    far[1]    = (float)( mat[n42] - mat[n32] ); // B = m42 - m32
    far[2]    = (float)( mat[n43] - mat[n33] ); // C = m43 - m33
    far[3]    = (float)( mat[n44] - mat[n34] ); // D = m44 - m34  
  }

  // ------------------------------------------------------------------------------------------ //
  public static void multiplyMatrix44( double[] matOut, double[] matA, double[] matB )
  {
    /*
    Matrix4 temp;
    for(int row=0; row<4; ++row)
      for(int col=0; col<4; ++col)
      {
        for(int x=0; x<4; ++x)
          temp[col][row] += m_mat[x][row] * m2[col][x];
      }
    */
    int n11 =  0, n12 =  1, n13 =  2, n14 =  3,
                   n21 =  4, n22 =  5, n23 =  6, n24 =  7,
                   n31 =  8, n32 =  9, n33 = 10, n34 = 11,
                   n41 = 12, n42 = 13, n43 = 14, n44 = 15;

    matOut[n11] = (matA[n11] * matB[n11])  +  (matA[n21] * matB[n12])  +  (matA[n31] * matB[n13])  +  (matA[n41] * matB[n14]);
    matOut[n12] = (matA[n12] * matB[n11])  +  (matA[n22] * matB[n12])  +  (matA[n32] * matB[n13])  +  (matA[n42] * matB[n14]);
    matOut[n13] = (matA[n13] * matB[n11])  +  (matA[n23] * matB[n12])  +  (matA[n33] * matB[n13])  +  (matA[n43] * matB[n14]);
    matOut[n14] = (matA[n12] * matB[n11])  +  (matA[n24] * matB[n12])  +  (matA[n34] * matB[n13])  +  (matA[n44] * matB[n14]);

    matOut[n21] = (matA[n11] * matB[n21])  +  (matA[n21] * matB[n22])  +  (matA[n31] * matB[n23])  +  (matA[n41] * matB[n24]);
    matOut[n22] = (matA[n12] * matB[n21])  +  (matA[n22] * matB[n22])  +  (matA[n32] * matB[n23])  +  (matA[n42] * matB[n24]);
    matOut[n23] = (matA[n13] * matB[n21])  +  (matA[n23] * matB[n22])  +  (matA[n33] * matB[n23])  +  (matA[n43] * matB[n24]);
    matOut[n24] = (matA[n12] * matB[n21])  +  (matA[n24] * matB[n22])  +  (matA[n34] * matB[n23])  +  (matA[n44] * matB[n24]);

    matOut[n31] = (matA[n11] * matB[n31])  +  (matA[n21] * matB[n32])  +  (matA[n31] * matB[n33])  +  (matA[n41] * matB[n34]);
    matOut[n32] = (matA[n12] * matB[n31])  +  (matA[n22] * matB[n32])  +  (matA[n32] * matB[n33])  +  (matA[n42] * matB[n34]);
    matOut[n33] = (matA[n13] * matB[n31])  +  (matA[n23] * matB[n32])  +  (matA[n33] * matB[n33])  +  (matA[n43] * matB[n34]);
    matOut[n34] = (matA[n12] * matB[n31])  +  (matA[n24] * matB[n32])  +  (matA[n34] * matB[n33])  +  (matA[n44] * matB[n34]);

    matOut[n41] = (matA[n11] * matB[n41])  +  (matA[n21] * matB[n42])  +  (matA[n31] * matB[n43])  +  (matA[n41] * matB[n44]);
    matOut[n42] = (matA[n12] * matB[n41])  +  (matA[n22] * matB[n42])  +  (matA[n32] * matB[n43])  +  (matA[n42] * matB[n44]);
    matOut[n43] = (matA[n13] * matB[n41])  +  (matA[n23] * matB[n42])  +  (matA[n33] * matB[n43])  +  (matA[n43] * matB[n44]);
    matOut[n44] = (matA[n12] * matB[n41])  +  (matA[n24] * matB[n42])  +  (matA[n34] * matB[n43])  +  (matA[n44] * matB[n44]);
    
  }

  // ------------------------------------------------------------------------------------------ //
  public static void calculateFrustumPlanes( GfxFrustum f )                           { calculateFrustumPlanes( glViewMats, f.left, f.right, f.bottom, f.top, f.near, f.far ); }
  public static void calculateFrustumPlanes( GfxViewMatrices matrices, GfxFrustum f ) { calculateFrustumPlanes( matrices, f.left, f.right, f.bottom, f.top, f.near, f.far ); }
  public static void calculateFrustumPlanes( GfxViewMatrices matrices, float[] left, float[] right, float[] bottom, float[] top, float[] near, float[] far )
  {
    double[] mat = new double[16];
    //multiplyMatrix44( mat, glViewMats.modelview, glViewMats.projection );
    multiplyMatrix44( mat, matrices.projection, matrices.modelview );
    
    if( left   != null ) calculateLeftPlane  ( mat, left   );
    if( right  != null ) calculateRightPlane ( mat, right  );
    if( bottom != null ) calculateBottomPlane( mat, bottom );
    if( top    != null ) calculateTopPlane   ( mat, top    );
    if( near   != null ) calculateNearPlane  ( mat, near   );
    if( far    != null ) calculateFarPlane   ( mat, far    );
  }

  // ------------------------------------------------------------------------------------------ //
  public static boolean sphereInFrustum( GfxFrustum f, Vector3 vCenter, float fRadius ) 
  { 

    if      ( distanceToPlane( vCenter, f.left   ) < -fRadius ) return false;
    else if ( distanceToPlane( vCenter, f.right  ) < -fRadius ) return false;
    else if ( distanceToPlane( vCenter, f.near   ) < -fRadius ) return false;
    else if ( distanceToPlane( vCenter, f.far    ) < -fRadius ) return false;
    else if ( distanceToPlane( vCenter, f.bottom ) < -fRadius ) return false;
    else if ( distanceToPlane( vCenter, f.top    ) < -fRadius ) return false;

    return true;
  }

  // ------------------------------------------------------------------------------------------ //
  public static void calculateScreenRay( GLU glu, float x, float y, float width, float height, Vector3 vRayPosOut, Vector3 vRayDirOut ) { calculateScreenRay(glu,glViewMats,x,y,width,height,vRayPosOut,vRayDirOut); }
  public static void calculateScreenRay( GLU glu, GfxViewMatrices matrices, float x, float y, float width, float height, Vector3 vRayPosOut, Vector3 vRayDirOut )
  {
	double dx   = (double)x;
	double dy   = (double)(height - y);
	double near = 0.5f;
    glu.gluUnProject( dx, dy, near, matrices.modelview, 0, matrices.projection, 0, matrices.viewport, 0, matrices.out, 0 );
    vRayPosOut.set( (float)matrices.out[0], (float)matrices.out[1], (float)matrices.out[2] );

    glu.gluUnProject( dx, dy, near + 1.0, matrices.modelview, 0, matrices.projection, 0, matrices.viewport, 0, matrices.out, 0 );
    vRayDirOut.set( (float)matrices.out[0], (float)matrices.out[1], (float)matrices.out[2] );

    vRayDirOut.dec( vRayPosOut );
    vRayDirOut.nrmeq();  
  }

  // ------------------------------------------------------------------------------------------ //
  public static Vector3 calculateIntersect( GLU glu, float x, float y, float width, float height, float[] plane ) { return calculateIntersect(glu,glViewMats,x,y,width,height,plane); }
  public static Vector3 calculateIntersect( GLU glu, GfxViewMatrices matrices, float x, float y, float width, float height, float[] plane )
  { 
    Vector3  vRayPos = new Vector3();
    Vector3  vRayDir = new Vector3();  

    calculateScreenRay( glu, matrices, x, y, width, height, vRayPos, vRayDir );

    // Finally set the mouse world position to the intersection location
    float[] result = rayPlaneIntersect( vRayPos, vRayDir, plane );
    if( result != null )
      return new Vector3( result );  
    
    return null;
  }

  // ------------------------------------------------------------------------------------------ //
  public static float[] rayPlaneIntersect( Vector3 vRayPos, Vector3 vRayDir, float[] plane )
  {
    Vector3 planeNormal  = new Vector3( plane );
    float   denom        = vRayDir.dot( planeNormal );               // Calculate the denominator first
    
    if( Math.abs(denom) < 0.001f ) return null;                      // Make sure we won't create a divide by zero
    
    float   numer        = vRayPos.dot( planeNormal ) + plane[3];    // Calculate the numerator next
    float   time         = -numer / denom;                           // Get the time of intersection
    return vRayDir.mul( time ).inc( vRayPos ).toFloats();            // Return the point of intersection
  }

}
