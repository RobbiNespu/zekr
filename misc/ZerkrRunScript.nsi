;--------- CONFIGURATION ---------

!define APP_NAME "The Zekr Project"
!define APP_VER "0.2.0b1"
!define CLASS_PATH "lib\log4j-1.2.8.jar;lib\swt-win32.jar;lib\commons-collections.jar;lib\velocity-1.4.jar;lib\xml-apis.jar;dist\zekr.jar"
!define JRE_OPT "-Djava.library.path=lib"
!define MAIN_CLASS "net.sf.zekr.ZekrMain"
#SetCompressor /SOLID lzma

;Uncomment the next line to specify a splash screen bitmap.
;!define SPLASH_IMAGE "splash.bmp"
;---------------------------------

Name "Zekr"
Caption "${APP_NAME}"
OutFile "zekr.exe"
Icon "..\res\image\icon\zekr01.ico"
SilentInstall silent
XPStyle on

VIProductVersion 0.2.0.0
VIAddVersionKey ProductName Zekr
VIAddVersionKey ProductVersion "${APP_VER}"
VIAddVersionKey OriginalFilename "zekr.exe"
VIAddVersionKey CompanyName "Siahe.com"
VIAddVersionKey CompanyWebsite "http://siahe.com"
VIAddVersionKey FileVersion "${APP_VER}"
VIAddVersionKey FileDescription "The Zekr Open Quranic Project"
VIAddVersionKey LegalCopyright "© 2004-2005 Mohsen Saboorian"

;InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails hide

!addplugindir .

Section ""
  System::Call "kernel32::CreateMutexA(i 0, i 0, t 'zekr') i .r1 ?e"
  Pop $R0 
  StrCmp $R0 0 +2
  Quit

  ClearErrors
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R0" "JavaHome"
  IfErrors 0 FoundVM

  ClearErrors
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R0" "JavaHome"
  IfErrors 0 FoundVM

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  IfErrors NotFound 0

  FoundVM:
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfFileExists $R0 0 NotFound

  StrCpy $R1 ""
  Call GetParameters
  Pop $R1

  SetOverwrite ifdiff
;  SetOutPath $TEMP
;  File "${JARFILE}"
;  StrCpy $R0 '$R0 -classpath "${CLASS_PATH}" ${JRE_OPT} net.sf.zekr.ZekrMain $R1'
    StrCpy $R0 'javaw.exe -classpath ${CLASS_PATH} ${JRE_OPT} ${MAIN_CLASS}'
  Exec "$R0"
  Quit

  NotFound:
  Sleep 800
  MessageBox MB_ICONEXCLAMATION|MB_YESNO \
                    'Could not find a Java Runtime Environment (JRE) installed on your computer. \
                     $\nWithout it you cannot run "${APP_NAME}". \
                     $\n$\nWould you like to visit the Java website to download it?' \
                    IDNO +2
  ExecShell open "http://java.sun.com/getjava"
  Quit
SectionEnd

Function GetParameters
  Push $R0
  Push $R1
  Push $R2
  StrCpy $R0 $CMDLINE 1
  StrCpy $R1 '"'
  StrCpy $R2 1
  StrCmp $R0 '"' loop
  StrCpy $R1 ' '
  loop:
    StrCpy $R0 $CMDLINE 1 $R2
    StrCmp $R0 $R1 loop2
    StrCmp $R0 "" loop2
    IntOp $R2 $R2 + 1
    Goto loop
  loop2:
    IntOp $R2 $R2 + 1
    StrCpy $R0 $CMDLINE 1 $R2
    StrCmp $R0 " " loop2
  StrCpy $R0 $CMDLINE "" $R2
  Pop $R2
  Pop $R1
  Exch $R0
FunctionEnd

