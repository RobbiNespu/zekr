#!/bin/sh

if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
	echo "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined."
	echo "At least one of these variables is needed to run this program."
	exit 1
fi

if [ -z "$JRE_HOME" ]; then
	export JAVA_CMD="$JAVA_HOME"/Commands/java
else
	export JAVA_CMD="$JRE_HOME"/Commands/java
fi

export MAIN_CLASS=net.sf.zekr.ZekrMain
export JRE_OPT=-Djava.library.path=lib
export CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-mac.jar:lib/commons-collections.jar:lib/velocity-1.4.jar:dist/zekr.jar

echo Launching Zekr...
"$JAVA_CMD" -XstartOnFirstThread -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS
