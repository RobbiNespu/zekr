/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2006
 */
package net.sf.zekr.engine.theme;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.IQuranTranslation;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class MixedViewTemplate extends AbstractQuranViewTemplate {
	IQuranTranslation trans;
	IQuranText quran;

	public MixedViewTemplate(IQuranText quran, IQuranTranslation trans) {
		this.quran = quran;
		this.trans = trans;
		engine.put("TEXT_LAYOUT", ApplicationConfig.LINE_BY_LINE);
		engine.put("MIXED", "true");
	}

	public String transform(int sura) {
		String[] quranSura = quran.getSura(sura);
		String[] transSura = trans.getSura(sura);
		String[][] mixed = new String[quranSura.length][2];
		for (int i = 0; i < mixed.length; i++) {
			mixed[i][0] = quranSura[i];
			mixed[i][1] = transSura[i];
		}
		engine.put("AYA_LIST", mixed);
		return super.transform(sura);
	}
}
