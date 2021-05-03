/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 30, 2007
 */
package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.model.Aya;
import net.sf.zekr.common.resource.model.Page;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * Template for multi-translation view layout.
 * 
 * @author Mohsen Saboorian
 */
public class MultiTranslationViewTemplate extends AbstractPageViewTemplate {
	private TranslationData transDataList[];

	/**
	 * Creates a new multi-translation view template object. This object may have a Quran text plus an array of
	 * translation data.
	 * 
	 * @param quran the Quran text
	 * @param transDataList an array of translation data
	 * @param suraNum the sura number to be transformed (counted from 1)
	 * @param ayaNum the selected aya number within the sura (counted from 1)
	 * @param playlistUrl recitation playlist URL
	 */
	public MultiTranslationViewTemplate(IQuranText quran, TranslationData[] transDataList, IUserView userView) {
		super(quran, userView);
		this.transDataList = transDataList;
		engine.put("TEXT_LAYOUT", ApplicationConfig.LINE_BY_LINE);
		engine.put("MIXED", "true");
		engine.put("CUSTOM", "true");
		engine.put("TRANS_DATA", transDataList);

		Page qPage = new Page(quran, quranPage);
		Aya[][] mixed = new Aya[qPage.getSize()][1];
		for (int i = 0; i < mixed.length; i++) {
			mixed[i][0] = qPage.getAya(i);
		}

		engine.put("AYA_LIST", mixed);
	}
}
