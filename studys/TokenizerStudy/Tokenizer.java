//!package makerscript.util;

import java.util.ArrayList;
import java.util.List;

//@TODO:
// Token / Tokenizer / StringTree should be pulled out into a study and refactored
// then reintegrated and used as an initial parser for makerscript


public class Tokenizer
{
  private String[]     delimeters = null;
  private String[][]   nesting    = null;
  private List<Token>  tokens     = new ArrayList<Token>();
  
  // ------------------------------------------------------------------------ //
  public Tokenizer( String[] delimeters )                       { this(delimeters, null); }
  public Tokenizer( String[] delimeters, String[][] nesting )
  {
    this.delimeters = delimeters; 
    this.nesting    = nesting;
    
    // Expand the delimiters array to contain all of the nesting elements
    int  oldLength  = this.delimeters.length;
    int  newLength  = this.delimeters.length + ((this.nesting != null)? this.nesting.length * 2 : 0);
    
    expandDelims( newLength );
    
    if( this.nesting != null ) {
      for( int i=0; i<this.nesting.length; ++i ) {
        // Assign the sub array
        this.nesting[i] = nesting[i];
        // Append the sub array to the delimiters
        this.delimeters[ oldLength + i*2 + 0] = this.nesting[i][0];
        this.delimeters[ oldLength + i*2 + 1] = this.nesting[i][1];
      }
    }
  }
  
  // ------------------------------------------------------------------------ //
  public void parse( String strLine ) {
    reset ( );
    feed  ( strLine );
  }
  public void parse( String[] strFile ) {
    reset ( );
    feed  ( strFile );
  }
  
  // ------------------------------------------------------------------------ //
  public int     getTokenCount ( )           { return tokens.size(); }
  public Token   getToken      ( int index ) { return index < tokens.size()? tokens.get(index) : null; }
  
  // ------------------------------------------------------------------------ //
  public int findDelimeter ( String strDelim ) {
    for( int i=0; i<delimeters.length; ++i )
      if( delimeters[i].equals( strDelim ) )
        return i;
    return -1;
  }
  
  // ------------------------------------------------------------------------ //
  private void expandDelims( int nNewLength ) {
    String[] newDelims = new String[nNewLength];
    System.arraycopy(this.delimeters, 0, newDelims, 0, delimeters.length);    
    this.delimeters = newDelims;
  }
  
  // ------------------------------------------------------------------------ //
  public void reset() { tokens.clear(); }
  
  // ------------------------------------------------------------------------ //
  public static String toString( Token[] tokens ) {
    String strOut = "";
    for( int i=0; i<tokens.length; ++i ) {
      strOut += tokens[i].toString( );
    }
    return strOut;
  }
  
  // ------------------------------------------------------------------------ //
  public List<Token> getTokens        ( )            { return tokens; }
  public String      getDelim         ( int index )  { return ( index > -1 && index < delimeters.length ) ? delimeters[index] : null; }
  public boolean     isDelim          ( Token t )    { return t.nDelim > -1 && t.nDelim < this.delimeters.length; }
  public boolean     isNestingDelim   ( Token t )    { return (this.nesting != null)? (isDelim(t) && t.nDelim >= this.delimeters.length - this.nesting.length*2) : false; }
  
