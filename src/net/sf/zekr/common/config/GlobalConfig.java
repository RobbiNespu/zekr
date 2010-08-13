/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 28, 2005
 */
package net.sf.zekr.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.SWT;

/**
 * This class holds some global settings used by Zekr.
 * 
 * @author Mohsen Saboorian
 */
public class GlobalConfig {
	/** is true for debug-mode */
	public static final boolean DEBUG_MODE = false;

	/** Specifies if OS is Linux */
	public static final boolean isLinux = SWT.getPlatform().equalsIgnoreCase("gtk");

	/** Specifies if OS is Carbon */
	public static final boolean isCarbon = SWT.getPlatform().equalsIgnoreCase("carbon");

	/** Specifies if OS is Cocoa */
	public static final boolean isCocoa = SWT.getPlatform().equalsIgnoreCase("cocoa");

	/** Specifies if OS is Mac */
	public static final boolean isMac = isCarbon || isCocoa;

	/** Specifies if OS is Solaris */
	public static final boolean isSolaris = SWT.getPlatform().equalsIgnoreCase("motif");

	/** Specifies if OS is Windows */
	public static final boolean isWindows = SWT.getPlatform().equalsIgnoreCase("win32");

	/** Specifies if SWT has full BIDI support for the current system */
	public static final boolean hasBidiSupport = isWindows || isLinux;

	/** Default output encoding for HTML */
	public static final String OUT_HTML_ENCODING = "UTF-8";

	public static final String HOME_PAGE = "http://zekr.org";

	public static final String RESOURCE_PAGE = HOME_PAGE + "/resources.html";

	public static final String HELP_PAGE = HOME_PAGE + "/wiki";

	public static final String SEARCH_HELP_PAGE = HOME_PAGE + "/wiki/Search_help";

	/**
	 * Holds user country (runtime property <code>user.country</code>). e.g. IR, US, etc.
	 */
	public static final String USER_COUNTRY = System.getProperty("user.country");

	/** This constant holds the current working directory for the application. */
	public static final String RUNTIME_DIR = System.getProperty("user.dir").replace('\\', '/');

	/** Holds user home directory. */
	public static final String USER_HOME_PATH = System.getProperty("user.home").replace('\\', '/');

	/**
	 * <tt>\n</tt> on Linux, <tt>\r\n</tt> on Win32 and <tt>\n\r</tt> on Mac.
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	/**
	 * Holds user language (runtime property <code>user.language</code>). e.g. fa, en, etc.
	 */
	public static final String USER_LANGUAGE = System.getProperty("user.language");

	public static final int MAX_MENU_STRING_LENGTH = 45;

	/**
	 * A unique number for each build. It contains full date plus hour. For example <tt>2008021020</tt> is used
	 * for a version released on Feb. 2, 2008, on 20 o'clock.
	 */
	public static final String ZEKR_BUILD_NUMBER;

	/**
	 * Zekr build date in {@link Date} format, originally obtained from {@link #ZEKR_BUILD_NUMBER}. This
	 * variable is intentionally left non-final, and should not be changed.
	 */
	public static Date ZEKR_BUILD_DATE;

	/** Build status: FINAL, BETA, DEV */
	public static final String ZEKR_BUILD_STATUS;

	/**
	 * Zekr full version: <tt>[version_number][release_status]</tt>. e.g. 0.2.0beta1 for beta1 or 0.2.0 for
	 * final release.
	 */
	public static final String ZEKR_VERSION;

	/**
	 * Zekr update URL.
	 */
	public static final String UPDATE_SITE;

	private static SimpleDateFormat zekrDateFormatter = new SimpleDateFormat("yyyyMMddHH", new Locale("en", "US"));

	static {
		PropertyResourceBundle prb = null;
		try {
			InputStream is = GlobalConfig.class.getResourceAsStream("version.properties");
			prb = new PropertyResourceBundle(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ZEKR_VERSION = prb == null ? "" : prb.getString("zekr.version");
			ZEKR_BUILD_NUMBER = prb == null ? "" : prb.getString("zekr.build.number");
			ZEKR_BUILD_STATUS = prb == null ? "" : prb.getString("zekr.build.status");
			UPDATE_SITE = prb == null ? "http://zekr.org/update" : prb.getString("zekr.update.address");
			try {
				ZEKR_BUILD_DATE = prb == null ? null : zekrDateFormatter.parse(ZEKR_BUILD_NUMBER);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Path of installer directory (path from where Zekr is installed). This path is useful for DVD or CD
	 * distributions of Zekr in which we want to refer to installer path for audio files (play recitations from
	 * DVD or CD). If no <code>install.properties</code> was found in {@link ApplicationPath#USER_CONFIG}, its
	 * value will be equal to {@link ApplicationPath#MAIN_CONFIG}
	 */
	public static String ZEKR_INSTALL_DIR;

	static {
		try {
			File file = new File(RUNTIME_DIR, "res/config/install.properties");
			if (file.exists()) {
				InputStream fis = new FileInputStream(file);
				PropertiesConfiguration props = new PropertiesConfiguration();
				props.setEncoding("UTF-8");
				props.load(fis);
				fis.close();
				ZEKR_INSTALL_DIR = props.getString("zekr.install.dir", RUNTIME_DIR);
			} else {
				ZEKR_INSTALL_DIR = RUNTIME_DIR;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Tries to find user's desktop folder. If failed, returns <code>USER_HOME_PATH</code> (
	 *         <tt>user.home</tt> system property).
	 */
	public static final String getDefaultStartFolder() {
		String ret = USER_HOME_PATH;
		if (new File(USER_HOME_PATH + "/" + "desktop/").exists())
			return USER_HOME_PATH + "/" + "desktop/";
		else if (new File(USER_HOME_PATH + "/" + "Desktop/").exists())
			return USER_HOME_PATH + "/" + "Desktop/";
		return ret;
	}
}
