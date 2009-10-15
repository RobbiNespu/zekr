/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 19, 2008
 */
package net.sf.zekr.ui;

import java.io.IOException;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.filter.IQuranFilter;
import net.sf.zekr.engine.audio.FlashPlayerController;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mohsen Saboorian
 */
public class BrowserCallbackHandler {
	private Logger logger = Logger.getLogger(this.getClass());
	private ApplicationConfig config = ApplicationConfig.getInstance();
	private QuranForm form;
	private FlashPlayerController playerController;

	public BrowserCallbackHandler(QuranForm form) {
		this.form = form;
	}

	/**
	 * Handle browser callback messages. This is the common way browser can communicate with SWT.
	 * 
	 * @param message
	 */
	public void doBrowserCallback(String message) {
		if (message.startsWith("ZEKR::")) {
			if (form.transBrowser != null) {
				form.quranBrowser.execute("window.title='';"); // clear the status text
			}
			if (form.transBrowser != null) {
				form.transBrowser.execute("window.title='';"); // clear the status text
			}

			if (message.startsWith("ZEKR::GOTO") || message.startsWith("ZEKR::REDIRECT")) {
				int sura = 0, aya = 0, page = 0;
				try {
					String[] nums = message.substring(message.indexOf(' ') + 1, message.indexOf(';')).split("-");
					sura = Integer.parseInt(nums[0].trim());
					aya = Integer.parseInt(nums[1].trim());
					if (nums.length > 2)
						page = Integer.parseInt(nums[2].trim());
				} catch (Exception e) {
					return; // do nothing
				}
				logger.info("Goto (sura: " + sura + ", aya: " + aya + ", page: " + page + ")");
				form.browserGoto(sura, aya, page, message.startsWith("ZEKR::REDIRECT"));
			} else if (message.startsWith("ZEKR::TRANS") && config.getTranslation().getDefault() != null) {
				int sura;
				int aya;
				try {
					sura = Integer.parseInt(message.substring(message.indexOf(' '), message.indexOf('-')).trim());
					aya = Integer.parseInt(message.substring(message.indexOf('-') + 1, message.indexOf(';')).trim());
				} catch (NumberFormatException e1) {
					return; // do nothing
				}
				PopupBox pe = null;
				boolean isQuranTarget = false;
				if (form.uvc.getViewMode() == IUserView.VM_ADVANCED_SEARCH) {
					isQuranTarget = form.advancedQuranTargetBut.getSelection();
				} else if (form.uvc.getViewMode() == IUserView.VM_SEARCH) {
					isQuranTarget = form.quranTargetBut.getSelection();
				} else { // root
					isQuranTarget = true;
				}

				if (isQuranTarget) {
					logger.info("Show translation: (" + sura + ", " + aya + ")");
					TranslationData td = config.getTranslation().getDefault();
					pe = new PopupBox(form.shell, form.meaning("TRANSLATION_SCOPE"), td.get(sura, aya), FormUtils
							.toSwtDirection(td.direction));
				} else {
					logger.info("Show quran: (" + sura + ", " + aya + ")");
					try {
						pe = new PopupBox(form.shell, form.meaning("QURAN_SCOPE"), new FilteredQuranText(
								IQuranText.SIMPLE_MODE, IQuranFilter.NONE).get(sura, aya), SWT.RIGHT_TO_LEFT);
					} catch (IOException e) {
						logger.log(e);
					}
				}
				Point p = form.display.getCursorLocation();
				p.y += 15;
				int x = 300;
				pe.open(new Point(x, 100), new Point(p.x - x / 2, p.y));
			} else if (message.startsWith("ZEKR::NEXT;")) {
				form.gotoNextAya();
			} else if (message.startsWith("ZEKR::PLAYER::")) {
				String command = message.substring("ZEKR::PLAYER::".length());
				// playerController.handleCallback(command);
			}
		}
	}

	public Object newCallbackHandler(Object[] args) {
		String method = (String) args[0];
		if (ArrayUtils.contains(new String[] { "ZEKR::GOTO", "ZEKR::REDIRECT" }, method)) {
			int sura = 0, aya = 0, page = 0;
			try {
				sura = Integer.parseInt(((String) args[1]).trim());
				aya = Integer.parseInt(((String) args[2]).trim());
				if (args.length > 3)
					page = Integer.parseInt(((String) args[3]).trim());
			} catch (Exception e) {
				return null; // do nothing
			}
			logger.info("Goto (sura: " + sura + ", aya: " + aya + ", page: " + page + ")");
			form.browserGoto(sura, aya, page, method.startsWith("ZEKR::REDIRECT"));
		} else if ("ZEKR::TRANS".equals(method) && config.getTranslation().getDefault() != null) {
			int sura;
			int aya;
			try {
				sura = Integer.parseInt(((String) args[1]).trim());
				aya = Integer.parseInt(((String) args[2]).trim());
			} catch (Exception e1) {
				return null; // do nothing
			}
			PopupBox pe = null;
			boolean isQuranTarget = false;
			if (form.uvc.getViewMode() == IUserView.VM_ADVANCED_SEARCH) {
				isQuranTarget = form.advancedQuranTargetBut.getSelection();
			} else if (form.uvc.getViewMode() == IUserView.VM_SEARCH) {
				isQuranTarget = form.quranTargetBut.getSelection();
			} else { // root
				isQuranTarget = true;
			}

			if (isQuranTarget) {
				logger.info("Show translation: (" + sura + ", " + aya + ")");
				TranslationData td = config.getTranslation().getDefault();
				pe = new PopupBox(form.shell, form.meaning("TRANSLATION_SCOPE"), td.get(sura, aya), FormUtils
						.toSwtDirection(td.direction));
			} else {
				logger.info("Show quran: (" + sura + ", " + aya + ")");
				try {
					pe = new PopupBox(form.shell, form.meaning("QURAN_SCOPE"), new FilteredQuranText(IQuranText.SIMPLE_MODE,
							IQuranFilter.NONE).get(sura, aya), SWT.RIGHT_TO_LEFT);
				} catch (IOException e) {
					logger.log(e);
				}
			}
			Point p = form.display.getCursorLocation();
			p.y += 15;
			int x = 300;
			pe.open(new Point(x, 100), new Point(p.x - x / 2, p.y));
		} else if ("ZEKR::NEXT".equals(method)) {
			form.gotoNextAya();
		} else if ("ZEKR::PLAYER".equals(method)) {
			return playerController.handleCallback(args);
		}
		return null;
	}
}
