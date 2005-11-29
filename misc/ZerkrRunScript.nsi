;--------- CONFIGURATION ---------

!define APP_NAME "The Zekr Project"
!define APP_VER "0.1.0"
!define CLASS_PATH "lib\log4j-1.2.8.jar;lib\swt-win32.jar;lib\commons-collections.jar;lib\velocity-1.4.jar;lib\xml-apis.jar;dist\zekr.jar"
!define JRE_OPT "-Djava.library.path=lib"

;Uncomment the next line to specify a splash screen bitmap.
;!define SPLASH_IMAGE "splash.bmp"
;---------------------------------

Name "Zekr"
Caption "${APP_NAME}"
OutFile "launch.exe"
Icon "..\res\image\icon\zekr01.ico"
SilentInstall silent
XPStyle on

VIProductVersion 0.2.0.0b1
VIAddVersionKey /lang=${LANG_ENGLISH} ProductName Zekr
VIAddVersionKey ProductVersion "${APP_VER}"
VIAddVersionKey /lang=${LANG_ENGLISH} OriginalFilename "zekr.exe"
VIAddVersionKey /lang=${LANG_ENGLISH} CompanyName "Siahe.com"
VIAddVersionKey /lang=${LANG_ENGLISH} CompanyWebsite "http://siahe.com"
VIAddVersionKey /lang=${LANG_ENGLISH} FileVersion "0.1.0"
VIAddVersionKey /lang=${LANG_ENGLISH} FileDescription "The Zekr Open Quranic Project"
VIAddVersionKey /lang=${LANG_ENGLISH} LegalCopyright "(C) 2004-2005 Mohsen Saboorian"

;InstallDirRegKey HKLM "${REGKEY}" Path
;ShowUninstDetails show

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
	StrCpy $R0 'javaw.exe -classpath ${JARFILE};${CLASS_PATH} ${JRE_OPT} net.sf.zekr.ZekrMain'
  Exec "$R0"
  Quit

  NotFound:
  Sleep 800
  MessageBox MB_ICONEXCLAMATION|MB_YESNO \
                    'Could not find a Java Runtime Environment installed on your computer. \
                     $\nWithout it you cannot run "${APPNAME}". \
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

Function Stat
 
;        DWORD st_dev;
;        WORD st_ino;
;        WORD st_mode;
;        WORD st_nlink;
;        WORD st_uid;
;        WORD st_gid;
;        WORD spacer;
;        DWORD st_rdev;
;        INT st_size;
;        DWORD st_atime;
;        DWORD st_mtime;
;        DWORD st_ctime;
!define stSTAT '(i,&i2,&i2,&i2,&i2,&i2,&i2,i,i,i,i,i) i'
 
   System::Call '*${stSTAT} .r0' ; allocates memory for STAT struct and writes address to $0
   System::Call 'msvcrt.dll::_stat(t "$EXEDIR\${FILE_NAME}", i r0) i .r1'
;  MessageBox MB_OK "_stat returned $1"
   MessageBox MB_OK "$EXEDIR\LocaleINFO.exe"
   IntCmp $1 -1 exit
   System::Call "*$0${stSTAT}(,,.r1,,,,,,.r2,.r3,.r4,.r5)"
;   MessageBox MB_OK "st_mode=$1, st_size=$2, st_atime=$3, st_mtime=$4, st_ctime=$5"
   System::Call 'msvcrt.dll::ctime(*i r3) t .r3'
   System::Call 'msvcrt.dll::ctime(*i r4) t .r4'
   System::Call 'msvcrt.dll::ctime(*i r5) t .r5'
   StrCpy $6 "File"
   IntOp $7 $1 & 0040000
   IntCmp $7 0 isfile
   StrCpy $6 "Directory"
isfile:
   StrCpy $8 "Read only"
   IntOp $7 $1 & 0000200
   IntCmp $7 0 rdonly
   StrCpy $8 "Write permitted"
rdonly:
exit:
   System::Free $0 ; free allocated memory
FunctionEnd

