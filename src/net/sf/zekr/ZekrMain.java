/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     4/02/2005
 */

package net.sf.zekr;

import java.util.Date;

import net.sf.zekr.common.commandline.CommandRunUtils;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.QuranForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.splash.AbstractSplachScreen;
import net.sf.zekr.ui.splash.AdvancedSplashScreen;

import org.eclipse.swt.widgets.Display;

/**
 * Application main class. This class launches Zekr platform.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ZekrMain {
	private final static Logger logger = Logger.getLogger(ZekrMain.class);

	/**
	 * Will start the Zekr platform
	 */
	static void startZekr() {
		Date date1 = new Date();
		Display display = new Display();
		QuranForm quranForm = null;
		try {
			logger.info("Starting The Zekr Platform " + GlobalConfig.ZEKR_VERSION + " on " + new Date() + ".");

			AbstractSplachScreen splash = new AdvancedSplashScreen(display);
			logger.debug("Display splash screen...");
			splash.show();
			logger.info("Configure runtime configurations...");
			ApplicationConfig.getInstance().getRuntime().configure();// TODO: some directories already created in
																						// ApplicationConfig

			quranForm = new QuranForm(display);
			EventUtils.sendEvent(EventProtocol.SPLASH_PROGRESS_FULLY + ":" + "UI Initialized");
			quranForm.show();

			Date date2 = new Date();
			logger.info("Startup took " + (date2.getTime() - date1.getTime()) + " ms.");

			splash.dispose(); // close splash screen

			while (!quranForm.isDisposed()) {
				try {
					quranForm.loopEver();
				} catch (Throwable th) {
					if (logger != null)
						logger.log(th);
					else
						th.printStackTrace();
				}
			}
		} catch (Throwable t) {
			if (logger != null)
				logger.log(t);
			else
				t.printStackTrace();
		} finally {
			if (display != null)
				display.dispose();
			if (logger != null)
				logger.info("Zekr is now down.\n");
		}
	}

	public static void main(String[] args) {
		if (CommandRunUtils.performAll(args))
			startZekr();
	}
}
