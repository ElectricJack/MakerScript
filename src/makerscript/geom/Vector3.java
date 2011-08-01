/*
  ScriptableMill
  copyright (c) 2011, Jack W. Kern
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */
package makerscript.geom;

import makerscript.geom.Vector2;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.lang.Math;

public class Vector3 extends Vector2
{
// No frontin' ( public data )
//public float   y;  - From Vector2
//public float   x;  - From Vector2
  public float   z;

  // -------------------------------------------------------------------------------------------------------- //
  // Constructors
  public          Vector3     ( )                               {                set( 0 );                             }
  public          Vector3     ( float   xyz )                   {                set( xyz );                           }
  public          Vector3     ( float   x, float y, float z )   {                set( x, y, z );                       }
  public          Vector3     ( Vector3 v )                     {                set( v.x, v.y, v.z );                 }
  public          Vector3     ( float[] v )                     { if( valid(v) ) set( v[0], v[1], v[2] );              }

  // -------------------------------------------------------------------------------------------------------- //
  // Converters 
  public int      toColor     ( PGraphics g )                   { return g.color(x,y,z);                               } /// Converts to a processing color
  public String   toString    ( )                               { return "[" + x + ", " +  y + ", " + z + "]";         }
  public float[]  toFloats    ( )                               { return new float[] { x, y, z };                      }
      
  // -------------------------------------------------------------------------------------------------------- //
  // Getters  
  public Vector3  get         ( )                               { return new Vector3( this );                          }
  public float    get         ( int index )                     { return index == 2 ? z : super.get( index );          }
  public Vector3  get         ( Vector3 vOut )                  { return vOut.set( this );                             }
  
  public Vector2  getXY       ( )                               { return new Vector2( x, y );                          }
  public Vector2  getYX       ( )                               { return new Vector2( y, x );                          }
  public Vector2  getYZ       ( )                               { return new Vector2( y, z );                          }
  public Vector2  getZY       ( )                               { return new Vector2( z, y );                          }
  public Vector2  getXZ       ( )                               { return new Vector2( x, z );                          }
  public Vector2  getZX       ( )                               { return new Vector2( z, x );                          }
  
  // -------------------------------------------------------------------------------------------------------- //
  // Setters
  public Vector3  set         ( float   s   )                   { return set( s, s, s );                               }
  public Vector3  set         ( Vector3 v   )                   { return set( v.x, v.y, v.z );                         }
  public Vector3  set         ( float[] v   )                   { return valid(v)? set( v[0], v[1], v[2] ) : null;     }
  public Vector3  set         ( float   x, float y, float z )   { super.set(x,y); this.z = z; return this;             }
   
  public Vector3  setXY       ( float   s  )                    { return setXY( s, s );                                }
  public Vector3  setXY       ( Vector2 v  )                    { return setXY( v.x, v.y );                            }
  public Vector3  setXY       ( float[] v  )                    { return super.valid(v)? setXY( v[0], v[1] ) : null;   }
  public Vector3  setXY       ( float   x, float y )            { super.set( x, y );      return this;                 }
  
  public Vector3  setYZ       ( float   s  )                    { return setYZ( s, s );                                }
  public Vector3  setYZ       ( Vector2 v  )                    { return setYZ( v.x, v.y );                            }
  public Vector3  setYZ       ( float[] v  )                    { return super.valid(v)? setYZ( v[0], v[1] ) : null;   }
  public Vector3  setYZ       ( float   y, float z )            { this.y = y; this.z = z; return this;                 }
  
  public Vector3  setXZ       ( float   s  )                    { return setXZ( s, s );                                }
  public Vector3  setXZ       ( Vector2 v  )                    { return setXZ( v.x, v.y );                            }
  public Vector3  setXZ       ( float[] v  )                    { return super.valid(v)? setXZ( v[0], v[1] ) : null;   }
  public Vector3  setXZ       ( float   x, float z )            { this.x = x; this.z = z; return this;                 }

  // -------------------------------------------------------------------------------------------------------- //
  // Incrementers
  public Vector3  inc         ( float   s )                     { return inc(   s,   s,   s );                         }
  public Vector3  inc         ( Vector3 v )                     { return inc( v.x, v.y, v.z );                         }
  public Vector3  inc         ( float[] v )                     { return valid(v)? inc( v[0], v[1], v[2] ) : null;     }
  public Vector3  inc         ( float   x, float y, float z )   { this.x += x; this.y += y; this.z += z;  return this; }
  
