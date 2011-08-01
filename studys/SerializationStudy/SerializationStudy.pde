/** Serialization.pde
 *  Jack Kern - http://www.jackkern.com
 *  
 *  An example demonstrating the advantages of XMLSerialization, including a 
 *  basic implementation.
 */
 

XMLSerializer store;

// ------------------------------------------------------------ // 
void setup() {
  
  // First create the serializer object
  store = new XMLSerializer( this );
  
  // Next register your serializable types so they
  //  can be instantiated on load. (Only required for list-serialization support)
  store.registerType( new Data() );
  store.registerType( new PVectorData() );
  
  // Finally let's test saving out some data, and loading it back in.
  testSaving();
  testLoading();

  // Fin.
  exit();  
}

// ------------------------------------------------------------ // 
void testLoading() {
  // First we create our data object, then use the serializer to load it, and finally
  //  we print it to the debug console to verify success!
  Data data = new Data();
  store.load( "data.xml", data );
  data.print();
}

// ------------------------------------------------------------ // 
void testSaving() { 
  // Notice it is possible to easily save a tree structure because "Data" types can
  //  store "Data" objects as thier own children.
  
  Data                    data =             new Data( 1, 2.0f, "root",    false, 10.0f,  20.0f, 30.0f );
                          data.children.add( new Data( 2, 1.0f, "child 1", true,   1.2f,   3.4f,  5.6f ) );
                          data.children.add( new Data( 3, 1.1f, "child 2", false, 40.0f, -10.1f, 62.0f ) );
  store.save( "data.xml", data );
  
// This code generates the following XML:
/*<root type="Data">
    <index type="int" value="1" />
    <scalar type="float" value="2.0" />
    <name type="string" value="root" />
    <active type="boolean" value="false" />
    <pos type="PVectorData">
      <x type="float" value="10.0" />
      <y type="float" value="20.0" />
      <z type="float" value="30.0" />
    </pos>
    <children type="ArrayList">
      <child_0 type="Data">
        <index type="int" value="2" />
        <scalar type="float" value="1.0" />
        <name type="string" value="child 1" />
        <active type="boolean" value="true" />
        <pos type="PVectorData">
          <x type="float" value="1.2" />
          <y type="float" value="3.4" />
          <z type="float" value="5.6" />
        </pos>
        <children type="ArrayList">
        </children>
      </child_0>
      <child_1 type="Data">
        <index type="int" value="3" />
        <scalar type="float" value="1.1" />
        <name type="string" value="child 2" />
        <active type="boolean" value="false" />
        <pos type="PVectorData">
          <x type="float" value="40.0" />
          <y type="float" value="-10.1" />
          <z type="float" value="62.0" />
        </pos>
        <children type="ArrayList">
        </children>
      </child_1>
    </children>
  </root>*/
}


