/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 29, 2008
 */
package net.sf.zekr.common.resource.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranText;

public class Page {
	private int index;
	private List<Aya> ayaList;
	private IQuranPage quranPage;

	public Page(IQuranPage quranPage) throws IOException {
		this(QuranText.getInstance(), quranPage);
	}

	public Page(IQuranText quranText, IQuranPage quranPage) {
		this.quranPage = quranPage;
		ayaList = new ArrayList<Aya>();
		IQuranLocation from = quranPage.getFrom();
		IQuranLocation to = quranPage.getTo();
		while (from != null && to.compareTo(from) >= 0) {
			ayaList.add(new Aya(quranText, from));
			from = from.getNext();
		}
	}

	public Aya getAya(int aya) {
		return ayaList.get(aya);
	}

	public List<Aya> getAyaList() {
		return ayaList;
	}

	public int getSize() {
		return ayaList.size();
	}
}
