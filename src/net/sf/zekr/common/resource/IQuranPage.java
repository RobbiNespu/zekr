/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     May 6, 2008
 */
package net.sf.zekr.common.resource;

public interface IQuranPage {

	public abstract int getIndex();

	public abstract IQuranLocation getFrom();

	public abstract IQuranLocation getTo();

}
