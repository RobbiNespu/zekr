#!/bin/sh

# If you cannot launch Zekr, make sure that Mozilla GTK2 1.4+ is installed (Firefox can also be used on some distributions),
# and set an environment variable MOZILLA_FIVE_HOME to your mozilla home folder.
# See http://siahe.com/zekr/faq.html#linux for more details.

# uncomment and change the following line to a proper Mozilla-GTK2 installation directory.
#export MOZILLA_FIVE_HOME=/usr/lib/firefox

if [ -z "$MOZILLA_FIVE_HOME" ]; then
	echo "MOZILLA_FIVE_HOME environment variable is not set. Please set it to a Mozilla GTK2 1.4+ installation directory. See http://siahe.com/zekr/faq.html#linux for more info."
	exit 1
fi

JAVA_CMD=java
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$MOZILLA_FIVE_HOME

ORIG_DIR_NAME=`cd`
DIR_NAME=`dirname $0`
MAIN_CLASS=net.sf.zekr.ZekrMain
JRE_OPT=-Djava.library.path=lib
CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-3.3.0.jar:lib/apache-commons.jar:lib/velocity-1.4.jar:lib/lucene-highlighter-2.1.0.jar:lib/lucene-core-2.1.1-dev.jar:dist/zekr.jar
VM_ARGS="-Xms10m -Xmx70m"
APP_ARGS=$*

cd $DIR_NAME
"$JAVA_CMD" $VM_ARGS -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS $APP_ARGS
cd $ORIG_DIR_NAME