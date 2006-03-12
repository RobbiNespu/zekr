/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 28, 2005
 */
package net.sf.zekr.common.config;

import org.eclipse.swt.SWT;

/**
 * This class holds some global settings used by Zekr. <br>
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class GlobalConfig {

	/** Zekr version: e.g. 0.2.0b1 for beta1 or 0.2.0 for release. */
	public static String ZEKR_VERSION = "0.3.0b1";

	/** Flag specifying if OS is Linux */
	public static boolean isLinux = SWT.getPlatform().equalsIgnoreCase("gtk");

	/** Flag specifying if OS is Mac */
	public static boolean isMac = SWT.getPlatform().equalsIgnoreCase("carbon");

	/** Flag specifying if OS is Solaris */
	public static boolean isSolaris = SWT.getPlatform().equalsIgnoreCase("motif");

	/** Flag specifying if OS is Windows */
	public static boolean isWindows = SWT.getPlatform().equalsIgnoreCase("win32");

	/** Default output encoding for html */
	public static final String OUT_HTML_ENCODING = "UTF-8";

	public static final String HOME_PAGE = "http://siahe.com/zekr";

	/**
	 * Holds user country (runtime property <code>user.country</code>). e.g. IR, US,
	 * etc.
	 */
	public static final String USER_COUNTRY = System.getProperty("user.country");

	/** This constant holds the current working directory for the application. */
	public static final String RUNTIME_DIR = System.getProperty("user.dir");

	/** Holds user home directory. */
	public static final String USER_HOME_PATH = System.getProperty("user.home");

	/**
	 * Holds user language (runtime property <code>user.language</code>). e.g. fa, en,
	 * etc.
	 */
	public static final String USER_LANGUAGE = System.getProperty("user.language");
}
