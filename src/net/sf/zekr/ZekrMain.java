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

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.AbstractSplachScreen;
import net.sf.zekr.ui.QuranForm;
import net.sf.zekr.ui.SplashScreen;
import net.sf.zekr.ui.helper.AdvancedSplashScreen;

import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class ZekrMain {
	private final static Logger logger = Logger.getLogger(ZekrMain.class);

	/**
	 * Will start the Zekr platform
	 */
	static void startZekr() {
		Display display = new Display();
		QuranForm quranForm = null;
		try {
			logger.info("Starting The Zekr Platform " + GlobalConfig.ZEKR_VERSION + " (" + new Date() + ")");

			AbstractSplachScreen splash = new AdvancedSplashScreen(display);
			logger.debug("Display splash screen...");
			splash.show();

			logger.info("Configure runtime configurations...");
			ApplicationConfig.getInstance().getRuntime().configure();
			quranForm = new QuranForm(display);
			quranForm.show();
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
			if (logger != null)
				logger.info("Zekr is now down.\n");
		}
	}

	public static void main(String[] args) {
		startZekr();
	}
}
