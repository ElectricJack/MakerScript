Tokenizer  toker;

void setup() {
  size(800,600);
  noLoop();
  
  println("Running tokenizer...");
  toker = new Tokenizer( new String[] { ",;" }, new String[][] { new String[] { "{", "}" } } );
  
  exit();
}