  // -------------------------------------------------------------------------------------------------------- //
  // Decrementers
  public Vector3  dec         ( float   s )                     { return dec(   s,   s,   s ); }
  public Vector3  dec         ( Vector3 v )                     { return dec( v.x, v.y, v.z ); }
  public Vector3  dec         ( float[] v )                     { return valid(v)? dec( v[0], v[1], v[2] ) : null; }
  public Vector3  dec         ( float   x, float y, float z )   { this.x -= x; this.y -= y; this.z -= z;  return this; }
  
  // -------------------------------------------------------------------------------------------------------- //
  public Vector3  muleq       ( float   s )                     { return muleq( s, s, s );                             }
  public Vector3  muleq       ( Vector3 v )                     { return muleq( v.x, v.y, v.z );                       }
  public Vector3  muleq       ( float[] v )                     { return valid(v)? muleq( v[0], v[1], v[2] ) : null;   }
  public Vector3  muleq       ( float   x, float y, float z )   { this.x *= x; this.y *= y; this.z *= z; return this;  }
  
  // -------------------------------------------------------------------------------------------------------- //
  public Vector3  diveq       ( float   s )                     { return diveq( s, s, s );                             }
  public Vector3  diveq       ( Vector3 v )                     { return diveq( v.x, v.y, v.z );                       }
  public Vector3  diveq       ( float[] v )                     { return valid(v)? diveq( v[0], v[1], v[2] ) : null;   }
  public Vector3  diveq       ( float   x, float y, float z )   { return muleq( 1.0f / x, 1.0f / y, 1.0f / z );        }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Mod Equals
  public Vector3  modeq       ( float   s )                     { return modeq( s, s, s );                             }
  public Vector3  modeq       ( Vector3 v )                     { return modeq( v.x, v.y, v.z );                       } 
  public Vector3  modeq       ( float[] v )                     { return valid(v)? modeq( v[0], v[1], v[2] ) : null;   } 
  public Vector3  modeq       ( float   x, float y, float z )   { this.x %= x; this.y %= y; this.y %= z; return this;  }

  // ------------------------------------------------------------------------------------------------------------ //
  // Midpoint Equals
  public Vector3  mideq       ( )                               { return muleq( 0.5f );                                }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Inverse Equals
  public Vector3  inveq       ( )                               { return set( 1.0f / x, 1.0f / y, 1.0f / z );          }
  
  // ------------------------------------------------------------------------------------------------------------ //
  public Vector3  crosseq     ( float   s )                     { return crosseq( s, s, s );                           }
  public Vector3  crosseq     ( Vector3 v )                     { return crosseq( v.x, v.y, v.z );                     }
  public Vector3  crosseq     ( float[] v )                     { return valid(v)? crosseq( v[0], v[1], v[2] ) : null; }
  public Vector3  crosseq     ( float x, float y, float z )
  {
    return set( this.y * z - this.z * y
              , this.z * x - this.x * z
              , this.x * y - this.y * x );
  }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Project Equals
  public Vector3  prjeq       ( float   s )                     { return prjeq( s, s, s );                             }  /// Projects this vector onto v and stores result in this
  public Vector3  prjeq       ( Vector3 v )                     { return prjeq( v.x, v.y, v.z );                       }  /// Projects this vector onto v and stores result in this
  public Vector3  prjeq       ( float[] v )                     { return valid(v)? prjeq( v[0], v[1], v[2] ) : null;   }  /// Projects this vector onto v and stores result in this
  public Vector3  prjeq       ( float   x, float y, float z )   { float d = dot(x,y,z); return set(x,y,z).muleq( d );  }  /// Projects this vector onto v and stores result in this
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Reflect Equals
  public Vector3  refeq       ( float   s )                     { return refeq( new Vector3( s ) );                         } /// Reflects this vector along the surface normal vector [s, s, s]
  public Vector3  refeq       ( Vector3 v )                     { return inc( prj( v.nrmeq() ).dec( this ).muleq( 2.0f ) ); } /// Reflects this vector along the surface normal vector v
  public Vector3  refeq       ( float[] v )                     { return valid(v)? refeq( new Vector3( v ) ) : null;        } /// Reflects this vector along the surface normal vector v[]
  public Vector3  refeq       ( float   x, float y, float z )   { return refeq( new Vector3( x, y, z ) );                   } /// Reflects this vector along the surface normal vector [x, y, z]
  

