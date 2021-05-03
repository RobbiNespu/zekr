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
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.filter.IQuranFilter;
import net.sf.zekr.engine.audio.PlayableObject;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.collections.MapUtils;
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

	public BrowserCallbackHandler(QuranForm form) {
		this.form = form;
	}

	public Object newCallbackHandler(Object[] args) {
		String method = (String) args[0];
		if (ArrayUtils.contains(new String[] { "ZEKR::GOTO", "ZEKR::REDIRECT" }, method)) {
			int sura = 0, aya = 0, page = 0;
			try {
				sura = Integer.parseInt(((String) args[1]).trim());
				aya = Integer.parseInt(((String) args[2]).trim());
				if (args.length > 3) {
					page = Integer.parseInt(((String) args[3]).trim());
				}
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
				pe = new PopupBox(form.shell, form.meaning("TRANSLATION_SCOPE"), td.get(sura, aya),
						FormUtils.toSwtDirection(td.direction));
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
			form.quranFormController.gotoNextAya();
		} else if ("ZEKR::PLAY".equals(method)) {
			IQuranLocation loc = QuranPropertiesUtils.getLocation((String) args[1]);
			boolean play = Boolean.parseBoolean((String) args[2]);
			if (play) {
				PlayableObject playableObject = config.getAudioCacheManager().getPlayableObject(loc, 0);
				if (playableObject == null) {
					logger.error("Search result audio for this location cannot be loaded: " + loc);
				} else {
					logger.debug(String.format("Open search result playable object: %s.", playableObject));
					form.searchPlayerController.open(playableObject);
					form.searchPlayerController.setVolume(config.getPlayerController().getVolume());
					form.searchPlayerController.play();
				}
			} else {
				form.searchPlayerController.stop();
			}
		} else if ("ZEKR::ZOOM".equals(method)) {
			int zoom = (int) Double.parseDouble(args[1].toString());
			String layout = config.getViewLayout();
			boolean onlyTrans = false;
			boolean onlyQuran = false;
			if (ApplicationConfig.SEPARATE_LAYOUT.equals(layout)) {
				onlyTrans = Boolean.parseBoolean(args[2].toString());
				onlyQuran = !Boolean.parseBoolean(args[2].toString());
			}

			ThemeData themeData = config.getTheme().getCurrent();
			Map<String, String> props = themeData.props;

			if (!onlyQuran) {
				for (Entry<String, String> entry : props.entrySet()) {
					String key = entry.getKey();
					if (key.startsWith("trans_") && key.endsWith("fontSize")) {
						int transFontSize = MapUtils.getIntValue(props, key, 10);
						transFontSize += zoom;
						props.put(key, String.valueOf(transFontSize));
					}
				}
			}

			if (!onlyTrans) {
				String quranFontSizeKey = "quran_fontSize";
				int quranFontSize = MapUtils.getIntValue(props, quranFontSizeKey, 10);
				quranFontSize += zoom;
				props.put(quranFontSizeKey, String.valueOf(quranFontSize));
			}

			EventUtils.sendEvent(EventProtocol.REFRESH_VIEW);
		}
		return null;
	}
}
