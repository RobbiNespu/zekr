/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 14, 2004
 */

package net.sf.zekr.engine.log;

import net.sf.zekr.ZekrMain;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.xml.XmlReadException;
import net.sf.zekr.ui.error.ErrorForm;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class Logger {
	public static final Level INFO = Level.INFO;
	public static final Level DEBUG = Level.DEBUG;
	public static final Level WARN = Level.WARN;
	public static final Level ERROR = Level.ERROR;
	public static final Level FATAL = Level.FATAL;

	private static org.apache.log4j.Logger logger;

	private static final String LOGGER_NAME = "Zekr";
	private static final String STACK_TRACE_INDENRAION = "  ";
	private static final Level DEAFALT_LEVEL = INFO;

	private static Logger thisInstance;

	private Logger() {
		System.setProperty("zekr.home", Naming.HOME_PATH);
		PropertyConfigurator.configure("res/config/lib/logger.properties");
		logger = org.apache.log4j.Logger.getLogger(LOGGER_NAME);
		sysInfo();
	}

	/**
	 * dumps all neccessary system properties.
	 */
	private void sysInfo() {
		String n = System.getProperty("line.separator");
		logger.info("System information:" + "\n" + "OS info:\t\t" + System.getProperty("os.name") + " - "
				+ System.getProperty("os.version") + " - " + System.getProperty("os.arch") + n
				+ "JVM info:\t\t" + System.getProperty("java.runtime.name") + " - "
				+ System.getProperty("java.vm.specification.vendor") + " - "
				+ System.getProperty("java.version") + n + "User info:\t\t" + System.getProperty("user.home")
				+ " - " + System.getProperty("user.dir") + " - " + System.getProperty("user.language") + "-"
				+ System.getProperty("user.country") + n + "Encoding info:\t"
				+ System.getProperty("file.encoding"));
	}

	/**
	 * @return logger with the <code>type</code> logger type which are:
	 */
	public static Logger getLogger() {
		if (thisInstance == null)
			thisInstance = new Logger();
		logger = org.apache.log4j.Logger.getLogger(LOGGER_NAME);
		return thisInstance;
	}

	/**
	 * For logging more precisely by implying the class name from which log message is sent.
	 * 
	 * @param theClass logging source class
	 * @return corresponding logger
	 */
	public static Logger getLogger(Class theClass) {
		int i = theClass.getName().lastIndexOf('.');
		String name = theClass.getName().substring(i + 1);
		logger = org.apache.log4j.Logger.getLogger(name);
		if (thisInstance == null)
			thisInstance = new Logger();
		return thisInstance;
	}

	final public void info(Object msg) {
		logger.info(msg);
	}

	final public void debug(Object msg) {
		logger.debug(msg);
	}

	final public void warn(Object msg) {
		logger.warn(msg);
	}

	final public void error(Object msg) {
		logger.error(msg);
	}

	final public void fatal(Object msg) {
		logger.fatal(msg);
	}

	/**
	 * @param msg any object of type <code>String</code> or <code>Throwable</code>
	 */
	final public void log(Object msg) {
		if (msg instanceof Throwable)
			logException(Level.ERROR, (Throwable) msg, true);
		else
			log(DEAFALT_LEVEL, msg);
	}

	final public void implicitLog(Throwable th) {
		logException(Level.ERROR, th, false);
	}

	final public void log(Level level, Object msg) {
		logger.log(level, msg);
	}

	private static void logException(Level level, Throwable th, boolean showForm) {
		logger.log(level, "[Exception stack trace for \"" + th.toString() + "\"]");
		StackTraceElement elements[] = th.getStackTrace();
		for (int i = 0, n = elements.length; i < n; i++) {
			logger.log(Priority.ERROR, STACK_TRACE_INDENRAION + elements[i].toString());
		}
		logger.log(level, "[/\"" + th.toString() + "\"]");
		if (showForm) {
			ErrorForm ef = new ErrorForm(Display.getCurrent(), th);
			ef.show();
		}
	}

	private static String getStackTrace(Throwable t) {
		StringBuffer ret = new StringBuffer();
		StackTraceElement[] trace = t.getStackTrace();
		if (t.getMessage() != null)
			ret.append(t.getMessage());
		for (int i = 0; i < trace.length; i++)
			ret.append("\n\tat " + trace[i]);
		return ret.toString();
	}

	/**
	 * A call to this method will first log the <code>Throwable</code> error, and then <code>exit</code>s
	 * the virtual machine with 1 error status.
	 * 
	 * @param th throwable object
	 */
	public void doFatal(Throwable th) {
		logException(Level.FATAL, th, false);
		Runtime.getRuntime().exit(1);
	}

}
