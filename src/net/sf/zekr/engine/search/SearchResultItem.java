/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search;

import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;

/**
 * @author Mohsen Saboorian
 */
public class SearchResultItem {
	public String text;
	public IQuranLocation location;
	public double score;

	/**
	 * <code>null</code> matchedParts means that this item was matched because of an exclude term in the search
	 * query.
	 */
	public List<String> matchedParts = new ArrayList<String>();

	/**
	 * @param ayaText
	 * @param location
	 */
	public SearchResultItem(String ayaText, IQuranLocation location) {
		this.text = ayaText;
		this.location = location;
	}

	public String toString() {
		return text;
	}

	public String toMeaningfulString() {
		return location + ":" + text + "(score:" + score + ")";
	}

	/**
	 * Method needed for Velocity
	 * 
	 * @return location
	 */
	public IQuranLocation getLocation() {
		return location;
	}

	/**
	 * Method needed for Velocity
	 * 
	 * @return text
	 */
	public String getText() {
		return text;
	}
}
