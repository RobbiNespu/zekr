/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2006
 */
package net.sf.zekr.ui;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface EventProtocol {
	/**
	 * Refresh the view
	 */
	String REFRESH_VIEW = "REFRESH_VIEW";

	/**
	 * Semi-restart the application
	 */
	String RECREATE_VIEW = "RECREATE_VIEW";

	/**
	 * Clear <tt>cache</tt> directory on application exit
	 */
	String CLEAR_CACHE_ON_EXIT = "CLEAR_CACHE_ON_EXIT";

	/**
	 * Tells the shell to update bookmarks menu (as bookmark tree changed)
	 */
	String UPDATE_BOOKMARKS_MENU = "UPDATE_BOOKMARKS_MENU";
	String GOTO_LOCATION = "GOTO_LOCATION";
}
