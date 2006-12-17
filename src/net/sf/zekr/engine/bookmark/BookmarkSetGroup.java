/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 16, 2006
 */
package net.sf.zekr.engine.bookmark;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class BookmarkSetGroup {
	Map map = new HashMap();
	private BookmarkSet defaultBookmarkSet;

	public BookmarkSetGroup() {
	}

	/**
	 * Adds a bookmark set to the list of bookmark sets group. <code>bookmarkSet.getName()</code> should not
	 * be null, and should be unique among other bookmark sets, since it is used as a key to store this
	 * bookmark set.
	 * 
	 * @param bookmarkSet the bookmark set to be added
	 */
	public void addBookmarkSet(BookmarkSet bookmarkSet) {
		map.put(bookmarkSet.getName(), bookmarkSet);
	}

	public void removeBookmarkSet(String bookmarkSetName) {
		map.remove(bookmarkSetName);
	}

	public BookmarkSet getBookmarkSet(String bookmarkSetName) {
		return (BookmarkSet) map.get(bookmarkSetName);
	}

	/**
	 * @return the default bookmark set
	 */
	public BookmarkSet getDefault() {
		return defaultBookmarkSet;
	}

	/**
	 * Makes a previously-added bookmark set as the default bookmark set
	 */
	public void setAsDefault(String bookmarkSetName) {
		defaultBookmarkSet = (BookmarkSet) map.get(bookmarkSetName);
	}

	Collection getBookmarkSets() {
		return map.values();
	}
}
