/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 14, 2004
 */

package net.sf.zekr.engine.log;

import java.io.File;
import java.text.DecimalFormat;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.ui.error.ErrorForm;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.widgets.Display;

// TODO: this class should be removed in future. Logger should be obtained through normal common logger interface.
/**
 * Zekr default logger wrapper class for Log4J library. This is not a singleton class. It creates a new instance of itself per
 * each call to <code>getInstance()</code>, because different classes should have different Log4J loggers associated with.
 * 
 * @author Mohsen Saboorian
 */
public class Logger {
   public static final Level INFO = Level.INFO;
   public static final Level DEBUG = Level.DEBUG;
   public static final Level WARN = Level.WARN;
   public static final Level ERROR = Level.ERROR;
   public static final Level FATAL = Level.FATAL;

   private org.apache.log4j.Logger logger;

   private static final String STACK_TRACE_INDENRAION = "  ";
   private static final Level DEAFALT_LEVEL = INFO;

   public static final String LOG_FILE_PATH = Naming.getWorkspace() + File.separator + "zekr.log";

   static {
      System.setProperty("zekr.home", Naming.getWorkspace());

      // make sure that ~/.zekr directory is created before logger startup
      File file = new File(Naming.getWorkspace());
      if (!file.exists()) {
         if (!file.exists() && !file.mkdirs())
            throw new RuntimeException("Can not create \'" + file.getAbsoluteFile() + "\'.");
      }

      PropertyConfigurator.configure("res/config/lib/logger.properties");
      dumpSysInfo(org.apache.log4j.Logger.getLogger(Logger.class));
   }

   private Logger(Class<?> clazz) {
      logger = org.apache.log4j.Logger.getLogger(clazz);
   }

   /**
    * Dumps all necessary system properties.
    * 
    * @param logger
    */
   private static void dumpSysInfo(org.apache.log4j.Logger logger) {
      String n = System.getProperty("line.separator");
      logger.info("System information:" + "\n" + "OS info:\t\t" + System.getProperty("os.name") + " - "
            + System.getProperty("os.version") + " - " + System.getProperty("os.arch") + n + "VM info:\t\t"
            + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.vendor") + ") - "
            + System.getProperty("java.version") + " (" + System.getProperty("java.vm.version") + ") - "
            + System.getProperty("java.vm.info") + n + "User info:\t\t" + System.getProperty("user.home") + " - "
            + System.getProperty("user.dir") + " - " + System.getProperty("user.language") + "-"
            + System.getProperty("user.country") + n + "Encoding info:\t" + System.getProperty("file.encoding") + n
            + "Zekr info:\t\t" + "Worspace: " + Naming.getWorkspace());
   }

   /**
    * For logging more precisely by implying the class name from which log message is sent.
    * 
    * @param theClass logging source class
    * @return corresponding logger
    */
   synchronized final public static Logger getLogger(Class<?> theClass) {
      return new Logger(theClass);
   }

   final public void info(Object msg) {
      logger.info(msg);
   }

   /**
    * Log (info level) memory info.
    */
   final public void memInfo() {
      DecimalFormat df = new DecimalFormat("###,### kb");
      long total = Runtime.getRuntime().totalMemory();
      long free = Runtime.getRuntime().freeMemory();
      String used = df.format((total - free) / 1024);
      String max = df.format(total / 1024);
      logger.info("Memory info - used: " + used + " / total vm memory: " + max + "");
   }

   final public void debug(Object msg) {
      logger.debug(msg);
   }

   final public void warn(Object msg) {
      logger.warn(msg);
   }

   final public void warn(Object msg, Throwable th) {
      logger.warn(msg, th);
   }

   final public void error(Object msg) {
      logger.error(msg);
   }

   final public void error(Object msg, Throwable th) {
      logger.error(msg, th);
   }

   final public void fatal(Object msg) {
      logger.fatal(msg);
   }

   /**
    * This method logs <code>msg.toString()</code> if msg is not of type <code>{@link java.lang.Throwable}</code> (exception). If
    * the msg is in fact a <code>Throwable</code> object, it logs it as an error message implicitly. Then if
    * <code>ApplicationConfig.isFullyInitialized()</code>, it brings up an error dialog and show the exception to user.
    * 
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

   private void logException(Level level, Throwable th, boolean showForm) {
      logger.log(level, "[Exception stack trace for \"" + th.toString() + "\"]", th);
      //		logger.log(level, "[Exception stack trace for \"" + th.toString() + "\"]");
      //		StackTraceElement elements[] = th.getStackTrace();
      //		for (int i = 0, n = elements.length; i < n; i++) {
      //			logger.log(Priority.ERROR, STACK_TRACE_INDENRAION + elements[i].toString());
      //		}
      //		logger.log(level, "[/\"" + th.toString() + "\"]");
      if (showForm && ApplicationConfig.isFullyInitialized()) {
         ErrorForm ef = new ErrorForm(Display.getCurrent(), th);
         ef.show();
      }
   }

   private String getStackTrace(Throwable t) {
      StringBuffer ret = new StringBuffer();
      StackTraceElement[] trace = t.getStackTrace();
      if (t.getMessage() != null)
         ret.append(t.getMessage());
      for (int i = 0; i < trace.length; i++)
         ret.append("\n\tat " + trace[i]);
      return ret.toString();
   }

   /**
    * A call to this method will first log the <code>Throwable</code> error, and then <code>exit</code>s the virtual machine with
    * 1 error status.
    * 
    * @param th throwable object
    */
   public void doFatal(Throwable th) {
      logException(Level.FATAL, th, true);
      Runtime.getRuntime().exit(1);
   }
}
