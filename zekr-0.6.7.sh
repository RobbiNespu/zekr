#!/bin/sh

# If you cannot launch Zekr, make sure that Mozilla GTK2 1.4+ is installed
# (Firefox can also be used on some distributions), and set an environment
# variable MOZILLA_FIVE_HOME to your Mozilla home folder.
# See http://zekr.org/faq.html#linux for more details.

# export MOZILLA_FIVE_HOME=/usr/lib/firefox

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

CLASS_PATH=lib/swt.jar:lib/log4j-1.2.8.jar:lib/commons-collections-3.1.jar:lib/commons-io-1.1.jar:lib/commons-lang-2.1.jar:lib/commons-logging-1.0.4.jar:lib/commons-configuration-1.2.jar:lib/velocity-1.4.jar:lib/lucene-core-2.2.0.jar:lib/lucene-highlighter-2.2.0.jar:dist/zekr.jar
VM_ARGS="-Xms10m -Xmx70m"

cd $DIR_NAME
"$JAVA_CMD" $VM_ARGS -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS $APP_ARGS $*
cd $ORIG_DIR_NAME