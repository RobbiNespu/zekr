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

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;

/**
 * @author Mohsen Saboorian
 */
public interface IPagingData {

	public String getId();

	public String getName();

	/**
	 * @param pageNum page number (counted from 1)
	 * @return quran page
	 */
	public IQuranPage getQuranPage(int pageNum);

	/**
	 * @return a list of all {@link IQuranPage}s
	 */
	public List<? extends IQuranPage> getPageList();

	public int size();

	public void load() throws PagingException;

	/**
	 * Find the page containing <code>location</code>.
	 * 
	 * @param location Quran location to be looked up in all pages
	 * @return the page containing Quran location
	 */
	public IQuranPage getContainerPage(IQuranLocation location);
}
