
DrawWriter drawer;
DXFReader  reader;

void setup() {
  size( 800, 600, P3D );
  
  drawer = new DrawWriter( g );
  reader = new DXFReader( dataPath("fet_card.dxf") );  
}

void draw() {
  background ( 255 );
  stroke     ( 0 );
  translate  ( width / 2, height / 2 );
  scale      ( mouseX );
  translate  ( -0.5, -0.5 );
  
  reader.convert( drawer );
}
