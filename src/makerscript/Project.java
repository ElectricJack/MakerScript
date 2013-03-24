package makerscript;

class Project {


  // ------------------------------------------------------------------------ //
/*
  public boolean setProjectPath( String pathValue ) {
    projectPath = "";
    
    // First clean the path of common problems
    if( pathValue.contains("\\")                        ) pathValue = pathValue.replaceAll( "\\\\", "/" );
    if( pathValue.charAt( pathValue.length()-1 ) != '/' ) pathValue += "/";  
    if( pathValue.charAt( 0 ) != '/'                    ) pathValue = "/" + pathValue;  
    pathValue = app.sketchPath( "projects" + pathValue + "commands.txt" );
    if( pathValue.contains("\\")                        ) pathValue = pathValue.replaceAll( "\\\\", "/" );

    // Check to see if the file exists, if it does we're done here.
    commandsFilePath = pathValue;
    File commandsFile = new File( commandsFilePath );
    if( commandsFile.exists() ) {
      projectPath = commandsFilePath.substring(0,commandsFilePath.lastIndexOf("/"));
      return true;
    }

    // Otherwise check if the default commands file exists
    commandsFilePath = app.sketchPath("commands.txt");
    commandsFile     = new File( commandsFilePath );
    if( commandsFile.exists() ) {
      projectPath = commandsFilePath.substring(0,commandsFilePath.lastIndexOf("/"));
      return true;
    }
      
    // If not, then we can't continue; return false.
    return false;
  }*/

/*
  private boolean initProject() {
    String[] setupFile = loadStrings("setup.txt");
    
    boolean result = true;
    for( String line : setupFile ) {
      if( line != null && line.length() > 0 ) {
        
        String[] initTokens = line.split(":");
        
        if( initTokens.length == 2 ) {
          
          String variable = initTokens[0].trim();
          String value    = initTokens[1].trim();
          
          if      ( variable.equals( "project" ) ) result = result && state.setProjectPath( value );
          //else if ( variable.equals( "" ) )
          
          if( !result ) break;
        }
      }
    }

    return result;
  }*/

}