  // ------------------------------------------------------------------------ //
  public int getNestingIndex  ( Token t ) {
	
    // Make sure we're looking for nesting tokens
    if( this.nesting == null ) return -1;
    
    // Make sure that this is a nesting delimeter
    if( !isNestingDelim(t) ) return -1;
    
    // Get the index into the nesting array
    return t.nDelim - (this.delimeters.length - this.nesting.length*2);
  }
  // ------------------------------------------------------------------------ //
  public StringTree buildTree() {
	  
    StringTree        tree        = new StringTree();
    StringTree        node        = tree;
    List<StringTree>  nodeStack   = new ArrayList<StringTree>();
    
    // We need to loop through all the tokens that have been parsed
    for( int nTokIndex=0; nTokIndex < tokens.size(); ++nTokIndex ) {
      
      Token tok = (Token)tokens.get( nTokIndex );
      
      // Check if this is a regular token (not a delimiter)
      if( !isDelim( tok ) ) {
        node.tokens.add( tok );
        continue;
      }

      // Now check if this is a standard delimiter
      if( nesting == null || !isNestingDelim( tok ) )
      {
        node.tokens.add( tok );
        continue;
      }
      
      if( nesting != null )
      {
        // Must be a nesting delimeter, so get the nesting index
        int nNestIndex = getNestingIndex( tok );
        
        // If this is an even index, we're going into our children
        if( nNestIndex % 2 == 0 )
        {
          StringTree child = new StringTree();
          
          child.tokStart   = new Token( tok.nDelim,   this );
          child.tokEnd     = new Token( tok.nDelim+1, this );
          
          node.tokens.add   ( null  );  // We must add a null so that we can denote the position of the child tree
          node.children.add ( child );  // Add the new child tree into the children nodes
          nodeStack.add     ( node  );  // Add the current node into the node stack
          
          node = child;                 // Finally replace the current node with the new child tree
        }
        // Otherwise we're jumping back up into our parents
        else if( nodeStack.size() > 0 )
        {
          node = (StringTree)nodeStack.remove( nodeStack.size()-1 );
        }
      }
    }
    
    return tree;
  }
  // ------------------------------------------------------------------------ //
  protected void feed( String[] strFile ) {
    for( int i=0; i<strFile.length; ++i )
      feed( strFile[i] );
  }
  protected void feed( String strLine ) {
	
    String strTokenAcc     = "";
    String strDelimAcc     = "";
    int    nCurrentDelim   = -1;
    int    nLineLength     = strLine.length();
    
    // Tokenize by linearly itterating through each character in the string
    for( int nCharIndex  = 0; nCharIndex < nLineLength; ++nCharIndex )
    {
      // Get the current character
      char nChar = strLine.charAt( nCharIndex );
      
      // Accumulate into the current delimeter
      strDelimAcc += nChar;
      
      // Check if the current delim accumulator matches a valid delim
      nCurrentDelim  = delimetersContain( strDelimAcc );
      
      // If the latest char invalidates the delimeter accumulator
      if( nCurrentDelim == -1 )
      {
        // Try removing characters from the left hand side and re-check to see if we can find
        //  a valid delimeter, or break if we only have the latest accumulated char left.
        while( nCurrentDelim == -1 && strDelimAcc.length() > 1 )
        {
          strDelimAcc   = strDelimAcc.substring( 1, strDelimAcc.length()-1 );
          nCurrentDelim = delimetersContain( strDelimAcc );
        }
      }
      
      // If it was found
      if( nCurrentDelim > -1 )
      {
        // Check if our token accumulater is not empty
        if( strTokenAcc.length() > 0 )
        {
          // Add the new token and clear the token accumulator!
          tokens.add( new Token( strTokenAcc, this ) );
          strTokenAcc = "";
        }
        
        // And if the delimeters match then add it to the token list
        if( delimeters[nCurrentDelim].length() == strDelimAcc.length() )
        {
          tokens.add( new Token( nCurrentDelim, this ) );
          
          // Reset both the token and delimeter accumulators
          strTokenAcc = "";
          strDelimAcc = "";
        }
      }
      // Otherwise reset the current delimeter accumulator,
      //  and accumulate into the current token string.
      else
      {
        strDelimAcc  = "";
        strTokenAcc += nChar;
      }
    }
    
    // If we have accumulated a token at the end of a line, then add it!
    if( !strTokenAcc.equals( "" ) )
      tokens.add( new Token( strTokenAcc, this ) );
    
    
    // Adds a new line token after each line is fed into the tokenizer.
    int nNewLineDelim = findDelimeter("\n");
    if( nNewLineDelim > 0 )
      tokens.add( new Token( nNewLineDelim, this ) );
  }
  
  // ------------------------------------------------------------------------ //
  // Returns -1 for no
  // Returns index of first container otherwise
  private int delimetersContain( String strPartial ) {
	
    int nPartLen = strPartial.length();
    
    // Search through all of the delimeters
    for( int nDelimIndex=0; nDelimIndex<delimeters.length; ++nDelimIndex )
    {
      String strDelim  = delimeters[ nDelimIndex ];
      int    nDelimLen = strDelim.length();
      
      // If the partial delim we're searching for is longer than 
      //  this delimeter then we know this one isn't the one, continue!
      if( nPartLen > nDelimLen ) continue;
      
      boolean bFound = true;
      
      // Check all the leading characters
      for( int nPartIndex=0; nPartIndex<nPartLen; ++nPartIndex ) {
        // Bail as soon as we reach a char that is not equal.
        if( strDelim.charAt( nPartIndex ) != strPartial.charAt( nPartIndex ) ) {
          bFound = false;
          break;
        }
      }
      
      // Since we found the delimeter, return it
      if( bFound ) return nDelimIndex;
    }
    
    return -1;
  }
}