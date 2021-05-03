/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 16, 2006
 */
package net.sf.zekr.engine.bookmark;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohsen Saboorian
 */
public class BookmarkSetGroup {
	List<BookmarkSet> list = new ArrayList<BookmarkSet>();
	private BookmarkSet defaultBookmarkSet;

	public BookmarkSetGroup() {
	}

	/**
	 * Adds a bookmark set to the list of bookmark sets group. <code>bookmarkSet.getFileName()</code> should
	 * not be null, and should be unique among other bookmark sets, since it is used as a key to store this
	 * bookmark set.
	 * 
	 * @param bookmarkSet the bookmark set to be added
	 */
	public void addBookmarkSet(BookmarkSet bookmarkSet) {
		list.add(bookmarkSet);
	}

	public void removeBookmarkSet(BookmarkSet bookmarkSet) {
		list.remove(bookmarkSet);
	}

	public BookmarkSet removeBookmarkSet(String bookmarkSetId) {
		BookmarkSet bms = getBookmarkSet(bookmarkSetId);
		if (bms != null) {
			list.remove(bms);
		}
		return bms;
	}

	/**
	 * @return the default bookmark set
	 */
	public BookmarkSet getDefault() {
		return defaultBookmarkSet;
	}

	public void setAsDefault(BookmarkSet bookmarkSet) {
		defaultBookmarkSet = bookmarkSet;
	}

	public List<BookmarkSet> getBookmarkSets() {
		return list;
	}

	public BookmarkSet getBookmarkSet(String bookmarkSetId) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().equals(bookmarkSetId)) {
				return list.get(i);
			}
		}
		return null;
	}

	public boolean containsId(String id) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
}
