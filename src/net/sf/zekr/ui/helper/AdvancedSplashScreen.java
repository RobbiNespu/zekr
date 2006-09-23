/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 22, 2006
 */
package net.sf.zekr.ui.helper;

import net.sf.zekr.ui.AbstractSplachScreen;
import net.sf.zekr.ui.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class AdvancedSplashScreen extends AbstractSplachScreen {
	private static final int ALPHA_LIMIT = 100;
	private ImageData imageData;
	private Region region;

	public AdvancedSplashScreen(Display display) {
		super(display);
		shell = new Shell(display, SWT.NO_TRIM);

		region = new Region();
		imageData = splashImage.getImageData();
		if (imageData.alphaData != null) {
			Rectangle pixel = new Rectangle(0, 0, 1, 1);
			for (int y = 0; y < imageData.height; y++) {
				for (int x = 0; x < imageData.width; x++) {
					if (imageData.getAlpha(x, y) >= ALPHA_LIMIT) {
						pixel.x = imageData.x + x;
						pixel.y = imageData.y + y;
						region.add(pixel);
					}
				}
			}
		} else {
			ImageData mask = imageData.getTransparencyMask();
			Rectangle pixel = new Rectangle(0, 0, 1, 1);
			for (int y = 0; y < mask.height; y++) {
				for (int x = 0; x < mask.width; x++) {
					if (mask.getPixel(x, y) != 0) {
						pixel.x = imageData.x + x;
						pixel.y = imageData.y + y;
						region.add(pixel);
					}
				}
			}
		}
		shell.setRegion(region);

		shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(splashImage, imageData.x, imageData.y);
			}
		});

		shell.setSize(imageData.x + imageData.width, imageData.y + imageData.height);
		shell.setLocation(FormUtils.getScreenCenter(display, splashImage.getBounds()));
	}

	public void showSplash() {
		shell.open();
	}

	public void dispose() {
		region.dispose();
		splashImage.dispose();
		shell.dispose();
	}
}
