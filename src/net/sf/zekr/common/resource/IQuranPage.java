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
	 * @return 1-base page number.
	 */
	public abstract int getPageNum();

	/**
	 * @return Quran page lower bound
	 */
	public abstract IQuranLocation getFrom();

	/**
	 * @return Quran page upper bound
	 */
	public abstract IQuranLocation getTo();
}
