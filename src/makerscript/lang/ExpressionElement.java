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
package makerscript.lang;

import makerscript.geom.Vector2;
import makerscript.geom.Vector3;

public abstract class ExpressionElement
{
  private         int       index;
  private         int       type;     // Type of this variable
  
  public          int       getIndex  ( )           { return index;       }
  public          int       getType   ( )           { return type;        }
  
  protected       void      setIndex  ( int index ) { this.index = index; }
  protected       void      setType   ( int type  ) { this.type  = type;  }

  public abstract float     asFloat   ( );
  public abstract int       asInt     ( );
  public abstract boolean   asBool    ( );
  public abstract String    asString  ( );
  public abstract Vector2   asVector2 ( );
  public abstract Vector3   asVector3 ( );
}
