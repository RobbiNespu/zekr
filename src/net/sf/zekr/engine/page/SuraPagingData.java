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

import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.SuraProperties;

/**
 * This class holds paging data of type sura, which means each page contains a single sura of the Quran.
 * 
 * @author Mohsen Saboorian
 */
public class SuraPagingData extends AbstractQuranPagingData {
	public SuraPagingData() {
		this.name = meaning("SURA");
		this.id = "<sura>";

		pageList = new ArrayList();
		List suraList = QuranProperties.getInstance().getSuraList();
		for (int i = 1; i <= suraList.size(); i++) {
			SuraProperties sura = (SuraProperties) suraList.get(i - 1);
			QuranPage qp = new QuranPage();
			qp.setIndex(i);
			qp.setFrom(new QuranLocation(i, 1));
			qp.setTo(new QuranLocation(i, sura.getAyaCount()));
			pageList.add(qp);
		}
	}
}
