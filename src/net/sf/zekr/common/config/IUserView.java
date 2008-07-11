/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 10, 2008
 */
package net.sf.zekr.common.config;

import net.sf.zekr.common.resource.IQuranLocation;

public interface IUserView {
	public IQuranLocation getLocation();

	public void setLocation(IQuranLocation location);

	/**
	 * @return current page number.
	 */
	public int getPage();

	public void setPage(int page);

	public void changeTo(int page);

	public void changeTo(IQuranLocation location);

	public void synchPage();

	public void synchLocation();
}
