/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 17, 2005
 */
package net.sf.zekr.engine.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.resource.AbstractRangedQuranText;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.search.AbstractQuranSearch;
import net.sf.zekr.engine.search.QuranSearch;
import net.sf.zekr.engine.search.Range;
import net.sf.zekr.engine.search.SearchUtils;
import net.sf.zekr.engine.theme.ThemeData;

/**
 * @author Mohsen Saboorian
 * @deprecated old basic search method is not used anymore.
 */
public class QuranSearchResultTemplate extends AbstractSearchResultTemplate {

	public QuranSearchResultTemplate(AbstractRangedQuranText quran, String keyword, boolean matchDiac) {
		super(quran, keyword, matchDiac);
	}

	public String doTransform() {
		AbstractQuranSearch qs;
		String ret = null;
		Map result = new LinkedHashMap();
		try {
			qs = new QuranSearch(quran, matchDiac);

			logger.info("Search started: " + keyword);
			Date date1 = new Date();
			boolean ok = qs.findAll(result, keyword); // find...
			Date date2 = new Date();
			logger.info("Search for " + keyword + " finished; it took " + (date2.getTime() - date1.getTime()) + " ms.");

			engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT", new String[] {
					i18n.localize(String.valueOf(qs.getResultCount())), i18n.localize(String.valueOf(result.size())) }));
			if (!ok) // more that maxAyaMatch ayas was matched
				engine.put("TOO_MANY_RESULT", langEngine.getDynamicMeaning("TOO_MANY_RESULT", new String[] { ""
						+ result.size() }));

			engine.put("AYA_LIST", refineQuranResult(result).entrySet());
			String k = matchDiac ? SearchUtils.replaceLayoutSimilarCharacters(keyword) : SearchUtils
					.arabicSimplify(keyword);
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { k }));

			ThemeData theme = config.getTheme().getCurrent();
			ret = engine.getUpdated(theme.getPath() + "/" + resource.getString("theme.search.result"));
		} catch (Exception e) {
			logger.log(e);
		}
		return ret;

	}

	/**
	 * @param result
	 * @return a map of locations
	 */
	private Map refineTransResult(Map result) {
		Map ret = new LinkedHashMap(result.size());
		for (Iterator iter = result.keySet().iterator(); iter.hasNext();) {
			IQuranLocation loc = (IQuranLocation) iter.next();
			String aya = quran.get(loc);
			ret.put(loc, aya);
		}
		return ret;
	}

	/**
	 * @param result
	 * @return a map of locations
	 */
	private final Map refineQuranResult(Map result) {
		Map ret = new LinkedHashMap(result.size());
		List l;
		for (Iterator iter = result.keySet().iterator(); iter.hasNext();) {
			int pre = 0;
			int post = 0;

			IQuranLocation loc = (IQuranLocation) iter.next();
			List list = (List) result.get(loc);
			l = new ArrayList();
			String aya = quran.get(loc.getSura(), loc.getAya());
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Range range = (Range) iterator.next();
				if (range.from < post)
					continue;
				pre = range.from;

				l.add(new String(aya.substring(post, pre)));

				post = range.to;
				l.add(new String(aya.substring(pre, post)));
			}
			if (post < aya.length())
				l.add(new String(aya.substring(post, aya.length())));

			ret.put(loc, l);
		}

		return ret;
	}
}
