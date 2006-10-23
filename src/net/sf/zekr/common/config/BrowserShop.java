/*   **********************************************************************  **
 **   Copyright notice                                                       **
 **                                                                          **
 **   (c) 2003-2006 RSSOwl Development Team                                  **
 **   http://www.rssowl.org/                                                 **
 **                                                                          **
 **   All rights reserved                                                    **
 **                                                                          **
 **   This program and the accompanying materials are made available under   **
 **   the terms of the Eclipse Public License 1.0 which accompanies this     **
 **   distribution, and is available at:                                     **
 **   http://www.rssowl.org/legal/epl-v10.html                               **
 **                                                                          **
 **   A copy is found in the file epl-v10.html and important notices to the  **
 **   license from the team is found in the textfile LICENSE.txt distributed **
 **   in this package.                                                       **
 **                                                                          **
 **   This copyright notice MUST APPEAR in all copies of the file!           **
 **                                                                          **
 **   Contributors:                                                          **
 **     RSSOwl - initial API and implementation (bpasero@rssowl.org)         **
 **                                                                          **
 **  **********************************************************************  */

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

import org.eclipse.swt.SWTError;
import org.eclipse.swt.program.Program;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class BrowserShop {
	/** Local link to launch */
	static String localHref;

	/** Either netscape or mozilla for Linux / Solaris */
	static String webBrowser;

	/** Flag to indicate a successfull launch on Linux / Solaris */
	static boolean webBrowserSuccessfullyOpened;

	private final static Logger logger = Logger.getLogger(BrowserShop.class);

	/** This utility class constructor is hidden */
	private BrowserShop() {
		// Protect default constructor
	}

	/**
	 * Create a human readable Error message from the given SWTError. Point the user to the FAQ as it includes
	 * the required help.
	 * 
	 * @param e The SWTError that occured while creating the Browser.
	 * @return String Human readable Error Message pointing to the FAQ.
	 */
	public static String createBrowserError(SWTError e) {
		StringBuffer error = new StringBuffer();
		error.append("Please refer to the FAQ on how to setup your").append("\n");
		error.append("system to use the Internal Browser in RSSOwl.");
		return error.toString();
	}

	/**
	 * Open a link in the external browser
	 * 
	 * @param href Any URL
	 */
	public static void openLink(String href) {

		/** If href points to local file */
		if (href.startsWith("file:")) {
			href = href.substring(5);
			while (href.startsWith("/")) {
				href = href.substring(1);
			}
			href = "file:///" + href;
		}

		localHref = href;

		/** Surround href with double quotes if it containes spaces */
		if (localHref.indexOf(" ") >= 0)
			localHref = "\"" + localHref + "\"";

		/** Open default browser */
		else
			useDefaultBrowser();
	}

	/**
	 * Open the webbrowser on Linux or Solaris
	 * 
	 * @param href An URL
	 * @return Process The process that was executed
	 */
	static Process openWebBrowser(String href) {
		Process p = null;

		/** Try Firefox as default browser */
		if (webBrowser == null) {
			try {
				webBrowser = "firefox";
				p = Runtime.getRuntime().exec(webBrowser + "  " + href);
			} catch (IOException e) {
				webBrowser = "mozilla";
			}
		}

		/** Try Mozilla as default browser */
		if (p == null) {
			try {
				p = Runtime.getRuntime().exec(webBrowser + " " + href);
			} catch (IOException e) {
				webBrowser = "netscape";
			}
		}

		/** Try Netscape as default browser */
		if (p == null) {
			try {
				p = Runtime.getRuntime().exec(webBrowser + " " + href);
			} catch (IOException e) {
				logger.log(e);
			}
		}

		return p;
	}

	/**
	 * Use default browser to display the URL
	 */
	static void useDefaultBrowser() {

		/** Launch default browser on Windows */
		if (GlobalConfig.isWindows) {
			Program.launch(localHref);
		}

		/** Launch default browser on Mac */
		else if (GlobalConfig.isMac) {
			try {
				Runtime.getRuntime().exec("/usr/bin/open " + localHref);
			}

			/** Show error message, default browser could not be launched */
			catch (IOException e) {
				logger.log(e);
			}
		}

		/** Launch default browser on Linux & Solaris */
		else {

			/** Run browser in a seperate thread */
			Thread launcher = new Thread("Browser Launcher") {
				public void run() {
					try {

						/** The default browser was successfully launched once, use again */
						if (webBrowserSuccessfullyOpened) {
							Runtime.getRuntime().exec(
									webBrowser + " -remote openURL(" + localHref + ")");
						}

						/** The default browser was not yet launched, try NS and Mozilla */
						else {
							Process proc = openWebBrowser(localHref);
							webBrowserSuccessfullyOpened = true;

							/** Wait for this process */
							try {
								if (proc != null)
									proc.waitFor();
							} catch (InterruptedException e) {
								logger.log(e);
							} finally {
								webBrowserSuccessfullyOpened = false;
							}
						}
					}

					/** Show error, default browser could not be launched */
					catch (IOException e) {
						logger.log(e);
					}
				}
			};
			launcher.setDaemon(true);
			launcher.start();
		}
	}
}
