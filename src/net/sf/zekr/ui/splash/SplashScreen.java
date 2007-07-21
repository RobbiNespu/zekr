/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.ui.splash;

import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SplashScreen extends AbstractSplachScreen {

	public SplashScreen(Display display) {
		super(display);
	}

	/**
	 * This method initializes Splash Shell
	 */
	private void createSplashShell() {
		shell = new Shell(display, SWT.NO_TRIM);
		shell.setText(LanguageEngine.getInstance().getMeaning("FORM_TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")), new Image(display, resource.getString("icon.form48")),
				new Image(display, resource.getString("icon.form128")), new Image(display, resource.getString("icon.form256"))});
		ImageData imageData = splashImage.getImageData();
		shell.setSize(imageData.width, imageData.height);
		shell.setLocation(FormUtils.getScreenCenter(display, splashImage.getBounds()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.ui.AbstractSplachScreen#show()
	 */
	public void showSplash() {
		if (showSplash) {
			logger.info("Show splash screen.");
			createSplashShell();
			shell.open();
			GC g = new GC(shell);
			g.drawImage(splashImage, 0, 0);
		} else {
			logger.info("Splash will not be shown.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.ui.AbstractSplachScreen#dispose()
	 */
	public void dispose() {
		if (showSplash) {
			logger.info("Close splash screen.");
			splashImage.dispose();
			shell.dispose();
		}
	}
}
