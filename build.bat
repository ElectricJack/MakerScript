ECHO OFF
CLS
SET JAVA_HOME=D:\Java\jdk6ee\jdk
REM SET JAVA_HOME=C:\Program Files\Java\jdk6
SET ANT_HOME=.\tools\apache-ant-1.8.2
SET PATH=.\tools\apache-ant-1.8.2\bin;D:\Java\jdk6ee\jdk\bin;"C:\Program Files (x86)\NSIS";%PATH%
REM SET PATH=.\tools\apache-ant-1.8.2\bin;"C:\Program Files\Java\jdk6\jdk\bin";"C:\Program Files (x86)\NSIS";%PATH%

ant -buildfile "res/build/ant-build.xml"
REM ant -buildfile "res/config/ant-build-win32.xml"
REM ant -buildfile "res/config/ant-build-win64.xml"
REM ant -buildfile "res/config/ant-build-osx.xml"
