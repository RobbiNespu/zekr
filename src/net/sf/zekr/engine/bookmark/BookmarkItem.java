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
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;

public class BookmarkItem {
	private String name;
	private String description;
	private List locations;
	private List children = new ArrayList();
	private boolean folder;

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
}
