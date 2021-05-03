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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.engine.common.LocalizedResource;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;

/**
 * Abstract Quran paging data.
 * 
 * @author Mohsen Saboorian
 */
public abstract class AbstractQuranPagingData extends LocalizedResource implements IPagingData {
	protected final Logger logger = Logger.getLogger(this.getClass());
	protected LanguageEngine lang = LanguageEngine.getInstance();
	protected String id;

	/** List of {@link QuranPage} items. */
	protected ArrayList<QuranPage> pageList;

	public List<QuranPage> getPageList() {
		return pageList;
	}

	public QuranPage getQuranPage(int pageNum) {
		return pageList.get(pageNum - 1);
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
		int page = Collections.binarySearch(pageList, qp, new Comparator<QuranPage>() {
			public int compare(QuranPage page, QuranPage key) {
				if (page.getFrom().compareTo(key.getTo()) > 0) {
					return 1;
				} else if (page.getTo().compareTo(key.getFrom()) < 0) {
					return -1;
				}
				return 0;
			}
		});
		return pageList.get(page);
	}

	protected String meaning(String key) {
		return lang.getMeaningById("PAGING_MODE", key);
	}

	public String toString() {
		return getId() + ": (" + getLocalizedName() + ")";
	}
}
