
import os
import sys
import subprocess

sixty_four_bit = 0

def linux():
  print 'No linux support at this time, sorry!'

def win32():
  os.chdir(os.path.abspath('app/win32'))
  subprocess.call( ['MakerScript.exe'] )

def osx():
  subprocess.call( ['open', '-a', os.path.abspath('app/osx/MakerScript.app')] )
  
def main():
  print ' '
  print ' +---------------------------------------------+'
  print ' | RUNNING MAKERSCRIPT                         |'
  print ' +---------------------------------------------+'
  print ' '
  
  if   sys.platform.startswith('linux') : linux()
  elif sys.platform == 'win32'          : win32()
  elif sys.platform == 'darwin'         : osx()
  

if __name__ == "__main__":
  main()
