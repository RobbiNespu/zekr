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

import net.sf.zekr.common.resource.IRangedQuranText;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class QuranSearch extends AbstractQuranSearch {

	/**
	 * Creates a new instance of this class with match diacritic set to <code>true</code>.
	 * 
	 * @param quran the input Quran source
	 */
	public QuranSearch(IRangedQuranText quran) {
		this(quran, false);
	}

	public QuranSearch(IRangedQuranText quran, boolean matchDiac) {
		super(quran, matchDiac, true, null);
	}

	protected List find(String src, String keyword) {
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
