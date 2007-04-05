/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 30, 2007
 */
package net.sf.zekr.engine.theme;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * Template for custom mixed view layout.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CustomMixedViewTemplate extends AbstractQuranViewTemplate {
	private TranslationData transData[];
	private int suraNum;

	/**
	 * Creates a new mixed view template object. It will put a 2xN matrix in velocity context. There is 2 row
	 * (first for the quran and second for translation). Each row holds the text of the sura (quran and
	 * translation).
	 * 
	 * @param quran the Quran text
	 * @param trans the translation text
	 * @param suraNum the sura number to be transformed (counted from 0)
	 */
	public CustomMixedViewTemplate(IQuranText quran, TranslationData[] transData, int suraNum, int ayaNum) {
		super(quran, suraNum, ayaNum);
		this.transData = transData;
		engine.put("TEXT_LAYOUT", ApplicationConfig.LINE_BY_LINE);
		engine.put("MIXED", "true");
		engine.put("CUSTOM", "true");
		engine.put("TRANS_DATA", transData);

		String[] quranSura = quran.getSura(suraNum);
		engine.put("AYA_LIST", quranSura);
	}
}
