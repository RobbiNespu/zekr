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
 * RSSOwl (@linkhttp://www.rssowl.org) source code was used in writing this file.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class GlobalConfig {
	/** Flag specifying if OS is Linux */
	public static boolean isLinux = SWT.getPlatform().equalsIgnoreCase("gtk");

	/** Flag specifying if OS is Mac */
	public static boolean isMac = SWT.getPlatform().equalsIgnoreCase("carbon");

	/** Flag specifying if OS is Solaris */
	public static boolean isSolaris = SWT.getPlatform().equalsIgnoreCase("motif");

	/** Flag specifying if OS is Windows */
	public static boolean isWindows = SWT.getPlatform().equalsIgnoreCase("win32");

	public static final String HOME_PAGE = "http://siahe.com/zekr";
}
