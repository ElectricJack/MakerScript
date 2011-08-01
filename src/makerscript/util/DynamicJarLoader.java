package makerscript.util;


import java.net.URLClassLoader;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.io.FileInputStream;


public class DynamicJarLoader
{
  protected String strJarFile = "";
 
  public         DynamicJarLoader( String strJarFile ) { this.strJarFile = strJarFile; }
  
  
  public boolean Load()
  {
    if( strJarFile.equals("") )
      return false;
  
    try
    {
      URLClassLoader  urlLoader   = getURLClassLoader   ( strJarFile );
      JarInputStream  jis         = new JarInputStream  ( new FileInputStream( strJarFile ) );
      JarEntry        entry       = jis.getNextJarEntry ( );
      int             loadedCount = 0;
      int             totalCount  = 0;
   
      while( entry != null )
      {
        String name = entry.getName();
        if( name.endsWith( ".class" ) )
        {
          totalCount++;
          name = name.substring(0, name.length() - 6);
          name = name.replace('/', '.');
          
          System.out.print( "> " + name );
   
          try {
          
            urlLoader.loadClass( name );
            loadedCount++;
            System.out.println("\t- loaded");
            
          } catch( Throwable e ) {
          
            System.out.println( "\t- not loaded" );
            System.out.println( "\t " + e.getClass().getName() + ": " + e.getMessage() );
            
          }
   
        }
        entry = jis.getNextJarEntry();
      }
    }
    catch( Exception e )
    {
      System.out.println( "Error loading jar file :\t" + strJarFile );
      return false;
    }
    
    return true;
  }
  
  private static URLClassLoader getURLClassLoader( String strJarPath )
  {
    try { return new URLClassLoader( new URL[] { new URL( "file", null, strJarPath ) } ); }
    catch( Exception eURL ) 
    {
      return null;
    }
  }
}
