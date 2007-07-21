/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 23, 2006
 */

package net.sf.zekr.common.config;

import java.io.IOException;

import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.program.Program;

/**
 * This class is used to open external links in detected browser. Inspired from RSSOwl.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class BrowserUtils {
	private final static Logger logger = Logger.getLogger(BrowserUtils.class);
	private static final String MAC_OPEN_COMMAND = "/usr/bin/open ";
	private static String linuxWebBrowser = null;

	public static void openLink(String href) {
		if (GlobalConfig.isWindows) {
			Program.launch(href);
		} else if (GlobalConfig.isMac) {
			try {
				Runtime.getRuntime().exec(MAC_OPEN_COMMAND + href);
			} catch (IOException e) {
				logger.implicitLog(e);
			}
		} else { // Linux
			if (!Program.launch(href))
				openLink4Linux(href);
		}
	}

	private static void openLink4Linux(String href) {
		if (linuxWebBrowser == null) {
			String[] browsers = { "firefox", "mozilla", "iceape", "seamonkey", "konqueror", "nautilus" };
			for (int i = 0; i < browsers.length; i++) {
				try {
					Runtime.getRuntime().exec(browsers[i] + "  " + href);
					linuxWebBrowser = browsers[i];
					break;
				} catch (IOException e) {
					// DO NOTHING!
				}
			}
		} else {
			try {
				Runtime.getRuntime().exec(linuxWebBrowser + "  " + href);
			} catch (IOException e) {
				logger.implicitLog(e);
			}
		}
	}
}
