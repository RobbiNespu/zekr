#!/bin/sh

# If you cannot launch Zekr, make sure that Mozilla GTK2 1.4+ is installed,
# and set an environment variable MOZILLA_FIVE_HOME to your mozilla home folder.
# See http://siahe.com/zekr/faq.html (Linux part) for more details

# You can uncomment this field if you know where your Mozilla-GTK2 is installed.
# export MOZILLA_FIVE_HOME=/usr/local/mozilla

if [ -z "$JAVA_HOME" -a -z "$JRE_HOME" ]; then
	echo "Neither the JAVA_HOME nor the JRE_HOME environment variable is defined."
	echo "At least one of these variables is needed to run this program."
	exit 1
fi


export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$MOZILLA_FIVE_HOME

export JAVA_CMD="$JAVA_HOME"/bin/java

export MAIN_CLASS=net.sf.zekr.ZekrMain
export JRE_OPT=-Djava.library.path=lib
export CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-linux-gtk.jar:lib/commons-collections.jar:lib/velocity-1.4.jar:dist/zekr.jar:bin

echo launching Zekr...
"$JAVA_CMD" -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS
