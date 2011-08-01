package makerscript.geom.mesh.collada;

import processing.core.PApplet;
import processing.xml.XMLElement;
import java.util.ArrayList;

import makerscript.geom.mesh.MeshBuilder;
import makerscript.util.Convertible;

public class COLLADAReader implements Convertible {

  public boolean                loaded = false;
  public ArrayList<COLLADAMesh> meshes = new ArrayList<COLLADAMesh>();
  public ArrayList<COLLADANode> roots  = new ArrayList<COLLADANode>();
  
  // ------------------------------------------------------------------------ //
  public COLLADAReader( PApplet app, String filePath ) {
    XMLElement xml  = new XMLElement( app, filePath );
    
    for( XMLElement child : xml.getChildren() ) {
      if      ( COLLADAHelpers.isNameOf(child, "asset"                 ) ) loadAssetData   ( child );
      else if ( COLLADAHelpers.isNameOf(child, "library_visual_scenes" ) ) loadVisualScene ( child );
      else if ( COLLADAHelpers.isNameOf(child, "library_geometries"    ) ) loadGeometries  ( child );
    }
        
    loaded = true;
  }
  // ------------------------------------------------------------------------ //
  public void convert( MeshBuilder builder ) {
    for( COLLADANode n : roots ) {
      n.convert( builder );
    }
  }
  // ------------------------------------------------------------------------ //
  protected COLLADAMesh getMesh( String id ) {
    for( COLLADAMesh m : meshes )
      if( ("#" + m.id).equals( id ) )
        return m;
        
    return null;
  }
  

  
  // ------------------------------------------------------------------------ //
  private void loadAssetData( XMLElement asset ) {
  }
  
  // ------------------------------------------------------------------------ //
  private void loadGeometries( XMLElement geometries ) {
    for( XMLElement geometry : geometries.getChildren() ) {
      String id = geometry.getString("id");
      for( XMLElement mesh : geometry.getChildren() )
        meshes.add( new COLLADAMesh( mesh, id ) );
    }
  }

  // ------------------------------------------------------------------------ //
  private void loadVisualScene( XMLElement scenes ) {
    for( XMLElement scene : scenes.getChildren() ) {
      XMLElement node = scene.getChild("node");
      if( node == null ) continue;
      roots.add( buildNodeTree( node ) );
    }
  }

  // ------------------------------------------------------------------------ //
  private COLLADANode buildNodeTree( XMLElement node ) {
    COLLADANode n      = new COLLADANode( this );   

    for( XMLElement child : node.getChildren() ) {
      if      ( COLLADAHelpers.isNameOf( child, "matrix"            ) ) n.matrix = COLLADAHelpers.getFloatArray( child );
      else if ( COLLADAHelpers.isNameOf( child, "node"              ) ) n.add( buildNodeTree( child ) );
      else if ( COLLADAHelpers.isNameOf( child, "instance_geometry" ) ) {
        
        String geomID = child.getString("url");
        n.instance( geomID );
      }
    }
 
    return n;
  }

}
