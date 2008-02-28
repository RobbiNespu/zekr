/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 23, 2006
 */

package net.sf.zekr.common.util;

import java.io.File;
import java.io.IOException;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.program.Program;

/**
 * This class is used to open external links in detected browser. Inspired from RSSOwl.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class HyperlinkUtils {
	private final static Logger logger = Logger.getLogger(HyperlinkUtils.class);
	private static final String MAC_OPEN_COMMAND = "/usr/bin/open ";
	private static String linuxWebBrowser = null;

	public static void openBrowser(String href) {
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

	public static void openEditor(String filePath) {
		if (GlobalConfig.isWindows) {
			Program.launch(filePath);
		} else if (GlobalConfig.isMac) {
			try {
				if (!Program.launch(filePath))
					Runtime.getRuntime().exec(MAC_OPEN_COMMAND + filePath);
			} catch (IOException e) {
				logger.implicitLog(e);
			}
		} else { // Linux
			if (!Program.launch(filePath)) { // any better idea?
				File gedit = new File("/usr/bin/gedit");
				File kwrite = new File("/usr/bin/kwrite");
				try {
					if (gedit.exists())
						Runtime.getRuntime().exec(gedit.getPath() + " " + filePath);
					else if (kwrite.exists())
						Runtime.getRuntime().exec(kwrite.getPath() + " " + filePath);
				} catch (IOException e) {
					logger.implicitLog(e);
				}
			}
		}
	}

	private static void openLink4Linux(String href) {
		if (linuxWebBrowser == null) {
			String[] browsers = { "firefox", "mozilla", "iceape", "seamonkey", "konqueror", "gnome-www-browser" };
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
