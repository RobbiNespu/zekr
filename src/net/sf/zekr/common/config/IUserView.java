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

/**
 * @author Mohsen Saboorian
 */
public interface IUserView {
	/**
	 * View mode corresponds to basic search.
	 */
	public static final int VM_SEARCH = 1;

	/**
	 * View mode corresponds to advanced search.
	 */
	public static final int VM_ADVANCED_SEARCH = 2;

	/**
	 * View mode corresponds to root search.
	 */
	public static final int VM_ROOT_SEARCH = 3;

	/**
	 * View mode corresponds to simple non-search mode set in the Quran browser.
	 */
	public static final int VM_QURAN_TRANS = 4;

	public IQuranLocation getLocation();

	public void setLocation(IQuranLocation location);

	/**
	 * @return current page number.
	 */
	public int getPage();

	public void setPage(int page);

	public void changeTo(int page);

	public void changeTo(IQuranLocation location);

	/**
	 * Synchronize internal page number with the current location.
	 */
	public void synchPage();

	/**
	 * Synchronize internal location with the current page number.
	 */
	public void synchLocation();

	/**
	 * Set current runtime view mode. Can be one of the <code>VM_*</code> constants available in this
	 * interface.
	 * 
	 * @param viewMode runtime view mode. Can be one of {@link #VM_SEARCH}, {@link #VM_ADVANCED_SEARCH},
	 *           {@link #VM_ROOT_SEARCH}, or {@link #VM_QURAN_TRANS}.
	 */
	public void setViewMode(int viewMode);

	/**
	 * @return current runtime view mode. Can be one of {@link #VM_SEARCH}, {@link #VM_ADVANCED_SEARCH},
	 *         {@link #VM_ROOT_SEARCH}, or {@link #VM_QURAN_TRANS}.
	 */
	public int getViewMode();
}
