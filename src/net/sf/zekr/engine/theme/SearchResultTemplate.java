/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 17, 2005
 */
package net.sf.zekr.engine.theme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranLocation;
import net.sf.zekr.common.util.Range;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.QuranSearch;
import net.sf.zekr.engine.search.SearchUtils;

/**
 * @author    Mohsen Saboorian
 * @since	  Zekr 1.0
 * @version   0.2
 */
public class SearchResultTemplate extends BaseViewTemplate {
	IQuranText quran;
	private final static Logger logger = Logger.getLogger(SearchResultTemplate.class);

	public String transform(String keyword, boolean matchDiac) {
		QuranSearch qs;
		String ret = null;
		try {
			qs = new QuranSearch(quran = QuranText.getInstance(), matchDiac);
			Map result = new LinkedHashMap();
			boolean ok = qs.findAll(result, keyword); // find over the Quran
			engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT", new String[] {
					"" + qs.getResultCount(), "" + result.size() }));
			if (!ok) // more that maxAyaMatch ayas was matched
				engine.put("TOO_MANY_RESULT", langEngine.getDynamicMeaning("TOO_MANY_RESULT",
						new String[] { "" + qs.getMaxAyaMatch() }));
			else
				engine.put("TOO_MANY_RESULT", null);

			engine.put("KEYWORD", keyword);
			engine.put("AYA_LIST", refineResult(result).entrySet());
			// engine.put("AYA_LIST", result.entrySet());
			String k = matchDiac ? SearchUtils.replaceSimilarCharacters(keyword) : SearchUtils
					.arabicSimplify(keyword);
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE",
					new String[] { k }));

			engine.put("APP_PATH", GlobalConfig.RUNTIME_DIR.replaceAll("\\\\", "/"));
			engine.put("THEME_DIR", ResourceManager.getInstance().getString("theme.baseDir"));

			ret = engine.getUpdated(ResourceManager.getInstance().getString("theme.default.search.result"));
		} catch (Exception e) {
			logger.log(e);
		}
		return ret;

	}

	/**
	 * @param result
	 * @return a map of locations to
	 */
	private Map refineResult(Map result) {
		Map ret = new LinkedHashMap(result.size());
		List l;
		for (Iterator iter = result.keySet().iterator(); iter.hasNext();) {
			int pre = 0;
			int post = 0;

			QuranLocation loc = (QuranLocation) iter.next();
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
