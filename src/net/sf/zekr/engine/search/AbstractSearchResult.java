/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.engine.search;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.search.tanzil.SearchResultComparator;

public class AbstractSearchResult {
	protected int resultPerPage;
	protected int totalMatch;
	protected String rawQuery;
	protected String clause;
	protected int maxResultPerPage;

	/**
	 * A list of matched <code>SearchResultItem</code>s.
	 */
	protected List results;

	/**
	 * Current search page.
	 */
	protected int pageNum;

	public class Iter implements Iterator {
		public boolean hasNext() {
			return (pageNum * resultPerPage <= results.size());
		}

		public Object next() {
			return getPage(pageNum++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Cannot remove a search item!");
		}
	}

	/**
	 * @param results
	 * @param clase clause in the text which are either matched or excluded. This string holds a list of items
	 *           which are encountered by the exploded query and are not neccessarily present in
	 *           <code>results</code> list.
	 * @param totalMatch
	 * @param ayaComparator
	 */
	public AbstractSearchResult(List results, String clause, String rawQuery, int totalMatch,
			SearchResultComparator ayaComparator) {
		this.results = results;
		this.totalMatch = totalMatch;
		this.resultPerPage = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");
		this.clause = clause;
		this.rawQuery = rawQuery;

		maxResultPerPage = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");

		// sort ayas based on result sorter
		if (ayaComparator != null)
			Collections.sort(results, ayaComparator);
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

	public List getResults() {
		return results;
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

	/**
	 * Create and return a new iterator to iterate on pages of {@link SearchResultItem}s.
	 * 
	 * @return
	 */
	public Iterator iterator() {
		return new Iter();
	}

	public String getRawQuery() {
		return rawQuery;
	}

	public String getClause() {
		return clause;
	}

	public int getMaxResultPerPage() {
		return 0;
	}
}
