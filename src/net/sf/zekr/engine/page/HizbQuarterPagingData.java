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
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SuraProperties;

/**
 * This class holds paging data of type hizb quarter, which means each page contains a single hizb quad of the
 * Quran.
 * 
 * @author Mohsen Saboorian
 */
public class HizbQuarterPagingData extends AbstractQuranPagingData {
	public static final String ID = "<hizbQuarter>";

	public HizbQuarterPagingData() {
		name = meaning("HIZB_QUARTER");
		id = ID;

		pageList = new ArrayList<QuranPage>();
		List<JuzProperties> juzList = QuranProperties.getInstance().getJuzList();

		for (int i = 0; i < juzList.size(); i++) {
			JuzProperties juz = juzList.get(i);
			IQuranLocation[] hizbQuads = juz.getHizbQuarters();
			QuranPage prevPage = null;
			for (int j = 0; j < hizbQuads.length; j++) {
				QuranPage page = new QuranPage();
				page.setIndex(i * 8 + j + 1);
				page.setFrom(hizbQuads[j]);
				if (prevPage != null) {
					prevPage.setTo(page.getFrom().getPrev());
				}
				prevPage = page;
				pageList.add(page);
			}
			if (i < juzList.size() - 1) {
				prevPage.setTo((juzList.get(i + 1)).getLocation().getPrev());
			} else {
				SuraProperties lastSura = QuranPropertiesUtils.getSura(QuranPropertiesUtils.QURAN_SURA_COUNT);
				prevPage.setTo(new QuranLocation(QuranPropertiesUtils.QURAN_SURA_COUNT, lastSura.getAyaCount()));
			}
		}
	}
}
