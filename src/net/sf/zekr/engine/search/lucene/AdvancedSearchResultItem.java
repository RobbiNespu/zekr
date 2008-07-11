/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 1, 2007
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.search.SearchResultItem;

/**
 * @deprecated replaced by {@link SearchResultItem}
 * @author Mohsen Saboorian
 */
public class AdvancedSearchResultItem {
	public String contents;
	public IQuranLocation location;

	public AdvancedSearchResultItem(IQuranLocation location, String resultStr) {
		this.contents = resultStr;
		this.location = location;
	}

	public String getContents() {
		return contents;
	}

	public IQuranLocation getLocation() {
		return location;
	}
}
