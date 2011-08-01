import os
import shutil
import sys

source_base_path  = "../../src/"   # Relative path to makerscript's source folder, from inside the study's working folder
class_def_file    = 'classes.txt'  # The name of the class definition file where required classes are listed.
processed_classes = []             # Classes already processed by the script
classes           = []             # Classes already discovered, to process

#------------------------------------------------------------------------#
def get_class_file_path( class_path ):
  rel_file_path = class_path.replace('.','/') + '.java'
  file_path     = os.path.realpath( source_base_path + rel_file_path )
  return file_path
#------------------------------------------------------------------------#
def process_class( src_class ):
  # - First copy the source file to a local copy
  src_file   = get_class_file_path( src_class )
  local_file = os.path.split( src_file )[1]
  print '\n-----------------------------------------------------------'
  print src_class + ' (' + local_file + ')'
  shutil.copy( src_file, local_file )
  if os.path.isfile( local_file ): print 'File copied successfully.\n'
  
  # - Load the entire file
  file = open( local_file, 'r' )
  file_lines = file.readlines()
  file.close()
  
  # - Collect a list of dependent files by parsing all the
  #   import statements in the file, and looking for any
  #   files that are part of any makerscript package. Put those files
  #   in the queue
  # - Comment out the package statement
  # - Comment out any imports from any makerscript package

  index = 0
  for line in file_lines:
    if line.count( 'import' ) == 1:
      if line.count( 'makerscript' ) == 1:
        file_lines[index] = '//!' + line
        import_class      = line.partition('import')[2].strip(' \n\t\r;')
        add_class( import_class )
    if line.count( 'package' ) == 1:
      file_lines[index] = '//!' + line
    index = index + 1
  
  # - Save the local file back out
  with open( local_file, 'w' ) as file:
    for line in file_lines:
      file.write(line)
#------------------------------------------------------------------------#
def add_class( src_class ):
  try:
    if processed_classes.index( src_class ) >= 0:
      print '  - Already processed ' + src_class
      return
  except ValueError: pass
  try:
    if classes.index( src_class ) >= 0:
      print '  - Already added ' + src_class
      return
  except ValueError: pass
  classes.insert(0,src_class)
#------------------------------------------------------------------------#
def main():
  global classes
  if len( sys.argv ) == 2:
    study_folder_name = sys.argv[1]
    os.chdir( study_folder_name )
    
    print 'Pulling content for study: ' + study_folder_name
    
    if os.path.isfile( class_def_file ):
      # Load the study's file list, and push them into
      # a file queue.
      with open( class_def_file, 'r' ) as classes_file:
        classes = classes_file.readlines()
      
      # Pop files off the queue until there are none left
      while len(classes) > 0:
        src_class  = classes.pop().strip()
        processed_classes.append( src_class )
        process_class( src_class )
    else:
      print 'Couldn\'t find class definition file for study. (' + study_folder_name + '/' + class_def_file + ')'
  else:
    print 'Usage:\npython pull.py [StudyFolderName]'

if __name__ == "__main__":
  main()