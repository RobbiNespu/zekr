/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 29, 2005
 */
package net.sf.zekr.engine.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.zekr.common.resource.IRangedQuranText;

/**
 * This is a sample implementation of the Quran translation search. It only supports non-diacritic match
 * searching.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.4
 */
public class TranslationSearch extends AbstractQuranSearch {

	public TranslationSearch(IRangedQuranText trans) {
		this(trans, false, null);
	}

	public TranslationSearch(IRangedQuranText trans, boolean matchCase, Locale locale) {
		super(trans, true, matchCase, locale);

		// trans.getTranslationData().load();
	}

	protected final List find(String src, String keyword) {
		Range r = finder.indexOf(src, keyword);
		if (r == null)
			return null;

		List ret = new ArrayList();
		while (r != null) {
			ret.add(r);
			r = finder.indexOf(src, keyword, r.to);
		}

		return ret;
	}
}
