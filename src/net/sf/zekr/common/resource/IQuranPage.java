/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     May 6, 2008
 */
package net.sf.zekr.common.resource;

/**
 * @author Mohsen Saboorian
 */
public interface IQuranPage {
	/**
	 * @return 1-base page number
	 */
	public abstract int getPageNum();

	//	/**
	//	 * @return the number of ayas in this page
	//	 */
	//	public abstract int getPageSize();

	/**
	 * @return Quran page lower bound (inclusive)
	 */
	public abstract IQuranLocation getFrom();

	/**
	 * @return Quran page upper bound (inclusive)
	 */
	public abstract IQuranLocation getTo();
}
