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
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

/**
 * Instances of this class hold search results. This class supports iteration.
 * 
 * @author Mohsen Saboorian
 */
public class SearchResultModel {
	protected int resultPerPage;
	protected int totalMatch;
	protected String rawQuery;
	protected String clause;
	protected int maxResultPerPage;
	private IQuranText quranText;

	/**
	 * A list of matched <code>SearchResultItem</code>s.
	 */
	protected List<SearchResultItem> results;

	/**
	 * Current search page.
	 */
	protected int pageNum;

	public class Iter implements Iterator<List<SearchResultItem>> {
		public boolean hasNext() {
			return (pageNum * resultPerPage <= results.size());
		}

		public List<SearchResultItem> next() {
			return getPage(pageNum++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Cannot remove a search item!");
		}
	}

	/**
	 * Create an instance of this class to hold search results.
	 * 
	 * @param quranText Quran or translation text on which search taken place
	 * @param results a list of {@link SearchResultItem}s
	 * @param clause a string representation of whole-words of all the matched text parts
	 * @param rawQuery raw user query
	 * @param totalMatch total count of all highlighted text parts
	 * @param ayaComparator the {@link AbstractSearchResultComparator} to be used for sorting results
	 * @param ascending
	 */
	public SearchResultModel(IQuranText quranText, List<SearchResultItem> results, String clause, String rawQuery,
			int totalMatch, AbstractSearchResultComparator ayaComparator, boolean ascending) {
		this.quranText = quranText;
		this.results = results;
		this.totalMatch = totalMatch;
		this.resultPerPage = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");
		this.clause = clause;
		this.rawQuery = rawQuery;

		maxResultPerPage = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");

		// sort ayas and order results
		if (ayaComparator != null)
			Collections.sort(results, ayaComparator);
		if (!ascending)
			Collections.reverse(results); // sort descending
	}

	/**
	 * Retrieves the specified page of search results.
	 * 
	 * @param page page number (zero-based)
	 * @return requested page
	 * @throws NoSuchElementException if no such page exists
	 */
	public List<SearchResultItem> getPage(int page) {
		if (page * resultPerPage > results.size())
			throw new NoSuchElementException("No such page: " + page);

		if ((page + 1) * resultPerPage <= results.size())
			return results.subList(page * resultPerPage, (page + 1) * resultPerPage);

		return results.subList(page * resultPerPage, results.size());
	}

	public List<SearchResultItem> getResults() {
		return results;
	}

	public int getResultCount() {
		return results.size();
	}

	/**
	 * @return the total number of pages of the result, based on {@link #resultPerPage}. If {@link #results}
	 *         size is 0, this method returns 1.
	 */
	public int getResultPageCount() {
		if (results.size() == 0) {
			return 1;
		}
		return (int) Math.ceil((double) results.size() / resultPerPage);
	}

	public int getTotalMatch() {
		return totalMatch;
	}

	public IQuranText getQuranText() {
		return quranText;
	}

	/**
	 * Create and return a new iterator to iterate on pages of {@link SearchResultItem}s.
	 * 
	 * @return
	 */
	public Iterator<List<SearchResultItem>> iterator() {
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
