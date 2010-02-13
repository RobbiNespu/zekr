/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.engine.page;

import java.util.ArrayList;

import net.sf.zekr.common.resource.QuranPropertiesUtils;

/**
 * This class holds paging data of type fixed aya, which means each page contains a fixed number of ayas of
 * the Quran.
 * 
 * @author Mohsen Saboorian
 */
public class FixedAyaPagingData extends AbstractQuranPagingData {
	public static final String ID = "<fixedAya>";

	private int ayaPerPage;

	public FixedAyaPagingData(int ayaPerPage) {
		name = meaning("CONST_AYA");
		id = ID;
		init(ayaPerPage);
	}

	private void init(int ayaPerPage) {
		this.ayaPerPage = ayaPerPage;
		loadPaging();
	}

	private void loadPaging() {
		pageList = new ArrayList<QuranPage>();

		int max = QuranPropertiesUtils.QURAN_AYA_COUNT;
		int pageCount = max / ayaPerPage;
		for (int i = 0; i < pageCount; i++) {
			QuranPage page = new QuranPage();
			page.setIndex(i + 1);
			page.setFrom(QuranPropertiesUtils.getLocation(i * ayaPerPage + 1));
			page.setTo(QuranPropertiesUtils.getLocation((i + 1) * ayaPerPage));
			pageList.add(page);
		}
		if (pageCount * ayaPerPage < max) {
			QuranPage page = new QuranPage();
			page.setIndex(pageCount + 1);
			page.setFrom(QuranPropertiesUtils.getLocation(pageCount * ayaPerPage + 1));
			page.setTo(QuranPropertiesUtils.getLocation(max));
			pageList.add(page);
		}
	}

	public int getAyaPerPage() {
		return ayaPerPage;
	}

	public void reload(int aya) {
		init(aya);
	}
}
