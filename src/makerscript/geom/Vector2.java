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

import processing.core.PApplet;
import java.lang.Math;

public class Vector2
{
  // No frontin' ( public data )
  public float    x;
  public float    y;
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Constructors
  public          Vector2     ( )                                   { set( 0    );                                 }
  public          Vector2     ( float xy )                          { set( xy   );                                 }
  public          Vector2     ( float x, float y )                  { set( x, y );                                 }
  public          Vector2     ( Vector2 v )                         { set( v    );                                 }
  public          Vector2     ( float[] v )                         { set( v    );                                 }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Converters 
  public float[]  toFloats    ( )                                   { return new float[] { x, y };                 }
  public String   toString    ( )                                   { return "[" + x + ", " +  y + "]";            }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Getters
  public float    get         ( int index )                         { return index == 0 ? x : y;                   }
  public Vector2  get         ( )                                   { return new Vector2( this );                  }
  public Vector2  get         ( Vector2 vOut )                      { return vOut.set( this );                     }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Setters
  public Vector2  set         ( float   x, float y )                { this.x = x; this.y = y; return this;         }
  public Vector2  set         ( float   xy )                        { return set( xy, xy );                        }
  public Vector2  set         ( float[] v  )                        { return valid(v)? set( v[0], v[1] ) : null;   }
  public Vector2  set         ( Vector2 v  )                        { return set( v.x, v.y );                      }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Adders
  public Vector2  add         ( Vector2 v )                         { return get().inc( v );                       }
  public Vector2  add         ( float[] v )                         { return get().inc( v );                       }
  public Vector2  add         ( float   x, float y )                { return get().inc( x, y );                    }
  public Vector2  add         ( float[] v,          Vector2 vOut )  { return get(vOut).inc( v );                   }
  public Vector2  add         ( Vector2 v,          Vector2 vOut )  { return get(vOut).inc( v );                   }
  public Vector2  add         ( float   x, float y, Vector2 vOut )  { return get(vOut).inc( x, y );                }

  // ------------------------------------------------------------------------------------------------------------ //
  // Subtractors
  public Vector2  sub         ( Vector2 v )                         { return get().dec( v );                       }
  public Vector2  sub         ( float[] v )                         { return get().dec( v );                       }
  public Vector2  sub         ( float   x, float y )                { return get().dec( x, y );                    }
  public Vector2  sub         ( Vector2 v,          Vector2 vOut )  { return get(vOut).dec( v );                   }
  public Vector2  sub         ( float[] v,          Vector2 vOut )  { return get(vOut).dec( v );                   }
  public Vector2  sub         ( float   x, float y, Vector2 vOut )  { return get(vOut).dec( x, y );                }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Multipliers
  public Vector2  mul         ( float   s )                         { return get().muleq( s );                     }
  public Vector2  mul         ( Vector2 v )                         { return get().muleq( v );                     }
  public Vector2  mul         ( float[] v )                         { return get().muleq( v );                     }
  public Vector2  mul         ( float   x, float y )                { return get().muleq( x, y );                  }
  
  public Vector2  mul         ( float   s,          Vector2 vOut )  { return get(vOut).muleq( s );                 }
  public Vector2  mul         ( Vector2 v,          Vector2 vOut )  { return get(vOut).muleq( v );                 }
  public Vector2  mul         ( float[] v,          Vector2 vOut )  { return get(vOut).muleq( v );                 }
  public Vector2  mul         ( float   x, float y, Vector2 vOut )  { return get(vOut).muleq( x, y );              }

  // ------------------------------------------------------------------------------------------------------------ //
  // Dividors
  public Vector2  div         ( float   s )                         { return get().diveq( s );                     }
  public Vector2  div         ( Vector2 v )                         { return get().diveq( v );                     }
  public Vector2  div         ( float[] v )                         { return get().diveq( v );                     }
  public Vector2  div         ( float   x, float y )                { return get().diveq( x, y );                  }    
  
  public Vector2  div         ( float   s,          Vector2 vOut )  { return get(vOut).diveq( s );                 }
  public Vector2  div         ( Vector2 v,          Vector2 vOut )  { return get(vOut).diveq( v );                 }
  public Vector2  div         ( float[] v,          Vector2 vOut )  { return get(vOut).diveq( v );                 }
  public Vector2  div         ( float   x, float y, Vector2 vOut )  { return get(vOut).diveq( x, y );              }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Modulators
  public Vector2  mod         ( float   s )                         { return get().modeq( s );                     }  /// Modulus Operator - Returns copy of piecewise remainder of division of each element.
  public Vector2  mod         ( Vector2 v )                         { return get().modeq( v );                     }  /// Modulus Operator - Returns copy of piecewise remainder of division of each element.
  public Vector2  mod         ( float[] v )                         { return get().modeq( v );                     }  /// Modulus Operator - Returns copy of piecewise remainder of division of each element.
  public Vector2  mod         ( float   x, float y )                { return get().modeq( x, y );                  }
  
