/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 25, 2007
 */
package net.sf.zekr.engine.theme;

import net.sf.zekr.engine.search.SearchUtils;
import net.sf.zekr.engine.search.lucene.QuranTextIndexer;
import net.sf.zekr.engine.search.lucene.QuranTextSearcher;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class AdvancedQuranSearchResultTemplate extends AbstractSearchResultTemplate {
	private QuranTextSearcher searcher;
	int pageNo;

	/**
	 * @param qts
	 *           QuranTextSearcher instance
	 * @param pageNo
	 *           counted from 1
	 */
	public AdvancedQuranSearchResultTemplate(QuranTextSearcher qts, int pageNo) {
		super(null, null, false);
		engine.put("ADVANCED", "true");
		this.searcher = qts;
		this.pageNo = pageNo;
	}

	public String transform() throws TemplateTransformationException {
		try {
			String ret = null;
			engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT", new String[] {
					"" + searcher.getMatchedItemCount(), "" + searcher.getResultCount() }));
			engine.put("AYA_LIST", searcher.getPage(pageNo));
			engine.put("PAGE_START_NUM", new Integer(pageNo * searcher.getMaxResultPerPage()));
			engine.put("PAGE_NUM_MSG", langEngine.getDynamicMeaning("SEARCH_PAGE", new String[] { String.valueOf(pageNo + 1),
					String.valueOf(searcher.getResultPageCount()) }));
			engine.put("CLAUSE", searcher.getQuery().toString(QuranTextIndexer.CONTENTS_FIELD));
			String k = SearchUtils.arabicSimplify(searcher.getRawQuery());
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { k }));
			ThemeData theme = config.getTheme().getCurrent();
			ret = engine.getUpdated(theme.getPath() + "/" + resource.getString("theme.search.result"));
			return ret;
		} catch (Exception e) {
			throw new TemplateTransformationException(e);
		}
	}
}
