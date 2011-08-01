package makerscript.geom.mesh.collada;

import java.util.ArrayList;

import makerscript.geom.mesh.MeshBuilder;



public class COLLADANode {
  public String                 name     = null;
  public float[]                matrix    = null;
  public ArrayList<COLLADANode> children  = null;
  public ArrayList<String>      instances = null;
  public COLLADAReader          parent;
  
  public COLLADANode( COLLADAReader parent ) { this.parent = parent; }
  
  public void add( COLLADANode child ) {
    if( children == null )
      children = new ArrayList<COLLADANode>();
    children.add( child );
  }
  
  public void instance( String id ) {
    if( instances == null )
      instances = new ArrayList<String>();
    instances.add( id );
  }
  
  public void convert( MeshBuilder builder ) {
    if( matrix != null ) {
      builder.pushMatrix();
      builder.applyMatrix( matrix );
    }
    
    if( instances != null ) {
      for( String id : instances ) {
        COLLADAMesh mesh = parent.getMesh( id );
        if( mesh != null )  mesh.convert( builder );
      }
    }
    
    if( children != null )
      for( COLLADANode node : children )
        node.convert( builder );
    
    if( matrix != null ) {
      builder.popMatrix();
    }
  }

}
