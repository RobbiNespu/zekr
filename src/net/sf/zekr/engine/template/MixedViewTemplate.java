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
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.model.Aya;
import net.sf.zekr.common.resource.model.Page;

/**
 * Template for mixed view layout.
 * 
 * @author Mohsen Saboorian
 */
public class MixedViewTemplate extends AbstractPageViewTemplate /*AbstractSuraViewTemplate */{
	/**
	 * Creates a new mixed view template object. It will put a 2xN matrix in velocity context. There is 2 row
	 * (first for the quran and second for translation). Each row holds the text of the sura (quran and
	 * translation).
	 * 
	 * @param quran the Quran text
	 * @param trans the translation text
	 * @param suraNum the sura number to be transformed (counted from 1)
	 * @param ayaNum the selected aya number within the sura
	 * @param playlistUrl recitation playlist URL
	 */
	public MixedViewTemplate(IQuranText quran, IQuranText trans, IUserView userView) {
		super(quran, trans, userView);
		engine.put("TEXT_LAYOUT", ApplicationConfig.LINE_BY_LINE);
		engine.put("MIXED", "true");
		engine.put("CUSTOM", "false"); // TODO: it should not be set!

		// String[] quranSura = quran.getSura(suraNum);
		// String[] transSura = trans.getSura(suraNum);

		Page qPage = new Page(quran, quranPage);
		Page tPage = new Page(trans, quranPage);

		//		String[][] mixed = new String[quranSura.length][2];
		//		for (int i = 0; i < mixed.length; i++) {
		//			mixed[i][0] = quranSura[i];
		//			mixed[i][1] = transSura[i];
		//		}
		//		engine.put("AYA_LIST", mixed);

		Aya[][] mixed = new Aya[qPage.getSize()][2];
		for (int i = 0; i < mixed.length; i++) {
			mixed[i][0] = qPage.getAya(i);
			mixed[i][1] = tPage.getAya(i);
		}
		engine.put("AYA_LIST", mixed);

		engine.put("QURAN_PAGE", qPage);
		engine.put("TRANS_PAGE", tPage);
		engine.put("AYA_COUNT", new Integer(qPage.getSize()));
	}
}