  // ------------------------------------------------------------------------------------------------------------ //
  public Vector3  add         ( float   s  )                                  { return get().inc( s ); } 
  public Vector3  add         ( Vector3 v  )                                  { return get().inc( v ); } 
  public Vector3  add         ( float[] v  )                                  { return get().inc( v ); } 
  public Vector3  add         ( float   x, float y, float z  )                { return get().inc( x, y, z ); } 
  
  public Vector3  add         ( float   s,                   Vector3 vOut )   { return get(vOut).inc( s ); }
  public Vector3  add         ( Vector3 v,                   Vector3 vOut )   { return get(vOut).inc( v ); }
  public Vector3  add         ( float[] v,                   Vector3 vOut )   { return get(vOut).inc( v ); }
  public Vector3  add         ( float   x, float y, float z, Vector3 vOut )   { return get(vOut).inc( x, y, z ); }
  

  // ------------------------------------------------------------------------------------------------------------ //
  public Vector3  sub         ( float   s    )                                { return get().dec( s ); }
  public Vector3  sub         ( Vector3 v    )                                { return get().dec( v ); }
  public Vector3  sub         ( float[] v    )                                { return get().dec( v ); }
  public Vector3  sub         ( float   x, float y, float z )                 { return get().dec( x, y, z ); }
  
  public Vector3  sub         ( float   s,                   Vector3 vOut )   { return get(vOut).dec( s ); }
  public Vector3  sub         ( Vector3 v,                   Vector3 vOut )   { return get(vOut).dec( v ); }
  public Vector3  sub         ( float[] v,                   Vector3 vOut )   { return get(vOut).dec( v ); }
  public Vector3  sub         ( float   x, float y, float z, Vector3 vOut )   { return get(vOut).dec( x, y, z ); }  
  
  // ------------------------------------------------------------------------------------------------------------ //
  public Vector3  mul         ( float   s    )                                { return get().muleq( s ); }
  public Vector3  mul         ( Vector3 v    )                                { return get().muleq( v ); }
  public Vector3  mul         ( float[] v    )                                { return get().muleq( v ); }
  public Vector3  mul         ( float   x, float y, float z )                 { return get().muleq( x, y, z); }
  
  public Vector3  mul         ( float   s,                   Vector3 vOut )   { return get(vOut).muleq( s ); }
  public Vector3  mul         ( Vector3 v,                   Vector3 vOut )   { return get(vOut).muleq( v ); }
  public Vector3  mul         ( float[] v,                   Vector3 vOut )   { return get(vOut).muleq( v ); }
  public Vector3  mul         ( float   x, float y, float z, Vector3 vOut )   { return get(vOut).muleq( x, y, z ); }
  
  // ------------------------------------------------------------------------------------------------------------ //
  public Vector3  div         ( float   s    )                                { return get().diveq( s ); }
  public Vector3  div         ( Vector3 v    )                                { return get().diveq( v ); }
  public Vector3  div         ( float[] v    )                                { return get().diveq( v ); }
  public Vector3  div         ( float   x, float y, float z )                 { return get().diveq( x, y, z ); }
  
  public Vector3  div         ( float   s,                   Vector3 vOut )   { return get(vOut).diveq( s );        }
  public Vector3  div         ( Vector3 v,                   Vector3 vOut )   { return get(vOut).diveq( v );        }
  public Vector3  div         ( float[] v,                   Vector3 vOut )   { return get(vOut).diveq( v );        }
  public Vector3  div         ( float   x, float y, float z, Vector3 vOut )   { return get(vOut).diveq( x, y, z );  }

  // ------------------------------------------------------------------------------------------------------------ //
  // Project 
  public Vector3  prj         ( float   s )                                   { return get().prjeq( s );            }
  public Vector3  prj         ( Vector3 v )                                   { return get().prjeq( v );            }
  public Vector3  prj         ( float[] v )                                   { return get().prjeq( v );            }
  public Vector3  prj         ( float   x, float y, float z )                 { return get().prjeq( x, y, z );      }
  
  public Vector3  prj         ( float   s,                   Vector3 vOut )   { return get(vOut).prjeq( s );        }
  public Vector3  prj         ( Vector3 v,                   Vector3 vOut )   { return get(vOut).prjeq( v );        }
  public Vector3  prj         ( float[] v,                   Vector3 vOut )   { return get(vOut).prjeq( v );        }
  public Vector3  prj         ( float   x, float y, float z, Vector3 vOut )   { return get(vOut).prjeq( x, y, z );  }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Reflect
  public Vector3  ref         ( float   s )                                   { return get().refeq( s );            } 
  public Vector3  ref         ( Vector3 v )                                   { return get().refeq( v );            } 
  public Vector3  ref         ( float[] v )                                   { return get().refeq( v );            } 
  public Vector3  ref         ( float   x, float y, float z )                 { return get().refeq( x, y, z );      }
  
