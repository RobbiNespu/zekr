/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 22, 2006
 */
package net.sf.zekr.ui.splash;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskItem;

/**
 * @author Mohsen Saboorian
 */
public class AdvancedSplashScreen extends AbstractSplachScreen {
	private static final int ALPHA_LIMIT = 0;
	private static final int PROGRESS_BAR_HEIGHT = 12;
	private ImageData imageData;
	private Region region;

	private String progressMsg = "Starting Zekr " + GlobalConfig.ZEKR_VERSION + " ...";
	private ProgressBar progBar;
	protected int progressCount = 0;

	public AdvancedSplashScreen(Display disp) {
		super(disp);
		shell = new Shell(display, SWT.NO_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText("Starting Zekr...");
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.form16")),
				new Image(display, resource.getString("icon.form32")),
				new Image(display, resource.getString("icon.form48")),
				new Image(display, resource.getString("icon.form128")),
				new Image(display, resource.getString("icon.form256")) });

		final TaskItem tbi = getTaskBarItem();
		if (tbi != null) {
			tbi.setProgressState(SWT.NORMAL);
		}

		shell.addListener(EventProtocol.CUSTOM_ZEKR_EVENT, new Listener() {
			public void handleEvent(Event e) {
				if (e.data != null) {
					if (((String) e.data).startsWith(EventProtocol.SPLASH_PROGRESS_FULLY)) {
						progressMsg = ((String) e.data).substring(EventProtocol.SPLASH_PROGRESS_FULLY.length() + 1);
						progBar.setSelection(progressCount = 100);

						if (tbi != null) {
							tbi.setProgress(progressCount);
						}

						shell.redraw();
						shell.update();
					} else if (((String) e.data).startsWith(EventProtocol.SPLASH_PROGRESS)) {
						progressMsg = ((String) e.data).substring(EventProtocol.SPLASH_PROGRESS.length() + 1);
						progBar.setSelection(progressCount += 7);

						if (tbi != null) {
							tbi.setProgress(progressCount);
							tbi.reskin(SWT.ALL);
						}

						shell.redraw();
						shell.update();
					}
				}
			}
		});

		final Point centerPoint = FormUtils.getScreenCenter(display, splashImage.getBounds());
		GC gc = new GC(display);
		Rectangle rect = display.getBounds();
		rect = splashImage.getBounds();
		final Image bgImage = new Image(display, rect);
		gc.copyArea(bgImage, centerPoint.x, centerPoint.y);
		gc.dispose();

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
		Rectangle regBound;

		regBound = region.getBounds();
		int y1 = regBound.height + regBound.y;
		region.add(0, y1, regBound.width, PROGRESS_BAR_HEIGHT);

		shell.setRegion(region);

		shell.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(bgImage, 0, 0);
				int height = imageData.height;
				e.gc.drawImage(splashImage, 0, 0);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.setAlpha(200);
				e.gc.drawText(StringUtils.abbreviate(progressMsg, 40), 40, height - 30, true);
			}
		});

		regBound = region.getBounds();
		shell.setSize(regBound.width, regBound.height);
		shell.setLocation(centerPoint);

		progBar = new ProgressBar(shell, SWT.SMOOTH | SWT.HORIZONTAL);
		progBar.setSelection(progressCount);
		Point shellSize = shell.getSize();
		progBar.setBounds(0, shellSize.y - PROGRESS_BAR_HEIGHT, shellSize.x, PROGRESS_BAR_HEIGHT);
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
