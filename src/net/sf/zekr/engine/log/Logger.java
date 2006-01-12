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
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.ui.error.ErrorForm;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
/*
 * TODO: This class should be in fact an adapter for log4j. In other words you should make
 * an interface for common logging framework, so that one can easily change the logger.
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

	private static Logger thisInstance = new Logger();

	/**
	 * Initialize the Log4J logger
	 */
	static {
		PropertyConfigurator.configure(ApplicationPath.DEFAULT_LOGGER);
		logger = org.apache.log4j.Logger.getLogger(LOGGER_NAME);
	}

	private Logger() {
	}

	/**
	 * @return logger with the <code>type</code> logger type which are:
	 */
	public static Logger getLogger() {
		logger = org.apache.log4j.Logger.getLogger(LOGGER_NAME);
		return thisInstance;
	}

	/**
	 * For logging more precisely by implying the class name from which log message is
	 * sent.
	 * 
	 * @param theClass
	 *            logging source class
	 * @return corresponding logger
	 */
	public static Logger getLogger(Class theClass) {
		logger = org.apache.log4j.Logger.getLogger(theClass);
		return thisInstance;
	}

	public void info(Object msg) {
		logger.info(msg);
	}

	public void debug(Object msg) {
		logger.debug(msg);
	}

	public void warn(Object msg) {
		logger.warn(msg);
	}

	public void error(Object msg) {
		logger.error(msg);
	}

	public void fatal(Object msg) {
		logger.fatal(msg);
	}

	/**
	 * @param msg
	 *            any <code>String</code> or <code>Exception</code>
	 */
	public void log(Object msg) {
		if (msg instanceof Throwable)
			logException((Throwable) msg);
		else
			log(DEAFALT_LEVEL, msg);
	}

	public void log(Level level, Object msg) {
		logger.log(level, msg);
	}

	private static void logException(Throwable t) {
		ErrorForm ef = new ErrorForm(ZekrMain.getDisplay(), t);
		logger.error("[Exception stack trace for \"" + t.toString() + "\"]");
		StackTraceElement elements[] = t.getStackTrace();
		for (int i = 0, n = elements.length; i < n; i++) {
			logger.log(Priority.ERROR, STACK_TRACE_INDENRAION + elements[i].toString());
		}
		logger.error("[/\"" + t.toString() + "\"]");
		ef.show();
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

}