  public Vector3  ref         ( float   s,                   Vector3 vOut )   { return get(vOut).refeq( s );        }
  public Vector3  ref         ( Vector3 v,                   Vector3 vOut )   { return get(vOut).refeq( v );        }
  public Vector3  ref         ( float[] v,                   Vector3 vOut )   { return get(vOut).refeq( v );        }
  public Vector3  ref         ( float   x, float y, float z, Vector3 vOut )   { return get(vOut).refeq( x, y, z );  }  
  
  // ------------------------------------------------------------------------------------------------------------ //
  public Vector3  cross       ( float   s )                                   { return get().crosseq(s); } 
  public Vector3  cross       ( Vector3 v )                                   { return get().crosseq(v); } 
  public Vector3  cross       ( float[] v )                                   { return get().crosseq(v); } 
  public Vector3  cross       ( float x, float y, float z )                   { return get().crosseq(x,y,z); }

  public Vector3  cross       ( float   s,                 Vector3 vOut )     { return get(vOut).crosseq(s); } 
  public Vector3  cross       ( Vector3 v,                 Vector3 vOut )     { return get(vOut).crosseq(v); } 
  public Vector3  cross       ( float[] v,                 Vector3 vOut )     { return get(vOut).crosseq(v); } 
  public Vector3  cross       ( float x, float y, float z, Vector3 vOut )     { return get(vOut).crosseq(x,y,z); }
  
  // ------------------------------------------------------------------------------------------------------------ //
  public float    lenlen      ( )                              { return dot( this );                      }
  public float    len         ( )                              { return (float)Math.sqrt( lenlen() );     }
  
  // ------------------------------------------------------------------------------------------------------------ //
  public float    dot         ( float   s    )                 { return dot( s, s, s );                            }
  public float    dot         ( Vector3 v    )                 { return dot( v.x, v.y, v.z );                      }
  public float    dot         ( float[] v    )                 { return valid(v)? dot( v[0], v[1], v[2] ) : null;  }
  public float    dot         ( float   x, float y, float z )  { return this.x*x + this.y*y + this.z*z;            }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Normalizers
  public Vector3  nrm         ( )                              { return get().nrmeq();                             }
  public Vector3  nrm         ( Vector3 vOut )                 { return get(vOut).nrmeq();                         }
  public Vector3  nrmeq       ( )                              { return diveq( len() );                            }

  
  // ------------------------------------------------------------------------------------------------------------ //
  public void lerp( Vector3 v0, Vector3 v1, float fValue )
  {
    set( PApplet.lerp( v0.x, v1.x, fValue )
       , PApplet.lerp( v0.y, v1.y, fValue )
       , PApplet.lerp( v0.z, v1.z, fValue ) );
  }
  
  // ------------------------------------------------------------------------------------------------------------ //
  protected boolean valid     ( float[] v    )                { return v.length >= 3; }
  
  // -------------------------------------------------------------------------------------------------------- //
  // Depricated:
  public Vector3  toScreenXYZ ( PGraphics g  )                { return  new Vector3( toScreenX(g), toScreenY(g), toScreenZ(g) ); }  // Returns the XYZ position of the Vector3 projected onto the screen
  public void     toScreenXYZ ( PGraphics g, Vector3 vOut )   {         vOut.set( toScreenX(g), toScreenY(g), toScreenZ(g) );    }  //  ^-- Same as above but doesn't allocate a new vector
  public Vector2  toScreenXY  ( PGraphics g  )                { return  new Vector2( toScreenX(g), toScreenY(g) );               }  // Returns the XY position of the Vector3 projected onto the screen
  public void     toScreenXY  ( PGraphics g, Vector2 vOut )   {         vOut.set( toScreenX(g), toScreenY(g) );                  }  //  ^-- Same as above but doesn't allocate a new vector
  public float    toScreenX   ( PGraphics g  )                { return  g.screenX(x,y,z);                                        }  // Returns the X position of the Vector3 projected onto the screen
  public float    toScreenY   ( PGraphics g  )                { return  g.screenY(x,y,z);                                        }  // Returns the Y position of the Vector3 projected onto the screen
  public float    toScreenZ   ( PGraphics g  )                { return  g.screenZ(x,y,z);                                        }  // Returns the Z position of the Vector3 projected onto the screen
  
}