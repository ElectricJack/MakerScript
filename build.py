
import os
import shutil
import sys
import subprocess
import Tkinter, tkFileDialog


sixty_four_bit = 0


def linux():
  print 'No linux support at this time, sorry!'

def win32():
  print 'Building for windows...'
  #os.environ['JAVA_HOME'] = 'D:\Java\jdk1.6.0_23'
  
  buildfile_path          = '\"' + os.path.abspath( 'res/build/ant-build.xml' ) +'\"'
  
  if sixty_four_bit : 
    print '(64 bit)\n\n'
    build_target = 'build_win64'
  else:
    print '(32 bit)\n\n'
    build_target = 'build_win32'
  
  subprocess.call( ['python','tools/apache-ant-1.8.2/bin/runant.py'
                   ,'-buildfile', buildfile_path, build_target] )

def osx():
  print 'Building for osx...'
  buildfile_path          = '\"' + os.path.abspath( 'res/build/ant-build.xml' ) +'\"'
  build_target            = 'build_osx'
  subprocess.call( ['python','tools/apache-ant-1.8.2/bin/runant.py'
                   ,' -buildfile',buildfile_path, build_target] )

def get_java_home():
  root = Tkinter.Tk()
  root.withdraw()
  dirname = tkFileDialog.askdirectory( parent = root
                                     , initialdir = "/"
                                     , title = 'Please select your JDK (Not JRE!) installation directory:' )
  return dirname
  
def main():
  print ' '
  print ' +---------------------------------------------+'
  print ' | BUILDING MAKERSCRIPT                        |'
  print ' +---------------------------------------------+'
  print ' '
  
  if not os.environ.has_key('JAVA_HOME'):
    java_home_file = os.path.abspath('res/java_home.txt')
    java_home      = ''
    if os.path.isfile(java_home_file):
      with open(java_home_file,'r') as file:
        java_home = file.readline().strip();
    else:
      java_home = get_java_home()
      with open(java_home_file,'w') as file:
        file.write( java_home );
    os.environ['JAVA_HOME'] = java_home

  if   sys.platform.startswith('linux') : linux()
  elif sys.platform == 'win32'          : win32()
  elif sys.platform == 'darwin'         : osx()
  

if __name__ == "__main__":
  main()
