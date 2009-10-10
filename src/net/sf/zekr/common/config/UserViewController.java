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
 * This class holds values of current location and page of user view.
 * 
 * @author Mohsen Saboorian
 */
public class UserViewController implements IUserView {
	private IQuranLocation location;
	private int page;
	private QuranPaging quranPaging;
	private int viewMode;

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
		setPage(page);
		synchLocation();
	}

	public void changeTo(IQuranLocation location) {
		setLocation(location);
		synchPage();
	}

	public void synchPage() {
		page = quranPaging.getDefault().getContainerPage(location).getPageNum();
	}

	public void synchLocation() {
		IQuranPage iqp = quranPaging.getDefault().getQuranPage(page);
		setLocation(iqp.getFrom());
	}

	public int getViewMode() {
		return viewMode;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	@Override
	public String toString() {
		return String.format("%s:%s-%s", location.getSura(), location.getAya(), page);
	}
}
