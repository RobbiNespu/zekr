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
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * Template for multi-translation view layout.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class MultiTranslationViewTemplate extends AbstractQuranViewTemplate {
	private TranslationData transDataList[];

	/**
	 * Creates a new multi-translation view template object. This object may have a Quran text plus an array of
	 * translation data.
	 * 
	 * @param quran
	 *           the Quran text
	 * @param transDataList
	 *           an array of translation data
	 * @param suraNum
	 *           the sura number to be transformed (counted from 1)
	 * @param ayaNum
	 *           the selected aya number within the sura (counted from 1)
	 */
	public MultiTranslationViewTemplate(IQuranText quran, TranslationData[] transDataList, int suraNum, int ayaNum) {
		super(quran, suraNum, ayaNum);
		this.transDataList = transDataList;
		engine.put("TEXT_LAYOUT", ApplicationConfig.LINE_BY_LINE);
		engine.put("MIXED", "true");
		engine.put("CUSTOM", "true");
		engine.put("TRANS_DATA", transDataList);

		String[] quranSura = quran.getSura(suraNum);
		engine.put("AYA_LIST", quranSura);
	}
}