  public Vector2  mod         ( float   s,          Vector2 vOut )  { return get(vOut).modeq( s );                 }  /// Modulus Operator - Calculates piecewise remainder of division of each element.
  public Vector2  mod         ( Vector2 v,          Vector2 vOut )  { return get(vOut).modeq( v );                 }  /// Modulus Operator - Calculates piecewise remainder of division of each element.
  public Vector2  mod         ( float[] v,          Vector2 vOut )  { return get(vOut).modeq( v );                 }  /// Modulus Operator - Calculates piecewise remainder of division of each element.  
  public Vector2  mod         ( float   x, float y, Vector2 vOut )  { return get(vOut).modeq( x, y );              }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Projectors
  public Vector2  prj         ( float   s )                         { return get().prjeq( s );                     }  /// Projects this vector onto v and returns copy
  public Vector2  prj         ( Vector2 v )                         { return get().prjeq( v );                     }  /// Projects this vector onto v and returns copy
  public Vector2  prj         ( float[] v )                         { return get().prjeq( v );                     }  /// Projects this vector onto v and returns copy
  public Vector2  prj         ( float   x, float y )                { return get().prjeq( x, y );                  }  /// Projects this vector onto v and returns copy
  
  public Vector2  prj         ( float   s,          Vector2 vOut )  { return get(vOut).prjeq( s );                 }  /// Projects this vector onto v and returns copy
  public Vector2  prj         ( Vector2 v,          Vector2 vOut )  { return get(vOut).prjeq( v );                 }  /// Projects this vector onto v and returns copy
  public Vector2  prj         ( float[] v,          Vector2 vOut )  { return get(vOut).prjeq( v );                 }  /// Projects this vector onto v and returns copy
  public Vector2  prj         ( float   x, float y, Vector2 vOut )  { return get(vOut).prjeq( x, y );              }  /// Projects this vector onto v and returns copy
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Reflectors
  public Vector2  ref         ( float   s )                         { return get().refeq( s );                     }
  public Vector2  ref         ( Vector2 v )                         { return get().refeq( v );                     }
  public Vector2  ref         ( float[] v )                         { return get().refeq( v );                     }
  public Vector2  ref         ( float   x, float y )                { return get().refeq( x, y );                  }
  
  public Vector2  ref         ( float   s,          Vector2 vOut )  { return get(vOut).refeq( s );                 }
  public Vector2  ref         ( Vector2 v,          Vector2 vOut )  { return get(vOut).refeq( v );                 }
  public Vector2  ref         ( float[] v,          Vector2 vOut )  { return get(vOut).refeq( v );                 }
  public Vector2  ref         ( float   x, float y, Vector2 vOut )  { return get(vOut).refeq( x, y );              }

  
  // ------------------------------------------------------------------------------------------------------------ //
  // Inverters
  public Vector2  inv         ( )                                   { return get().inveq();                        }
  public Vector2  inv         ( Vector2 vOut )                      { return get( vOut ).inveq();                  }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Midpoint
  public Vector2  mid         ( )                                   { return get().mideq();                        }
  public Vector2  mid         ( Vector2 vOut )                      { return get(vOut).mideq();                    }  
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Lengths
  public float    lenlen      ( )                                   { return dot( this );                          }  /// Returns length of the vector squared
  public float    len         ( )                                   { return (float)Math.sqrt( lenlen() );         }
    
  // ------------------------------------------------------------------------------------------------------------ //
  // Dotters
    public float  dot         ( float   s )                         { return dot( s, s );                          }
    public float  dot         ( Vector2 v )                         { return dot( v.x, v.y );                      }
    public float  dot         ( float[] v )                         { return valid(v)? dot( v[0], v[1] ) : 0;      }
    public float  dot         ( float x, float y )                  { return this.x * x + this.y * y;              } 
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Normalizers 
  public Vector2  nrm         ( )                                   { return get().nrmeq();                        }
  public Vector2  nrm         ( Vector2 vOut )                      { return get(vOut).nrmeq();                    }
  public Vector2  nrmeq       ( )                                   { return diveq( len() );                       }
    
