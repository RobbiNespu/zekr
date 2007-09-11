/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranText;

/**
 * @author Mohsen Saboorian
 */
public class QuranViewTemplate extends AbstractQuranViewTemplate {

	public QuranViewTemplate(IQuranText quran, int suraNum, int ayaNum) {
		super(quran, suraNum, ayaNum);
		engine.put("TEXT_LAYOUT", config.getViewProp("view.quranLayout"));
		engine.put("AYA_LIST", quran.getSura(suraNum));
		if (config.getViewLayout().equals(ApplicationConfig.SEPARATE_LAYOUT)) {
//			engine.put("NO_AUDIO", Boolean.TRUE);
		}
	}
}
