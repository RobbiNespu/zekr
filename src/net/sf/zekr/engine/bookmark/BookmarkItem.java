/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 1, 2006
 */
package net.sf.zekr.engine.bookmark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;

public class BookmarkItem {
	private String name;
	private String description;
	private List locations;
	private List children = new ArrayList();
	private boolean folder;

	/** A unique identifier (among other bookmark items in a single tree) for looking up this bookmark item. */
	private String id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List getLocations() {
		return locations;
	}

	public void setLocations(List locations) {
		this.locations = locations;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public List getChildren() {
		return children;
	}

	public void addChild(BookmarkItem bmItem) {
		children.add(bmItem);
	}

	public String toString() {
		return folder ? name : name + "-" + locations;
	}

	public Object clone() {
		BookmarkItem bi = new BookmarkItem();
		bi.setName(getName());
		bi.setDescription(getDescription());
		if (locations != null)
			bi.setLocations(new ArrayList(locations));
		bi.setFolder(isFolder());
		bi.children = new ArrayList(children);
		return bi;
	}

	public void clearChilrden() {
		children.clear();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Looks for a <code>BookmarkItem</code> in the children of this bookmark recursively.
	 * 
	 * @param bookmarkItem the bookmark to be looked up
	 * @return <code>true</code> if such a bookmark item found, <code>false</code> otherwise.
	 */
	public boolean hasDescendant(BookmarkItem bookmarkItem) {
		if (children.size() == 0)
			return false;

		int i = children.indexOf(bookmarkItem);
		if (i != -1)
			return true;

		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			BookmarkItem child = (BookmarkItem) iterator.next();
			if (child.hasDescendant(bookmarkItem))
				return true;
		}

		return false;
	}

	/**
	 * If <code>obj</code> is of type <code>BookmarkItem</code>, just checks if its ID is the same with
	 * <code>this.id</code>. Returns <code>obj.equals(this)</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof BookmarkItem)
			return ((BookmarkItem) obj).id == id;
		return obj.equals(this);
	}

//	public void removeChild(BookmarkItem item) {
//		children.remove(item);
//	}
}
