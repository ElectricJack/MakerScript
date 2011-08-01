package makerscript.util;


public class Describable //implements Serializable
{
  private    String  name         = "";
  private    String  description  = "";
  
  // ------------------------------------------------------------------------------------------ //
  public               Describable    ( ) { }
  public               Describable    ( String name, String description ) {
    this.name         = name;
    this.description  = description;
  }
  public               Describable    ( Describable other ) {
    this.name         = other.name;
    this.description  = other.description;
  }
  
  public String       getType   ( ) { return "Describable"; }
  //public Serializable clone     ( ) { return new Describable(this); }
  
  // ------------------------------------------------------------------------------------------ //
  protected   void     setName        ( String name        ) { this.name        = name;        }
  protected   void     setDescription ( String description ) { this.description = description; }
  public      String   getName        ( )                    { return name;         }
  public      String   getDescription ( )                    { return description;  }
  
  // ------------------------------------------------------------------------------------------ //
  /*public      void     serialize( Serializer s ) {
    name         = s.serialize( "name",        name );
    description  = s.serialize( "description", description );
  }*/
}