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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.resource.AbstractRangedQuranText;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.search.AbstractQuranSearch;
import net.sf.zekr.engine.search.Range;
import net.sf.zekr.engine.search.SearchUtils;
import net.sf.zekr.engine.search.TranslationSearch;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * @author Mohsen Saboorian
 * @deprecated as old basic search is depricated, this class is not used anymore.
 */
public class TransSearchResultTemplate extends AbstractSearchResultTemplate {
	private boolean matchCase;

	public TransSearchResultTemplate(AbstractRangedQuranText trans, String keyword, boolean matchCase) {
		super(trans, keyword);
		this.matchCase = matchCase;
		engine.put("TRANSLATE", langEngine.getMeaning("QURAN"));
		engine.put("TRANSLATION", "true");
		engine.put("TRANS_DIRECTION", trans.getTranslationData().direction);
		engine.put("KEYWORD", keyword);
	}

	public String doTransform() throws TemplateTransformationException {
		try {
			AbstractQuranSearch qs;
			String ret = null;
			TranslationData td = quran.getTranslationData();
			qs = new TranslationSearch(quran, matchCase, td.locale);
			Map result = new LinkedHashMap();

			boolean ok = qs.findAll(result, keyword); // Search over the whole Quran translation

			engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT", new String[] {
					i18n.localize(String.valueOf(qs.getResultCount())),
					i18n.localize(String.valueOf(result.size())) }));

			if (!ok) // more that maxAyaMatch ayas was matched
				engine.put("TOO_MANY_RESULT", langEngine.getDynamicMeaning("TOO_MANY_RESULT", new String[] { ""
						+ result.size() }));
			else
				engine.put("TOO_MANY_RESULT", null);

			engine.put("AYA_LIST", refineQuranResult(result).entrySet());
			String k = SearchUtils.arabicSimplify(keyword);
			engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { k }));
			ThemeData theme = config.getTheme().getCurrent();
			ret = engine.getUpdated(theme.getPath() + "/" + resource.getString("theme.search.result"));
			return ret;
		} catch (Exception e) {
			throw new TemplateTransformationException(e);
		}
	}

	/**
	 * Converts a <code>Map</code> of <code>QuranLocation</code> to <code>List</code> of
	 * <code>Rage</code>s to a <code>Map</code> of <code>QuranLocation</code> to list of aya string
	 * fragments.
	 * 
	 * @param result
	 * @return a map of locations
	 */
	private Map refineQuranResult(Map result) {
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
