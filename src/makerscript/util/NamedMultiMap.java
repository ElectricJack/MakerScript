package makerscript.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class NamedMultiMap<T extends Nameable> extends AbstractCollection<T> {
  private static final long serialVersionUID = 1L;
  private ArrayList<T>                  indexToValue = new ArrayList<T>();
  private HashMap<String, ArrayList<T>> nameToValue  = new HashMap<String, ArrayList<T>>();

  // ------------------------------------------------------------------------------------------------------------- //  
  public int size() { return indexToValue.size(); }

  // ------------------------------------------------------------------------------------------------------------- //
  public ArrayList<T> getAll() {
    return indexToValue;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //  
  public boolean add( T value ) {
    if( nameToValue.containsKey( value.getName() ) ) {
      nameToValue.get( value.getName() ).add( value );
    } else {
      ArrayList<T> values = new ArrayList<T>();
                   values.add( value );
      nameToValue.put( value.getName(), values );
    }
    indexToValue.add( value );
    return true;
  }

  // ------------------------------------------------------------------------------------------------------------- //
  public void clear() {
    indexToValue.clear();
    nameToValue.clear();
  }
  
  // ------------------------------------------------------------------------------------------------------------- //  
  public ArrayList<T> get( String name ) {
    if( nameToValue.containsKey( name ) ) {
      return (ArrayList<T>)nameToValue.get( name );
    }
    return null;
  }
  
  // ------------------------------------------------------------------------------------------------------------- //
  public T get( int index ) {
    if( index < 0 || index >= indexToValue.size() ) return null;
    return indexToValue.get( index );
  }

  // ------------------------------------------------------------------------------------------------------------- //
  @Override
  public Iterator<T> iterator() {
    return indexToValue.iterator();
  }
  
}
