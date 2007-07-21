;--------- CONFIGURATION ---------

!define APP_NAME "The Zekr Project"
!define APP_VER "0.6.0beta2"
!define APP_VER_DETAIL ""
!define CLASS_PATH "lib\swt-3.3.0.jar;lib\apache-commons.jar;lib\log4j-1.2.8.jar;lib\velocity-1.4.jar;lib\lucene-highlighter-2.1.0.jar;lib\lucene-core-2.1.1-dev.jar;dist\zekr.jar"
!define JRE_OPT "-Xms10m -Xmx70m"
!define MAIN_CLASS "net.sf.zekr.ZekrMain"
#SetCompressor /SOLID lzma

;Uncomment the next line to specify a splash screen bitmap.
;!define SPLASH_IMAGE "splash.bmp"
;---------------------------------

Name "Zekr"
Caption "${APP_NAME}"
OutFile "zekr.exe"
Icon "..\res\image\icon\zekr.ico"
SilentInstall silent
XPStyle on
ShowInstDetails nevershow

VIProductVersion 0.6.0.0
VIAddVersionKey ProductName Zekr
VIAddVersionKey ProductVersion "${APP_VER}${APP_VER_DETAIL}"
VIAddVersionKey OriginalFilename "zekr.exe"
VIAddVersionKey CompanyName "zekr.org"
VIAddVersionKey CompanyWebsite "http://zekr.org"
VIAddVersionKey FileVersion "${APP_VER}"
VIAddVersionKey FileDescription "Zekr - Open Quranic Project"
VIAddVersionKey LegalCopyright "© 2004-2007 Mohsen Saboorian and other contributore"

;InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails hide

!addplugindir .

Section ""
  ;Detect if exe file is already running
  System::Call 'kernel32::CreateMutexA(i 0, i 0, t "myMutex") i .r1 ?e'
  Pop $R0
  StrCmp $R0 0 +3
  MessageBox MB_OK|MB_ICONEXCLAMATION "Zekr.exe is already running."
  Abort

  ; 1. check for JAVA_HOME
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  IfErrors 0 FoundVM

  ; 2. check for JDK in registry
  ClearErrors
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R0" "JavaHome"
  IfErrors 0 FoundVM

  ; 3. check for JRE in registry
  ClearErrors
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R0" "JavaHome"
  IfErrors NotFound FoundVM

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
    StrCpy $R0 'javaw.exe -classpath ${CLASS_PATH} ${JRE_OPT} ${MAIN_CLASS} $R1'
  ExecWait "$R0"
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

