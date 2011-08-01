
;--------------------------------
!include MUI2.nsh


!define MUI_ICON "..\MakerScript.ico"
;!define MUI_UNICON "..\MakerScript.ico"
!define MUI_HEADERIMAGE
!define MUI_HEADER_TRANSPARENT_TEXT
!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH 
!define MUI_HEADERIMAGE_BITMAP "..\images\MakerScriptInstall.bmp"
!define MUI_HEADERIMAGE_UNBITMAP_NOSTRETCH 
!define MUI_HEADERIMAGE_UNBITMAP "..\images\MakerScriptInstall.bmp"


!define APP_NAME         "MakerScript"
!define APP_PLATFORM     "win64"
!define APP_DIR          "..\..\app"
!define APP_DIR_PLATFORM "${APP_DIR}\${APP_PLATFORM}"
!define APP_LICENSE      "..\makerscript_license.txt"



Name ${APP_NAME}                                              ; The name of the installer
OutFile "${APP_DIR}\ms_setup_${APP_PLATFORM}.exe"             ; The file to write
InstallDir $PROGRAMFILES\${APP_NAME}                          ; The default installation directory
InstallDirRegKey HKLM "Software\${APP_NAME}" "Install_Dir"    ; Registry key to check for directory (so if you install again, it will overwrite the old one automatically)
RequestExecutionLevel admin ; Request application privileges for Windows Vista

;----------------------------------------------------------------
; Load our UI customizations
!insertmacro MUI_LANGUAGE "English" 

;----------------------------------------------------------------
; Pages
!insertmacro MUI_PAGE_LICENSE ${APP_LICENSE}
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;----------------------------------------------------------------
; The stuff to install
Section "${APP_NAME} (required)"

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  CreateDirectory "$INSTDIR\projects"
  CreateDirectory "$INSTDIR\examples"
  
  ; Put files there
  File /r "${APP_DIR_PLATFORM}\*"
  ;CopyFiles "${APP_DIR_PLATFORM}\*" "$INSTDIR\"
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\${APP_NAME} "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayName" "${APP_NAME}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}${APP_NAME}" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

;----------------------------------------------------------------
; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\${APP_NAME}"
  CreateShortCut "$SMPROGRAMS\${APP_NAME}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${APP_NAME}\${APP_NAME}.lnk" "$INSTDIR\${APP_NAME}.exe" "" "$INSTDIR\${APP_NAME}.exe" 0
  
SectionEnd

;----------------------------------------------------------------
; Uninstaller
Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
  DeleteRegKey HKLM SOFTWARE\${APP_NAME}

  ; Remove files and uninstaller
  Delete $INSTDIR\*

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\${APP_NAME}\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\${APP_NAME}"
  RMDir /r "$INSTDIR\*"
  RMDir "$INSTDIR"

SectionEnd
