
class PVectorData extends PVector implements Serializable {
  public              PVectorData ( )                   { super(); } // Needed as copy-constructor trumps auto default-constructor creation
  public              PVectorData ( PVectorData other ) { this.x = other.x; this.y = other.y; this.z = other.z; }
  public String       getType     ( )                   { return "PVectorData"; }
  public PVectorData  clone       ( )                   { return new PVectorData(this); }
  public void         serialize   ( Serializer s )      {
    x = s.serialize("x", x);
    y = s.serialize("y", y);
    z = s.serialize("z", z);
  }
}

public class Data implements Serializable {
  private int               index;
  private float             scalar;
  private String            name;
  private boolean           active;
  private PVectorData       pos      = new PVectorData();
  private ArrayList< Data > children = new ArrayList< Data >();
  
  // ------------------------------------------------------------ //
  public String     getType ( ) { return "Data"; }
  public Data       clone   ( ) { return new Data( this ); }
  
  // ------------------------------------------------------------ //
  public void serialize( Serializer s ) {
    index    = s.serialize( "index",    index    ); // All the standard types must be re-assigned so thier values can be updated.
    scalar   = s.serialize( "scalar",   scalar   );
    name     = s.serialize( "name",     name     );
    active   = s.serialize( "active",   active   );
               s.serialize( "pos",      pos      ); // Serializable objects, don't need to be reassigned because they are passed-by-reference.
               s.serialize( "children", children ); // Children (ArrayList) must exist at this point
  }
  
  // ------------------------------------------------------------ //
  public Data()                                                                         { }
  public Data( int index, float scalar, String name, boolean active, float x, float y, float z ) {
    this.index  = index;
    this.scalar = scalar;
    this.name   = name;
    this.active = active;
    this.pos.set( x, y, z );
  }
  
  // ------------------------------------------------------------ //
  public Data( Data other ) {
    this.index  = other.index;
    this.scalar = other.scalar;
    this.name   = other.name;
    this.active = other.active;
    this.pos.set( other.pos );
    
    for( Data child : other.children )
      children.add( child.clone() );
  }
  
  // ------------------------------------------------------------ //
  public void print() {
    println( "index:  " + index   );
    println( "scalar: " + scalar  );
    println( "name:   " + name    );
    println( "active: " + active  );
    println( "pos:    " + pos     );
    
    for( Data child : children ) {
      child.print();
    }
  }
}
