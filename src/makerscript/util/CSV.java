package makerscript.util;

import processing.core.PApplet; //TODO: Get rid of CSV's dependency on PApplet
import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;


public class CSV
{
  protected Tokenizer    tok    = null;
  protected List<CSVRow> rows   = new ArrayList<CSVRow>();  
  protected int          cols   = 0;  
  protected PApplet      parent = null;
  
  // --------------------------------------------- //
  public CSV( PApplet parent )                     { this(parent, null); }
  public CSV( PApplet parent, String strFilePath )
  {
    this.parent = parent;
  
    String[] strDelims = new String[] { ",", "\n" };
             tok       = new Tokenizer( strDelims );
    
    load( strFilePath );
  }  
  

  // --------------------------------------------- //
  public int getRows() { return rows.size(); }
  public int getCols() { return cols;        }
  
  // --------------------------------------------- //
  public String get( int nCol, int nRow )
  {
    if( nCol < 0 || nCol >= getCols() ||
        nRow < 0 || nRow >= getRows() )
        return "";
        
    CSVRow row = (CSVRow)rows.get( nRow );
    return row.get( nCol );
  }
  
  // --------------------------------------------- //
  public boolean set( int nCol, int nRow, String strField )
  {
    if( nCol < 0 || nRow < 0 )
        return false;
    
    boolean bSuccess = false;
    
    if( nRow < getRows() )
    {
      CSVRow     row = (CSVRow)rows.get( nRow );
      bSuccess = row.set( nCol, strField );
    }
    else
    {
      for( int i=getRows(); i<nRow; ++i )
        rows.add( new CSVRow() );
     
       CSVRow     row = new CSVRow();
       bSuccess = row.set( nCol, strField );
       rows.add ( row );
    }
    
    return bSuccess;
  }  
  
  // --------------------------------------------- //
  public boolean save( String strFilePath )
  {
    PrintWriter output = parent.createWriter( strFilePath );
    if( output == null ) return false;
    
    for( int i=0; i<getRows(); ++i )
    {
      CSVRow          row = (CSVRow)rows.get(i);
      output.println( row.toString() );
    }
    
    output.flush();
    output.close();
    
    return true;
  }
  public boolean load( String strFilePath )
  {
    if( strFilePath == null ) return false;
    
    rows.clear();
        
    String[] strFile   = parent.loadStrings( strFilePath );
    
    // Make sure the file is valid
    if( strFile == null ) return false;
  
    tok.parse( strFile );
    
    String  strPreviousCol = "";
    CSVRow  currentRow     = new CSVRow();
    int     nCols          = 0;
    boolean bInsideQuote   = false;
    
    for( int i=0; i<tok.getTokenCount(); ++i )
    {
      Token t = tok.getToken(i);
      
      if( t.isDelimeter( 0 ) && !bInsideQuote ) // Comma
      {
        currentRow.add( strPreviousCol );
        strPreviousCol = "";
        
        // Update the current column counter
        ++nCols;
      }
      else if( t.isDelimeter( 1 ) && !bInsideQuote ) // New Line
      {
        currentRow.add( strPreviousCol );
        strPreviousCol = "";
        
        // Update the current column counter
        ++nCols;
        
        rows.add( currentRow );
        currentRow = new CSVRow();
        
        // Track the largest number of columns, reset the column counter
        cols = nCols > cols? nCols : cols;
        nCols = 0;
        
      }
      else
      {
        // If this field contains any quotes, toggle if we're inside quotes that many times
        String strTok = t.toString();
        int    nInd   = 0;
        while( (nInd = strTok.indexOf("\"", nInd) + 1) >= 1 )
          bInsideQuote = !bInsideQuote;

        strPreviousCol += strTok;
      }
    }
    
    return true;
  }

  
  // ======================================================================== //
  protected class CSVRow {
	  
    private  List<String> cols = new ArrayList<String>();
    
    // --------------------------------------------- //
    public            CSVRow     ()                          {}
    public   void     add        ( String strCol )           { cols.add( strCol ); }
    public   int      getCols    ()                          { return cols.size(); }
    public   String   get        ( int nCol )                { return (nCol < getCols())? (String)cols.get(nCol) : ""; }
    public   boolean  set        ( int nCol, String strCol ) {
      if( nCol < getCols() ) cols.set( nCol, strCol );
      else {
        for( int i=getCols(); i<nCol; ++i )
          cols.add("");
        cols.add(strCol);
      }
      
      return true;
    }
    
    public String toString() {
      String strLine = "";
      for( int i=0; i<cols.size(); ++i )
      {
        strLine += ( i > 0 )? "," : "";
        strLine += (String)cols.get(i);
      }
      return strLine;
    }
  }  
}
