/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.theme;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.IQuranTranslation;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class TranslationViewTemplate extends AbstractQuranViewTemplate {
	IQuranTranslation text;

	public TranslationViewTemplate(IQuranTranslation text) {
		this.text = text;
		engine.put("TEXT_LAYOUT", config.getViewProp("view.transLayout"));
		engine.put("TRANSLATION", "true");
	}

	public String transform(int sura) {
		engine.put("AYA_LIST", text.getSura(sura));
		return super.transform(sura);
	}
}