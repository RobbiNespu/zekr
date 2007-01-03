#!/bin/sh

# If you cannot launch Zekr, make sure that Mozilla GTK2 1.4+ is installed (Firefox is also possible on some distributions),
# and set an environment variable MOZILLA_FIVE_HOME to your mozilla home folder.
# See http://siahe.com/zekr/faq.html#linux for more details.

# You can uncomment this field if you know where your Mozilla-GTK2 is installed.
#export MOZILLA_FIVE_HOME=/usr/lib/firefox

if [ -z "$MOZILLA_FIVE_HOME" ]; then
	echo "MOZILLA_FIVE_HOME environment variable is not set. Please set it to Mozilla GTK2 1.4+. See http://siahe.com/zekr/faq.html#linux for more info."
	exit 1
fi

JAVA_CMD=java
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$MOZILLA_FIVE_HOME

ORIG_DIR_NAME=`cd`
DIR_NAME=`dirname $0`
MAIN_CLASS=net.sf.zekr.ZekrMain
JRE_OPT=-Djava.library.path=lib
CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-linux.jar:lib/apache-commons.jar:lib/velocity-1.4.jar:lib/velocity-tools-generic-1.2.jar:dist/zekr.jar
VM_ARGS="-Xms10m -Xmx60m"

cd $DIR_NAME
echo Launching Zekr...
"$JAVA_CMD" $VM_ARGS -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS
cd $ORIG_DIR_NAME
