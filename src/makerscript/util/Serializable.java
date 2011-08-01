package makerscript.util;

public interface Serializable {
  
  public String       getType   ( );
  public Serializable clone     ( );
  public void         serialize ( Serializer s );
  
}
