/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.engine.page;

import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
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

	protected String meaning(String key) {
		return lang.getMeaningById("QURAN_PAGING", key);
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.engine.page.IPagingData#toString()
	 */
	public String toString() {
		return getId() + ": (" + getName() + ")";
	}
}
