/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.engine.page;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.engine.common.LocalizedResource;
import net.sf.zekr.engine.language.LanguageEngine;

/**
 * Abstract quran paging data.
 * 
 * @author Mohsen Saboorian
 */
public abstract class AbstractQuranPagingData extends LocalizedResource implements IPagingData {
	LanguageEngine lang = LanguageEngine.getInstance();
	protected String id;
	protected String name;

	/** List of {@link QuranPage} items. */
	protected List pageList;

	public String getName() {
		return name;
	}

	public List getPageList() {
		return pageList;
	}

	public IQuranPage getQuranPage(int pageNum) {
		return (IQuranPage) pageList.get(pageNum - 1);
	}

	public void load() throws PagingException {
	}

	public String getId() {
		return id;
	}

	public int size() {
		return pageList.size();
	}

	public IQuranPage getContainerPage(IQuranLocation loc) {
		QuranPage qp = new QuranPage();
		qp.setFrom(loc);
		qp.setTo(loc);
		int page = Collections.binarySearch(pageList, qp, new Comparator() {
			public int compare(Object page, Object key) {
				IQuranPage item = (IQuranPage) page;
				IQuranPage k = (IQuranPage) key;
				if (item.getFrom().compareTo(k.getTo()) > 0)
					return 1;
				else if (item.getTo().compareTo(k.getFrom()) < 0)
					return -1;
				return 0;
			}
		});
		return (IQuranPage) pageList.get(page);
	}

	protected String meaning(String key) {
		return lang.getMeaningById("PAGING_MODE", key);
	}

	public String toString() {
		return getId() + ": (" + getName() + ")";
	}
}
