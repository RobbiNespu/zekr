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

import net.sf.zekr.common.resource.IQuranPage;

/**
 * @author Mohsen Saboorian
 */
public interface IPagingData {

	public abstract String getId();

	public abstract String getName();

	/**
	 * @param pageNum page number (counted from 1)
	 * @return
	 */
	public abstract IQuranPage getQuranPage(int pageNum);

	/**
	 * @return a list of all {@link IQuranPage}s
	 */
	public abstract List getPageList();

	public abstract int size();

	public abstract void load() throws PagingException;

}
