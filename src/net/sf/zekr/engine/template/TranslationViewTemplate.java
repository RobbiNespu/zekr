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
public class TranslationViewTemplate extends AbstractQuranViewTemplate {

	public TranslationViewTemplate(IQuranText trans, int suraNum, int ayaNum) {
		super(trans, suraNum, ayaNum);
		engine.put("TEXT_LAYOUT", config.getViewProp("view.transLayout"));
		engine.put("TRANSLATION", "true");
		// put sura ayas
		engine.put("AYA_LIST", quran.getSura(suraNum));
	}
}
