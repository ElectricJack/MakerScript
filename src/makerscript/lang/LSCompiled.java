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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import makerscript.doc.ListDoc;


public class LSCompiled extends ListDoc<char[]>
{
  public static final int             DEFAULT_TERMINATION_COUNT = 8000;
  
  protected  CommandStore             commands   = new CommandStore();
  protected  VariableBank             vars       = new VariableBank();
  private    LScriptState             state      = new LScriptState(); 
  private    Queue<ExpressionElement> params     = new LinkedList<ExpressionElement>();
    
  public LSCompiled() {      
    new CmdCreateVar    ( commands );
    new CmdIf           ( commands );
    new CmdLoop         ( commands );
    new CmdMathHelpers  ( commands );
  }
  
  public void setVariable( String name, float value ) { vars.registerAndCreate( name, VariableBank.VARTYPE_FLOAT);   vars.setVariable( name, value ); }
  public void setVariable( String name, int   value ) { vars.registerAndCreate( name, VariableBank.VARTYPE_INTEGER); vars.setVariable( name, value ); }

  
  public boolean compile( String file ) {
    
    BufferedReader    reader = createReader(file);
    ArrayList<String> code   = new ArrayList<String>();
    
    try {
      
      String line;
      while( (line = reader.readLine()) != null )
        code.add( line );
      
    } catch (Exception e) { e.printStackTrace(); }
    
    return compile( code );
  }
  
  public boolean compile( String[] code ) {
    clear();
    for( String line : code ) {
      if( line.isEmpty() ) continue;
      // Attempt to compile this line
      char[] compiledLine = commands.compileLine( line, vars );
      // Make sure we were able to successfully compile this line, if not return null
      if( compiledLine == null ) return false;
      // Add the compiled line
      add( compiledLine );
    }
    
    return true;
  }
  
  public boolean compile( ArrayList<String> code ) {
    
    clear();
    for( String line : code ) {
      if( line.isEmpty() ) continue;
      // Attempt to compile this line
      char[] compiledLine = commands.compileLine( line, vars );
      // Make sure we were able to successfully compile this line, if not return null
      if( compiledLine == null ) return false;
      // Add the compiled line
      add( compiledLine );
    }
    
    return true;
  }

  public void reset( Object userState )
  {
    state.reset( userState );
    state.vars = vars;    
  }
  
  public boolean step()
  {
    char[] code = (char[])get( state.codeLine );
    params.clear();
    commands.executeLine( code, state, vars, params ); 
      
    if( state.codeLine == -1 )
      return false;
    
    return true;
  }
  
  public boolean run( Object userState ) {
    return run( userState, DEFAULT_TERMINATION_COUNT );
  }
  
  public boolean run( Object userState, int terminationCount )
  {
    reset( userState );
    
    int codeLines = size();
    int counter   = 0;
    
    while( state.codeLine >= 0         && 
           state.codeLine <  codeLines && 
           counter        <  terminationCount )
    {
      if( !step() ) return false;
      ++counter;
    }
    
    return true;
  }
  
  private BufferedReader createReader(String filename) {
    try {
      InputStream is = null;
      try {
        is = new FileInputStream(filename);
      } catch( FileNotFoundException e ) {
        System.err.println(filename + " does not exist or could not be read");
        return null;
      }

      return new BufferedReader( new InputStreamReader(is) );
      
    } catch (Exception e) {
      if (filename == null) System.err.println( "Filename passed to reader() was null"     );
      else                  System.err.println( "Couldn't create a reader for " + filename );
    }
    return null;
  }
}