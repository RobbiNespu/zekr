/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 1, 2007
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.common.resource.QuranLocation;

public class AdvancedSearchResultItem {
	public String contents;
	public QuranLocation location;

	public AdvancedSearchResultItem(QuranLocation location, String resultStr) {
		this.contents = resultStr;
		this.location = location;
	}

	public String getContents() {
		return contents;
	}

	public QuranLocation getLocation() {
		return location;
	}
}
