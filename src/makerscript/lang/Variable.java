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

public class Variable extends ExpressionElement
{
  private String   name;      // Name of this variable
  private Object   objRef;    // Object reference to the variable

  public  String   getName() { return name; }
  
  
  public Variable( String name, int index, int type, Object obj ) {
    this.name     = name;
    this.objRef   = obj;
 
    setIndex ( index );
    setType  ( type  );
  }
  
  public void set( float     value ) { objRef = new Float(value);   setType( VariableBank.VARTYPE_FLOAT   ); }
  public void set( int       value ) { objRef = new Integer(value); setType( VariableBank.VARTYPE_INTEGER ); }
  public void set( boolean   value ) { objRef = new Boolean(value); setType( VariableBank.VARTYPE_BOOLEAN ); }
  public void set( Vector2   value ) { objRef = new Vector2(value); setType( VariableBank.VARTYPE_VECTOR2 ); }
  public void set( Vector3   value ) { objRef = new Vector3(value); setType( VariableBank.VARTYPE_VECTOR3 ); }

  
  public float asFloat() {
    if      ( getType() == VariableBank.VARTYPE_FLOAT   ) return ((Float)objRef).floatValue();
    else if ( getType() == VariableBank.VARTYPE_INTEGER ) return (float)((Integer)objRef).intValue();
    return 0.f;
  }
  
  public int   asInt() {
    if      ( getType() == VariableBank.VARTYPE_INTEGER ) return ((Integer)objRef).intValue(); 
    else if ( getType() == VariableBank.VARTYPE_FLOAT   ) return (int)((Float)objRef).floatValue(); 
    return 0;
  }
  
  public boolean   asBool()       { if( getType() != VariableBank.VARTYPE_BOOLEAN    ) return false; return ((Boolean)objRef).booleanValue(); }
  public String    asString()     { if( getType() != VariableBank.VARTYPE_STRING     ) return null;  return (String)  objRef; }    
  public Vector2   asVector2()    { if( getType() != VariableBank.VARTYPE_VECTOR2    ) return null;  return (Vector2) objRef; }
  public Vector3   asVector3()    { if( getType() != VariableBank.VARTYPE_VECTOR3    ) return null;  return (Vector3) objRef; }
}
