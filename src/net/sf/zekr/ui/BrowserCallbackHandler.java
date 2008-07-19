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
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.filter.IQuranFilter;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mohsen Saboorian
 */
public class BrowserCallbackHandler {
	private Logger logger = Logger.getLogger(this.getClass());
	private ApplicationConfig config = ApplicationConfig.getInstance();
	private QuranForm form;

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
			form.quranBrowser.execute("window.status='';"); // clear the status text
			if (form.transBrowser != null)
				form.transBrowser.execute("window.status='';"); // clear the status text

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
				if (form.searchTarget == QuranForm.QURAN_ONLY) {
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
			} else if (message.startsWith("ZEKR::PLAYER_VOLUME")) {
				Integer volume = new Integer(message.substring(message.indexOf(' '), message.indexOf(';')).trim());
				config.getProps().setProperty("audio.volume", volume);
			} else if (message.startsWith("ZEKR::PLAYER_PLAYPAUSE")) {
				form.playerTogglePlayPause();
			} else if (message.startsWith("ZEKR::PLAYER_STOP")) {
				form.playerStop();
			} else if (message.startsWith("ZEKR::PLAYER_NEXT_SURA")) {
				form.playerAutoNextSura = true;
			} else if (message.startsWith("ZEKR::PLAYER_CONT")) {
				String contAya = message.substring(message.indexOf(' ') + 1, message.indexOf(';')).trim();
				config.getProps().setProperty("audio.continuousAya", contAya);
			}
		}
	}

}
