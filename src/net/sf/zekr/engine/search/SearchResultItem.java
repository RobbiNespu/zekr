/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search;

import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;

public class SearchResultItem {
	public String ayaText;
	public IQuranLocation location;
	public double score;

	/**
	 * matchedParts <code>null</code> matchedParts means that this item was matched because of an exclude
	 * term in the search query
	 */
	public List matchedParts;

	/**
	 * @param ayaText
	 * @param location
	 * @param matchedParts <code>null</code> matchedParts means that this item was matched because of an
	 *           exclude term in the search query
	 */
	public SearchResultItem(String ayaText, IQuranLocation location, List matchedParts) {
		this.ayaText = ayaText;
		this.location = location;
		this.matchedParts = matchedParts;
	}

	public String toString() {
		return location + ":" + ayaText + "(score:" + score + ")";
	}
}
