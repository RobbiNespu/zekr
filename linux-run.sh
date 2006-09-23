#!/bin/sh

# If you cannot launch Zekr, make sure that Mozilla GTK2 1.4+ is installed,
# and set an environment variable MOZILLA_FIVE_HOME to your mozilla home folder.
# See http://siahe.com/zekr/faq.html (Linux part) for more details

# You can uncomment this field if you know where your Mozilla-GTK2 is installed.
# export MOZILLA_FIVE_HOME=/usr/local/mozilla

if [ -z "$MOZILLA_FIVE_HOME" ]; then
	echo "MOZILLA_FIVE_HOME environment variable is not set.\nPlease set to point to Mozilla GTK2 1.4+.\nSee http://siahe.com/zekr/faq.html#linux for more info."
	exit 1
fi

export JAVA_CMD=java
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$MOZILLA_FIVE_HOME

export MAIN_CLASS=net.sf.zekr.ZekrMain
export JRE_OPT=-Djava.library.path=lib
export CLASS_PATH=lib/log4j-1.2.8.jar:lib/swt-linux.jar:lib/apache-commons.jar:lib/velocity-1.4.jar:lib/velocity-tools-generic-1.2.jar:dist/zekr.jar

echo Launching Zekr...
"$JAVA_CMD" -cp "$CLASS_PATH" $JRE_OPT $MAIN_CLASS
