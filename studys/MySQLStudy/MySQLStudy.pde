
MySQLDoc testConnection;
 
void setup() {
  size(100,100,P2D);
  noLoop();
  
  //testConnection = new MySQLDoc( this, "dev1", "Rollercoaster" );
  testConnection = new MySQLDoc();
  testConnection.init(this, "dev1", "Bucket");
  println( testConnection.size() );
  testConnection.add("load file command".toCharArray());
  println( testConnection.size() );
  
  exit();
}

