/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2004
 */

package net.sf.zekr.ui;

import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class SplashScreen extends BaseForm {
	final private String imagePath = resource.getString("image.splashScreen");
	private Image splashImage = null;

	private static final Logger logger = Logger.getLogger(SplashScreen.class);

	public SplashScreen(Display display) {
		this.display = display;
		splashImage = new Image(display, imagePath);
	}

	/**
	 * This method initializes Splash Shell
	 */
	private void createSplashShell() {
		shell = new Shell(display, SWT.NO_TRIM);
		shell.setText(LanguageEngine.getInstance().getMeaning("FORM_TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")) });
		ImageData imageData = splashImage.getImageData();
		shell.setSize(imageData.width, imageData.height);
		shell.setLocation(FormUtils.getScreenCenter(display, splashImage.getBounds()));
	}

	/**
	 * Shows a splash screen until the whole application is started. This can be done by
	 * disposing splash screen after other parts of the application are started.
	 */
	public void show() {
		logger.info("Show splash screen.");
		createSplashShell();
		shell.open();
		GC g = new GC(shell);
		g.drawImage(splashImage, 0, 0);
	}

	public void dispose() {
		logger.info("Close splash screen.");
		splashImage.dispose();
		shell.dispose();
		super.dispose();
	}
}
