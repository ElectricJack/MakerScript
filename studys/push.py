import os
import shutil
import sys

source_base_path  = "../../src/"   # Relative path to makerscript's source folder, from inside the study's working folder
class_def_file    = 'classes.txt'  # The name of the class definition file where required classes are listed.
classes           = []             # Classes to process

#------------------------------------------------------------------------#
def get_class_file_path( class_path ):
  rel_file_path = class_path.replace('.','/') + '.java'
  file_path     = os.path.realpath( source_base_path + rel_file_path )
  return file_path
#------------------------------------------------------------------------#
def process_file( src_class ):
  src_file   = get_class_file_path( src_class )
  local_file = os.path.split( src_file )[1]
  
  if os.path.isfile( local_file ):
    print '\n-----------------------------------------------------------'
    print src_class + ' (' + local_file + ')'

    # Check the file modified times, and make sure this file has changed
    #  more recently than the source file
    newer = True
    if os.path.isfile( src_file ):
      local_modified = os.path.getmtime( local_file )
      src_modified   = os.path.getmtime( src_file )
      newer = local_modified > src_modified
    
    if newer:
    
      # - Load the entire file
      with open( local_file, 'r' ) as file:
        file_lines = file.readlines()
      
      # - Process the file and uncomment the required lines
      index = 0
      for line in file_lines:
        if line.count( '//!import' ) == 1:
          if line.count( 'makerscript' ) == 1:
            file_lines[index] = line.partition('//!')[2]
        if line.count( '//!package' ) == 1:
          file_lines[index] = line.partition('//!')[2]
        index = index + 1

      # - Save the processed file
      with open( local_file, 'w' ) as file:
        for line in file_lines:
          file.write( line )
      
      # - Copy the file back
      shutil.copy( local_file, src_file )
      
    else:
      print 'Skipping... no relevent changes found.'
#------------------------------------------------------------------------#
def main():
  if len( sys.argv ) == 2:
    study_folder_name = sys.argv[1]
    os.chdir( study_folder_name )
    
    print 'Pushing content back for study: ' + study_folder_name
    if os.path.isfile( class_def_file ):
      # Load the study's file list, and push them into
      # a file queue.
      with open( class_def_file, 'r' ) as classes_file:
        classes = classes_file.readlines()
      
      # Pop files off the queue until there are none left
      while len(classes) > 0:
        src_class  = classes.pop().strip()
        process_file( src_class )
      
      # Finally, let's delete all the .java files in the study folder so it's clear
      #  that the changes have been pushed back
      for fname in os.listdir('.'):
        if fname.count( '.java' ) == 1:
          os.remove( fname )
      
    else:
      print 'Couldn\'t find class definition file for study. (' + study_folder_name + '/' + class_def_file + ')'
  else:
    print 'Usage:\npython push.py [StudyFolderName]'

if __name__ == "__main__":
  main()