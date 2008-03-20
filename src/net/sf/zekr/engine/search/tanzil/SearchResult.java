/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.zekr.common.config.ApplicationConfig;

public class SearchResult {
	private int resultPerPage;
	private int totalMatch;

	/**
	 * a list of matched Quran locations
	 */
	public List results;
	private int pageNum;

	public SearchResult(List results, int totalMatch, AbstractAyaComparator ayaComparator) {
		this.results = results;
		this.totalMatch = totalMatch;
		this.resultPerPage = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");
		this.pageNum = 0;

		// sort ayas based on result sorter
		if (ayaComparator != null)
			Collections.sort(results, ayaComparator);

	}

	public SearchResult(List res, int total) {
		this(res, total, null);
	}

	/**
	 * Retrieves the specified page of search results.
	 * 
	 * @param page page number (zero-based)
	 * @return requested page
	 * @throws NoSuchElementException if no such page exists
	 */
	public List getPage(int page) {
		if (page * resultPerPage > results.size())
			throw new NoSuchElementException("No such page: " + page);

		if ((page + 1) * resultPerPage <= results.size())
			return results.subList(page * resultPerPage, (page + 1) * resultPerPage);

		return results.subList(page * resultPerPage, results.size());

	}

	public int getResultCount() {
		return results.size();
	}

	public int getResultPageCount() {
		return (int) Math.ceil((double) results.size() / resultPerPage);
	}

	public int getTotalMatch() {
		return totalMatch;
	}
}
