/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2006
 */
package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranText;

/**
 * Template for mixed view layout.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class MixedViewTemplate extends AbstractQuranViewTemplate {
	private IQuranText trans;
	private int suraNum;

	/**
	 * Creates a new mixed view template object. It will put a 2xN matrix in velocity context. There is 2 row (first for
	 * the quran and second for translation). Each row holds the text of the sura (quran and translation).
	 * 
	 * @param quran
	 *           the Quran text
	 * @param trans
	 *           the translation text
	 * @param suraNum
	 *           the sura number to be transformed (counted from 1)
	 * @param ayaNum
	 *           the selected aya number within the sura
	 */
	public MixedViewTemplate(IQuranText quran, IQuranText trans, int suraNum, int ayaNum) {
		super(quran, suraNum, ayaNum);
		this.trans = trans;
		engine.put("TEXT_LAYOUT", ApplicationConfig.LINE_BY_LINE);
		engine.put("MIXED", "true");
		engine.put("CUSTOM", "false"); // TODO: it should not be set!

		String[] quranSura = quran.getSura(suraNum);
		String[] transSura = trans.getSura(suraNum);
		String[][] mixed = new String[quranSura.length][2];
		for (int i = 0; i < mixed.length; i++) {
			mixed[i][0] = quranSura[i];
			mixed[i][1] = transSura[i];
		}
		engine.put("AYA_LIST", mixed);
	}
}
