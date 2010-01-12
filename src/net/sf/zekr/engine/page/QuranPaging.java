/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.engine.page;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class contains {@link CustomPagingData} loaded from *.page.xml files from <tt>res/text/metadata</tt>.
 * 
 * @author Mohsen Saboorian
 */
public class QuranPaging {
	private IPagingData defaultPagingData;
	private Map<String, IPagingData> pagingList = new LinkedHashMap<String, IPagingData>();

	public void add(IPagingData pd) {
		pagingList.put(pd.getId(), pd);
	}

	public IPagingData get(String pagingId) {
		return pagingList.get(pagingId);
	}

	public Collection<IPagingData> getAllPagings() {
		return pagingList.values();
	}

	public void setDefault(IPagingData defaultPagingData) {
		this.defaultPagingData = defaultPagingData;
	}

	public IPagingData getDefault() {
		return defaultPagingData;
	}
}
