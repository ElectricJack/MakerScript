package makerscript.util;

//@TODO:
//Token / Tokenizer / StringTree should be pulled out into a study and refactored
//then reintegrated and used as an initial parser for makerscript

public class Token
{
  public String      strIdent = null;  // This will be set to a string if this is an identifier
  public int         nDelim   = -1;    // This will be set to >= 0 if this is a delimeter
  public Tokenizer   parent   = null;
  
  // ------------------------------------------------------------------------ //
  public             Token    ( String strIdent, Tokenizer parent ) { this.strIdent = strIdent; this.parent = parent; }
  public             Token    ( int    nDelim,   Tokenizer parent ) { this.nDelim   = nDelim;   this.parent = parent; }
  
  // ------------------------------------------------------------------------ //
  public boolean     equals       ( Token  other    ) { return isDelimeter() ? isDelimeter( other.nDelim                 ) : this.strIdent.equals( other.strIdent ); }
  public boolean     equals       ( String strTok   ) { return isDelimeter() ? isDelimeter( parent.findDelimeter(strTok) ) : this.strIdent.equals( strTok );         }
  public boolean     isDelimeter  ( )                 { return this.nDelim > -1; }
  public boolean     isDelimeter  ( int nDelim )      { return this.nDelim == nDelim; }
  
  // ------------------------------------------------------------------------ //
  public String toString () {
    if( nDelim == -1 ) return strIdent;
    else               return parent.getDelim( nDelim );
  }
  // ------------------------------------------------------------------------ //
  public void print  ( ) {
    if( nDelim == -1 ) System.out.println( "\"" + strIdent + "\"" );
    else               System.out.println( "["  + nDelim   + "]" );
  }
}
