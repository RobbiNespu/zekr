/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 7, 2008
 */
package net.sf.zekr.common.config;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.engine.page.QuranPaging;

/**
 * @author Mohsen Saboorian
 */
public class UserViewController implements IUserView {
	private IQuranLocation location;
	private int page;
	private QuranPaging quranPaging;

	public UserViewController(QuranPaging quranPaging) {
		this.quranPaging = quranPaging;
	}

	public IQuranLocation getLocation() {
		return location;
	}

	public void setLocation(IQuranLocation location) {
		this.location = location;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void changeTo(int page) {
		IQuranPage iqp = quranPaging.getDefault().getQuranPage(page);
		setPage(page);
		setLocation(iqp.getFrom());
	}

	public void changeTo(IQuranLocation location) {
		setLocation(location);
		page = quranPaging.getDefault().getContainerPage(location).getPageNum();
	}
}
