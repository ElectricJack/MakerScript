package makerscript.geom.mesh.collada;

import processing.xml.XMLElement;

public class COLLADAHelpers {

  //------------------------------------------------------------------------ //
  static boolean  isNameOf        ( XMLElement elm, String name )                 { return elm.getName().equals(name);  }
  static boolean  isAttribOf      ( XMLElement elm, String attrib, String value ) { return elm.getString(attrib).equals(value); }
  static boolean  isAttribOf      ( XMLElement elm, String attrib, int    value ) { return elm.getInt(attrib) == value; }
  static String[] getContentArray ( XMLElement elm )                              { return elm.getContent().split(" "); }
  //------------------------------------------------------------------------ //
  static int[] getIntArray ( XMLElement elm ) {
   String[] values = getContentArray(elm);
   int[]    ints   = new int[values.length];
   for( int i=0; i<values.length; ++i )
     ints[i] = Integer.parseInt( values[i] );
   return ints;
  }
  //------------------------------------------------------------------------ //
  static float[] getFloatArray ( XMLElement elm ) {
   String[] values = getContentArray(elm);
   float[]  floats = new float[values.length];
   for( int i=0; i<values.length; ++i )
     floats[i] = Float.parseFloat( values[i] );
   return floats;
  }
}
