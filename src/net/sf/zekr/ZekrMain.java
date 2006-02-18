/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     4/02/2005
 */

package net.sf.zekr;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.common.runtime.InitRuntime;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.QuranForm;
import net.sf.zekr.ui.SplashScreen;

import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class ZekrMain {
	private static Display display = new Display();
	private final static Logger logger = Logger.getLogger(ZekrMain.class);

	/**
	 * Will start the Zekr platform
	 */
	static void startZekr() {
		try {
			logger.info("Starting The Zekr Platform...");
			SplashScreen splash = new SplashScreen(display);
			splash.show();

			InitRuntime runtime = new InitRuntime();
			runtime.configureDirectories();

			QuranForm quraForm = new QuranForm(display);
			quraForm.show(750, 580);
			splash.dispose(); // close splash screen
			quraForm.loopForever();
			quraForm.dispose();

		} catch (Throwable t) {
			logger.log(t);
		} finally {
			logger.info("Zekr is now down.\n");
		}
	}

	private static void test() {
		((TranslationData) ApplicationConfig.getInsatnce().
				getTranslation().getAllTranslation().iterator().next()).load();
		((TranslationData) ApplicationConfig.getInsatnce().
		getTranslation().getAllTranslation().iterator().next()).equals(null);
	}

	public static void main(String[] args) {
		startZekr();
	}

	public static Display getDisplay() {
		return display;
	}
}
