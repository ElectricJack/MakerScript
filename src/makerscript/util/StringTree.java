package makerscript.util;

import  java.util.ArrayList;
import  java.util.List;

public class StringTree {
  
  public   Token            tokStart  = null;
  public   Token            tokEnd    = null;
  public   List<Token>      tokens    = new ArrayList<Token>();
  public   List<StringTree> children  = new ArrayList<StringTree>();
  private  Token[]          flat      = null;
  
  // ---------------------------------------------------------------------------- //
  public static StringTree build( String[] srcLines, String[] delimeters, String[][] nesting ) {
    Tokenizer toker = new Tokenizer( delimeters, nesting );
    
    for( int i=0; i<srcLines.length; ++i )
      toker.feed( srcLines[i] );
  
    return toker.buildTree();
  }
  
  // ---------------------------------------------------------------------------- //
  public String toString() { return Tokenizer.toString( this.flatten() ); }
  
  // ---------------------------------------------------------------------------- //
  public boolean matches( int nTokenOffset, StringTree matchTree ) { //TODO: implement
    //Token[] flatTreeHaystack = this.flatten();
    //String  strNeedle        = matchTree.toString();
    //return  true;
    
    return false;
  }
  // ---------------------------------------------------------------------------- //
  public Token[] flatten() {
    // If we already have a cached version return that
    if( flat != null ) return flat;
    
    // Create our token accumulator
    ArrayList<Token> tokAccum = new ArrayList<Token>();
    
    // Recursively flatten the tree into the token accumulator
    flatten( tokAccum );
    
    // Allocate the new list and copy out all the elements
    flat = new Token[ tokAccum.size() ];
    for( int i=0; i<flat.length; ++i )
      flat[i] = tokAccum.get(i);
    
    // Return the flat list of tokens
    return flat;
  }
  // ---------------------------------------------------------------------------- //
  public void flatten( ArrayList<Token> tokAccum ) {
    int nChild = 0;
    for( int i=0; i<tokens.size(); ++i )
    {
      Token tok = (Token)tokens.get(i);
      if( tok == null )
      {
        StringTree child = (StringTree)children.get( nChild );
        tokAccum.add( child.tokStart );
        child.flatten( tokAccum );
        tokAccum.add( child.tokEnd );
        ++nChild;
      }
      else
      {
        tokAccum.add( tok );
      }
    }
  }
}
