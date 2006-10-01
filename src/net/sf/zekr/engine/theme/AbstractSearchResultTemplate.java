/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 22, 2006
 */
package net.sf.zekr.engine.theme;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.IRangedQuranText;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public abstract class AbstractSearchResultTemplate extends BaseViewTemplate {
	protected IRangedQuranText quran;
	protected String keyword;
	protected boolean matchDiac;

	/**
	 * @param quran
	 * @param keyword
	 * @param matchDiac
	 */
	protected AbstractSearchResultTemplate(IRangedQuranText quran, String keyword, boolean matchDiac) {
		this.quran = quran;
		this.keyword = keyword;
		this.matchDiac = matchDiac;

		engine.put("KEYWORD", keyword);
		engine.put("ICON_TRANSLATE", resource.getString("icon.translate"));

		engine.put("NEXT_AYA", langEngine.getMeaning("NEXT_AYA"));
		engine.put("PREV_AYA", langEngine.getMeaning("PREV_AYA"));

		engine.put("GOTO_MSG", langEngine.getMeaning("GOTO"));
		engine.put("SURA_MSG", langEngine.getMeaning("SURA"));
		engine.put("AYA_MSG", langEngine.getMeaning("AYA"));
	}

	/**
	 * @param quran
	 * @param keyword
	 */
	protected AbstractSearchResultTemplate(IRangedQuranText quran, String keyword) {
		this(quran, keyword, false);
	}
}
