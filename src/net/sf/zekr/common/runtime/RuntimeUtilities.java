/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     20/01/2005
 */

package net.sf.zekr.common.runtime;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class RuntimeUtilities {

	/**
	 * Holds user home directory.
	 */
	public static final String USER_HOME_PATH = System.getProperty("user.home");

	/**
	 * This constant holds the current working directory for the application.
	 */
	public static final String RUNTIME_DIR = System.getProperty("user.dir");

	/**
	 * Holds user language (runtime property <code>user.language</code>).
	 * e.g. fa, en, etc.
	 */
	public static final String USER_LANGUAGE = System.getProperty("user.language");
	
	/**
	 * Holds user country (runtime property <code>user.country</code>).
	 * e.g. IR, US, etc.
	 */
	public static final String USER_COUNTRY = System.getProperty("user.country");

}
