Tokenizer  toker;

void setup() {
  size(800,600);
  noLoop();
  
  println("Running tokenizer...");
  // new String[] { ",;" }, new String[][] { new String[] { "{", "}" } } 
  toker = new Tokenizer();
  toker.addTokens    ( "" );
  toker.addTokenPair (  "{", "}" );

  String[] file = loadStrings( "makerscript_example.mkrs" );
  
  
  exit();
}

