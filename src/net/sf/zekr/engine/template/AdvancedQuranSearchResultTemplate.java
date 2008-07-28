/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 25, 2007
 */
package net.sf.zekr.engine.template;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.search.AbstractSearchResult;
import net.sf.zekr.engine.search.SearchUtils;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * @author Mohsen Saboorian
 */
public class AdvancedQuranSearchResultTemplate extends AbstractSearchResultTemplate {
	private AbstractSearchResult searchResult;
	int pageNo;

	/**
	 * @param qts QuranTextSearcher instance
	 * @param pageNo counted from 1
	 */
	public AdvancedQuranSearchResultTemplate(AbstractSearchResult searchResult, int pageNo) {
		super(null, null, false);
		engine.put("ADVANCED", "true");
		this.searchResult = searchResult;
		this.pageNo = pageNo;
	}

	public String doTransform() throws TemplateTransformationException {
		try {
			String ret = null;
			engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT", new String[] {
					i18n.localize(String.valueOf(searchResult.getTotalMatch())),
					i18n.localize(String.valueOf(searchResult.getResultCount())) }));
			engine.put("AYA_LIST", searchResult.getPage(pageNo));
			engine.put("PAGE_START_NUM", new Integer(pageNo * searchResult.getMaxResultPerPage()));
			engine.put("PAGE_NUM_MSG", langEngine.getDynamicMeaning("SEARCH_PAGE", new String[] {
					i18n.localize(String.valueOf(pageNo + 1)),
					i18n.localize(String.valueOf(searchResult.getResultPageCount())) }));
			engine.put("CLAUSE", searchResult.getClause());
			// String k = SearchUtils.arabicSimplify(searchResult.getRawQuery());
			// engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { k }));
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { searchResult.getRawQuery() }));

			IQuranText iqt = searchResult.getQuranText();
			if (iqt instanceof TranslationData) {
				engine.put("TRANSLATE", langEngine.getMeaning("QURAN"));
				engine.put("TRANSLATION", "true");
				engine.put("TRANS_DIRECTION", iqt.getTranslationData().direction);
			} else {
				engine.put("TRANSLATE", langEngine.getMeaning("TRANSLATION"));
			}

			ThemeData theme = config.getTheme().getCurrent();
			ret = engine.getUpdated(theme.getPath() + "/" + resource.getString("theme.search.result"));
			return ret;
		} catch (Exception e) {
			throw new TemplateTransformationException(e);
		}
	}
}
