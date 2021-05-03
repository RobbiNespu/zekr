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
 * This class holds paging data of type juz, which means each page contains a single juz of the Quran.
 * 
 * @author Mohsen Saboorian
 */
public class JuzPagingData extends AbstractQuranPagingData {
	public static final String ID = "<juz>";

	public JuzPagingData() {
		name = meaning("JUZ");
		id = ID;

		pageList = new ArrayList<QuranPage>();
		List<JuzProperties> juzList = QuranProperties.getInstance().getJuzList();
		IQuranLocation oldLoc;
		oldLoc = new QuranLocation(1, 1);
		for (int i = 1; i < juzList.size(); i++) {
			JuzProperties juz = juzList.get(i);
			IQuranLocation loc = juz.getLocation();
			QuranPage qp = new QuranPage();
			qp.setIndex(i);
			qp.setFrom(oldLoc);
			qp.setTo(loc.getPrev());
			oldLoc = loc;
			pageList.add(qp);
		}
		QuranPage qp = new QuranPage();
		qp.setIndex(juzList.size());
		qp.setFrom(oldLoc);
		SuraProperties lastSura = QuranPropertiesUtils.getSura(QuranPropertiesUtils.QURAN_SURA_COUNT);
		qp.setTo(new QuranLocation(QuranPropertiesUtils.QURAN_SURA_COUNT, lastSura.getAyaCount()));
		pageList.add(qp);
	}
}
