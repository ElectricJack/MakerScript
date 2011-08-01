
import os
import shutil
import sys
import subprocess

sixty_four_bit = 0


def linux():
  print 'No linux support at this time, sorry!'

def win32():
  print 'Building for windows...'
  os.environ['JAVA_HOME'] = 'D:\Java\jdk1.6.0_23'
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


def main():
  print ' '
  print ' +---------------------------------------------+'
  print ' | BUILDING MAKERSCRIPT                        |'
  print ' +---------------------------------------------+'
  print ' '
  
  if   sys.platform.startswith('linux') : linux()
  elif sys.platform == 'win32'          : win32()
  elif sys.platform == 'darwin'         : osx()
  

if __name__ == "__main__":
  main()
