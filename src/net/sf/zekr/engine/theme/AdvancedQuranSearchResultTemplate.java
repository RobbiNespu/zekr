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

import org.apache.lucene.search.BooleanClause;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class AdvancedQuranSearchResultTemplate extends AbstractSearchResultTemplate {
	private QuranTextSearcher searcher;

	public AdvancedQuranSearchResultTemplate(QuranTextSearcher qts, String keyword) {
		super(null, keyword, false);
		engine.put("ADVANCED", "true");
		this.searcher = qts;
	}

	public String transform() {
		String ret = null;
		try {
			engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT", new String[] {
					"" + searcher.getMatchedItemCount(), "" + searcher.getResultCount() }));
			engine.put("AYA_LIST", searcher.nextElement());
			engine.put("CLAUSE", searcher.getQuery().toString(QuranTextIndexer.CONTENTS_FIELD));
			String k = SearchUtils.arabicSimplify(keyword);
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { k }));
			ThemeData theme = config.getTheme().getCurrent();
			ret = engine.getUpdated(theme.getPath() + "/" + resource.getString("theme.search.result"));
		} catch (Exception e) {
			logger.log(e);
		}
		return ret;
	}
}
