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

import java.util.Queue;
import java.util.TreeMap;

import makerscript.math.Mathematical;

public abstract class Command extends Mathematical implements Comparable<Command>
{ 

  // -----------------------------------------------------------------
  protected int                     code           = 0;
  protected TreeMap<String,Command> nameToCommand  = new TreeMap<String,Command>();
  protected String                  identifier     = null;    
  protected CommandData             data           = new CommandData ();
  
  
  // Compareable methods
  public          int      compareTo ( Command rhs )  { return identifier.compareTo( rhs.identifier ); }
  public          boolean  equals    ( Command obj )  { return identifier.equals( obj ); }
  public abstract Command  clone     ( );
  public abstract int      call      ( LScriptState state, Queue<ExpressionElement> params, int callIndex );

  public Command( CommandStore cs, String strSignature ) {
    nameToCommand = new TreeMap<String,Command>();
    cs.addCommand( this, strSignature );
  }

  // ---------------------------------------------------------------------------- //
  public Command( Command copy )
  {
    this.code       = copy.code;
    this.identifier = copy.identifier;
  }
  
  // ---------------------------------------------------------------------------- //
  public boolean takesParams() { return data.params > 0; }

  // ---------------------------------------------------------------------------- //
  protected int varTypeStringToIndex( String varType )
  {
    varType = varType.trim().toLowerCase();
    if      ( varType.equals("float") )      return VariableBank.VARTYPE_FLOAT;
    else if ( varType.equals("int") )        return VariableBank.VARTYPE_INTEGER;
    else if ( varType.equals("bool") )       return VariableBank.VARTYPE_BOOLEAN;
    else if ( varType.equals("string") )     return VariableBank.VARTYPE_STRING;
    else if ( varType.equals("vector2") )    return VariableBank.VARTYPE_VECTOR2;
    else if ( varType.equals("vector3") )    return VariableBank.VARTYPE_VECTOR3;    
    else if ( varType.equals("variable") )   return VariableBank.VARTYPE_VARIABLEREF;
    return -1;    
  }
  // ---------------------------------------------------------------------------- //
  protected String varTypeIndexToString( int index )
  {
    switch( index )
    {
      case VariableBank.VARTYPE_FLOAT:       return "Float";
      case VariableBank.VARTYPE_INTEGER:     return "Int";
      case VariableBank.VARTYPE_BOOLEAN:     return "Bool";
      case VariableBank.VARTYPE_STRING:      return "String";
      case VariableBank.VARTYPE_VECTOR2:     return "Vector2";
      case VariableBank.VARTYPE_VECTOR3:     return "Vector3";
      case VariableBank.VARTYPE_VARIABLEREF: return "Variable";
    }
    return "INVALID!";
  }
  // ---------------------------------------------------------------------------- //
  protected void addVarParam( String varType, boolean expression )
  {
                      data.paramSignature[ data.params ]  = varTypeStringToIndex( varType );
    if( expression )  data.paramSignature[ data.params ] |= VariableBank.VARTYPE_EXPRESSION_FLAG;
    
    ++data.params;
  }

  // ----------------------------------------------------------------- //
  protected int       popVarRef  ( Queue<ExpressionElement> params )  { ExpressionElement elm = popVar( params ); return elm == null ? -1     : elm.getIndex(); }
  protected int       popInt     ( Queue<ExpressionElement> params )  { ExpressionElement elm = popVar( params ); return elm == null ?  0     : elm.asInt();    }
  protected float     popFloat   ( Queue<ExpressionElement> params )  { ExpressionElement elm = popVar( params ); return elm == null ?  0.f   : elm.asFloat();  }
  protected boolean   popBool    ( Queue<ExpressionElement> params )  { ExpressionElement elm = popVar( params ); return elm == null ?  false : elm.asBool();   }
  protected String    popString  ( Queue<ExpressionElement> params )  { ExpressionElement elm = popVar( params ); return elm == null ?  ""    : elm.asString(); }
  // ----------------------------------------------------------------- //
  protected ExpressionElement popVar( Queue<ExpressionElement> params ) {
    if( params.size() == 0 ) return null;
    return (ExpressionElement)params.remove();
  }

  // ---------------------------------------------------------------------------- //
  public String asString()
  {
    String out = new String();
    
    if( identifier != null )
    {
      out += identifier + "[" + code + "]";
      
      if( data != null )
      {
        out += "( ";
        
        for( int i=0; i<data.params; ++i )
          out += varTypeIndexToString( data.paramSignature[i] )  + " ";
          
        out += ")";
      }
    }
    
    if( nameToCommand.size() == 0 )
      return out;
  
    out += "->";
    
    /*
    Collection<Command> collect = nameToCommand.values();
    Iterator<Command>   itt     = collect.iterator();
    while( itt.hasNext() ) {      
      Command cmd = (Command)itt.next();
      out  += cmd.asString();
      if( itt.hasNext() ) out += ", ";
    }
    */
    boolean first = true;
    for( Command cmd : nameToCommand.values() ) {
      if( !first ) out += ", ";
      out += cmd.asString();
      first = false;
    }
    
    return out;
  } 
  
}
 