  // ------------------------------------------------------------------------------------------------------------ //
  // Angulars
  public float    ang         ( )                                   { return PApplet.atan2( y, x );                }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Rotators
  public Vector2  rot         ( float a )                           { return get().roteq( a );                     }
  public Vector2  rot         ( float a, Vector2 vOut )             { return get(vOut).roteq( a );                 }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Incrementers
  public Vector2  inc         ( float   s )                         { return inc( s, s );                          }
  public Vector2  inc         ( Vector2 v )                         { return inc( v.x, v.y );                      }
  public Vector2  inc         ( float[] v )                         { return valid(v)? inc( v[0], v[1] ) : null;   }
  public Vector2  inc         ( float   x, float y )                { this.x += x; this.y += y; return this;       }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Decrementers
  public Vector2  dec         ( float   s )                         { return dec( s, s );                          }
  public Vector2  dec         ( Vector2 v )                         { return dec( v.x, v.y );                      }
  public Vector2  dec         ( float[] v )                         { return valid(v)? dec( v[0], v[1] ) : null;   }
  public Vector2  dec         ( float   x, float y )                { this.x -= x; this.y -= y; return this;       }
  
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Multiply Equals
  public Vector2  muleq       ( float   s )                         { return muleq( s,   s   );                    }
  public Vector2  muleq       ( Vector2 v )                         { return muleq( v.x, v.y );                    }
  public Vector2  muleq       ( float[] v )                         { return valid(v)? muleq( v[0], v[1] ) : null; }
  public Vector2  muleq       ( float   x, float y )                { this.x *= x; this.y *= y; return this;       }  
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Divide Equals
  public Vector2  diveq       ( float   s )                         { return muleq( 1.0f / s          );           }
  public Vector2  diveq       ( Vector2 v )                         { return muleq( v.inv()           );           }
  public Vector2  diveq       ( float[] v )                         { return valid(v)? diveq( v[0], v[1] ) : null; }
  public Vector2  diveq       ( float   x, float y )                { return muleq( 1.0f / x, 1.0f / y );          }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Mod Equals
  public Vector2  modeq       ( float   s )                         { return modeq( s, s );                        }
  public Vector2  modeq       ( Vector2 v )                         { return modeq( v.x, v.y );                    } 
  public Vector2  modeq       ( float[] v )                         { return valid(v)? modeq( v[0], v[1] ) : null; } 
  public Vector2  modeq       ( float   x, float y )                { this.x %= x; this.y %= y; return this;       }

  // ------------------------------------------------------------------------------------------------------------ //
  // Midpoint Equals
  public Vector2  mideq       ( )                                   { return muleq( 0.5f );                        }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Inverse Equals
  public Vector2  inveq       ( )                                   { return set( 1.0f / x, 1.0f / y );            }
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Project Equals
  
  public Vector2  prjeq       ( float   s )                         { return prjeq( s, s );                           }  /// Projects this vector onto v and stores result in this
  public Vector2  prjeq       ( Vector2 v )                         { return prjeq( v.x, v.y );                       }  /// Projects this vector onto v and stores result in this
  public Vector2  prjeq       ( float[] v )                         { return valid(v)? prjeq( v[0], v[1] ) : null;    }  /// Projects this vector onto v and stores result in this
  public Vector2  prjeq       ( float   x, float y )                { float d = dot(x,y); return set(x,y).muleq( d ); }  /// Projects this vector onto v and stores result in this
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Reflect Equals
  public Vector2  refeq       ( float   s )                         { return refeq( new Vector2( s )   );                       } /// Reflects this vector along the surface normal vector v
  public Vector2  refeq       ( Vector2 v )                         { return inc( prj( v.nrmeq() ).dec( this ).muleq( 2.0f ) ); } /// Reflects this vector along the surface normal vector v
  public Vector2  refeq       ( float[] v )                         { return valid(v)? refeq( new Vector2( v ) ) : null;        } /// Reflects this vector along the surface normal vector v
  public Vector2  refeq       ( float   x, float y )                { return refeq( new Vector2( x, y ) );                      } /// Reflects this vector along the surface normal vector v
  
  // ------------------------------------------------------------------------------------------------------------ //
  // Rotate Equals
  public Vector2  roteq       ( float a )                           { return set( x * (float)Math.cos(a) - y * (float)Math.sin(a)
                                                                                , x * (float)Math.sin(a) + y * (float)Math.cos(a) ); }
  
  public Vector2 lerp( Vector2 v0, Vector2 v1, float fValue )
  {
    set( PApplet.lerp( v0.x, v1.x, fValue )
         , PApplet.lerp( v0.y, v1.y, fValue ) );
    return this;
  }
  public Vector2 lerp( Vector2 v0, Vector2 v1, Vector2 vValue )
  {
    set( PApplet.lerp( v0.x, v1.x, vValue.x )
         , PApplet.lerp( v0.y, v1.y, vValue.y ) );
    return this;
  }  
  
  protected boolean valid       ( float[] v    )                  { return v.length >= 3; }
};
