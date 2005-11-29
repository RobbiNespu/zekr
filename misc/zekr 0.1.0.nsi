!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\orange-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\orange-uninstall.ico"

!define MUI_WELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\orange.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\orange.bmp"

!include "MUI.nsh"

!define VERSION "0.1.0"

Name "Zekr ${VERSION}"
OutFile "zekr-${VERSION}.exe"
BrandingText "The Zekr Project"

;Default installation folder
InstallDir "$PROGRAMFILES\Zekr"

;Get installation folder from registry if available
InstallDirRegKey HKCU "Software\Zekr" ""

; Global variables
!define src "zekr_"
Var STARTMENU_FOLDER

!define MUI_ABORTWARNING
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "zekr_\license.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY

!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\Zekr"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"

!insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

# Uninstall Macros
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

!insertmacro MUI_LANGUAGE "English"
#!insertmacro MUI_LANGUAGE "Farsi"

Section "Zekr main installation" zekrMain
	; Set Section properties
	SetOverwrite on
  SectionIn RO

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\"
	File "${src}\zekr.exe"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
		;Create shortcuts
		CreateDirectory "$SMPROGRAMS\Zekr"
		CreateShortCut "$SMPROGRAMS\Zekr\Zekr.lnk" "$INSTDIR\zekr.exe"
		CreateShortCut "$SMPROGRAMS\Zekr\Documentation.lnk" "$INSTDIR\doc\site\index.html"
		CreateShortCut "$SMPROGRAMS\Zekr\Uninstall.lnk" "$INSTDIR\uninstall.exe"
	!insertmacro MUI_STARTMENU_WRITE_END

	;Store installation folder
    WriteRegStr HKCU "Software\Zekr" "" $INSTDIR

    ;Create uninstaller
	WriteUninstaller "$INSTDIR\uninstall.exe"
SectionEnd


;--------------------------------
;Descriptions

	;Language strings
  LangString descZekrMain ${LANG_ENGLISH} "Zekr main installation features"
  LangString descZekrMain ${LANG_FARSI} "‰’» «„ò«‰«  «’·Ì »—‰«„ÂùÌ –ò—"

	;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${zekrMain} $(descZekrMain)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section
Section "Uninstall"
  ;ADD YOUR OWN FILES HERE...

	Delete "$INSTDIR\*"
  RMDir "$INSTDIR"
	RMDir /r "$SMPROGRAMS\Zekr"
  DeleteRegKey /ifempty HKCU "Software\Zekr"
SectionEnd



Function .onInit
	;Detect already running installer
	System::Call 'kernel32::CreateMutexA(i 0, i 0, t "myMutex") i .r1 ?e'
	Pop $R0
	StrCmp $R0 0 +3
	MessageBox MB_OK|MB_ICONEXCLAMATION "The installer is already running."
	Abort

	!insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd

Function un.onInit
	System::Call 'kernel32::CreateMutexA(i 0, i 0, t "myMutex") i .r1 ?e'
	Pop $R0
	StrCmp $R0 0 +3
	MessageBox MB_OK "The uninstaller is already running."
	Abort

	!insertmacro MUI_UNGETLANGUAGE
FunctionEnd