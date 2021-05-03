/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.model.Page;

/**
 * @author Mohsen Saboorian
 */
public class TranslationViewTemplate extends AbstractPageViewTemplate {

	public TranslationViewTemplate(IQuranText trans, IUserView userView) {
		super(trans, userView);
		engine.put("TEXT_LAYOUT", config.getViewProp("view.transLayout"));
		engine.put("TRANSLATION", "true");
		engine.put("AYA_LIST", new Page(trans, quranPage).getAyaList());
	}
}
