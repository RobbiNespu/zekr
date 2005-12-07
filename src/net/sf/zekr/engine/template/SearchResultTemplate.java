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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.runtime.RuntimeUtilities;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranLocation;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchQuran;

public class SearchResultTemplate {
	IQuranText quran;
	private final static Logger logger = Logger.getLogger(SearchResultTemplate.class);

	public String transform(String keyword) { // FIXIT: ptoblem with "æ"
		LanguageEngine langEngine = ApplicationConfig.getInsatnce().getLanguageEngine();
		SearchQuran sq;
		String ret = null;
		TemplateEngine engine = TemplateEngine.getInstance();
		try {
			sq = new SearchQuran(quran = QuranText.getInstance());
			Map result = sq.findAll(keyword);
			engine.put("KEYWORD", keyword);
			engine.put("AYA_LIST", refineResult(result).entrySet());			
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] {keyword}));

			engine.put("APP_PATH", RuntimeUtilities.RUNTIME_DIR.replaceAll("\\\\", "/"));
			engine.put("DIRECTION", langEngine.getDirection());

			ret = engine.getUpdated(ApplicationPath.SEARCH_RESULT_TEMPLATE);
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
		Map ret = new HashMap(result.size());
		List l;
		for (Iterator iter = result.keySet().iterator(); iter.hasNext();) {
			int pre = 0;
			int post = -1;

			QuranLocation loc = (QuranLocation) iter.next();
			List list = (List) result.get(loc);
			l = new ArrayList();
			String aya = quran.get(loc.getSoora(), loc.getAya());
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Integer index = (Integer) iterator.next();
				int i = index.intValue();
				if (i < post)
					continue;
				pre = aya.substring(0, i + 1).lastIndexOf(' ');
//				if (pre == -1) pre = -1;

				l.add(new String(aya.substring(post + 1, pre + 1)));

				post = aya.indexOf(' ', i);
				if (post == -1) {
					l.add(new String(aya.substring(pre + 1, aya.length())));
					break;
				}
				else
					l.add(new String(aya.substring(pre + 1, post)));
			}
			if (post != -1)
				l.add(new String(aya.substring(post + 1, aya.length())));
			
			ret.put(loc, l);
		}

		return ret;
	}
}
