/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 29, 2008
 */
package net.sf.zekr.common.resource.model;

import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;

public class Page {
	private int index;
	private List ayaList;
	private IQuranPage quranPage;

	public Page(IQuranPage quranPage) {
		this.quranPage = quranPage;
		IQuranLocation from = quranPage.getFrom();
		IQuranLocation to = quranPage.getFrom();
		while (to.compareTo(from) >= 0) {
			ayaList.add(new Aya(from));
			from = from.getNext();
		}
	}

	public Aya getAya(int aya) {
		return (Aya) ayaList.get(aya);
	}

	public List getAyaList() {
		return ayaList;
	}
}
