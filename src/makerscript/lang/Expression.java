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

//import scriptcam.lang.ExpressionElement;

public class Expression extends ExpressionElement
{
  public ExpressionElement  left;
  public ExpressionElement  right;
  public String             operator;

  public boolean asBool() {

    if      ( operator.equals(">")  ) return left.asFloat() >  right.asFloat();
    else if ( operator.equals("<")  ) return left.asFloat() <  right.asFloat();
    else if ( operator.equals("<=") ) return left.asFloat() <= right.asFloat();
    else if ( operator.equals(">=") ) return left.asFloat() >= right.asFloat();
    else if ( operator.equals("==") ) return (float)Math.abs( left.asFloat() - right.asFloat() ) < 0.001f;
    else if ( operator.equals("!=") ) return (float)Math.abs( left.asFloat() - right.asFloat() ) >= 0.001f;

    return false;     
  }

  public int asInt() {
    
    if      ( operator.equals("+") ) return left.asInt() + right.asInt();
    else if ( operator.equals("-") ) return left.asInt() - right.asInt();
    else if ( operator.equals("*") ) return left.asInt() * right.asInt();
    else if ( operator.equals("/") ) return left.asInt() / right.asInt();
    else if ( operator.equals("%") ) return left.asInt() % right.asInt();
    else if ( operator.equals("^") ) return (int)Math.pow( left.asInt(), right.asInt() );
    
    return 0;    
  }
  
  public float asFloat() {

    if      ( operator.equals("+") ) return left.asFloat() + right.asFloat();
    else if ( operator.equals("-") ) return left.asFloat() - right.asFloat();
    else if ( operator.equals("*") ) return left.asFloat() * right.asFloat();
    else if ( operator.equals("/") ) return left.asFloat() / right.asFloat();
    else if ( operator.equals("%") ) return left.asFloat() % right.asFloat();
    else if ( operator.equals("^") ) return (float)Math.pow( left.asFloat(), right.asFloat() );
    
    return 0.f;      
  }
  
  public String asString() {
    return "(" + left.asString() + operator + right.asString() + ")";
  }
  
  public Vector2 asVector2() {
    return null; //@TODO convert expression to vector2
  }
  public Vector3 asVector3() {
    return null; //@TODO convert expression to vector3
  }
}
