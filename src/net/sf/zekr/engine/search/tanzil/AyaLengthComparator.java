/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.io.IOException;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranText;

/**
 * This comparator compares ayas based on their length (including the diacritics and signs).
 * 
 * @author Mohsen Saboorian
 */
public class AyaLengthComparator extends AbstractAyaComparator {
	private IQuranText quranText;

	public AyaLengthComparator() throws IOException {
		this(QuranText.getSimpleTextInstance());
	}

	public AyaLengthComparator(IQuranText quranText) {
		this.quranText = quranText;
	}

	public int compare(IQuranLocation ql1, IQuranLocation ql2) {
		int l1 = quranText.get(ql1).length();
		int l2 = quranText.get(ql2).length();
		return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
	}
}
