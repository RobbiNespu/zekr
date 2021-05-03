/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.template;

import java.io.IOException;

import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.model.Page;

/**
 * @author Mohsen Saboorian
 */
public class QuranViewTemplate extends AbstractPageViewTemplate {
	public QuranViewTemplate(IQuranText quran, IUserView userView) throws IOException {
		super(quran, userView);
		engine.put("TEXT_LAYOUT", config.getViewProp("view.quranLayout"));
		engine.put("AYA_LIST", new Page(quran, quranPage).getAyaList());
	}
}
