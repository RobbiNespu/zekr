/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.template;

import net.sf.zekr.common.resource.IQuranText;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class QuranViewTemplate extends AbstractQuranViewTemplate {

	public QuranViewTemplate(IQuranText quran, int suraNum, int ayaNum) {
		super(quran, suraNum, ayaNum);
		engine.put("TEXT_LAYOUT", config.getViewProp("view.quranLayout"));
		// put sura ayas
		engine.put("AYA_LIST", quran.getSura(suraNum));
	}
}
