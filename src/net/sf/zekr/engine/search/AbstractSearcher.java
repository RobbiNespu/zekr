/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 15, 2010
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;
import net.sf.zekr.engine.search.tanzil.RegexUtils;

/**
 * All searches are subclasses of this class and should implement {@link #doSearch(String)} method.
 * 
 * @author Mohsen Saboorian
 */
public abstract class AbstractSearcher implements ISearcher {
	protected Logger logger = Logger.getLogger(this.getClass());
	protected final ApplicationConfig config = ApplicationConfig.getInstance();
	protected SearchScope searchScope;
	protected AbstractSearchResultComparator searchResultComparator;
	protected boolean ascending = true;

	public SearchResultModel search(String rawQuery) throws SearchException {
		SearchResultModel resultModel = doSearch(rawQuery);
		for (SearchResultItem sri : resultModel.results) {
			sri.text = filter(sri.text);
		}
		return resultModel;
	}

	private String filter(String text) {
		text = RegexUtils.pregReplace(text, "($SHADDA)([$KASRA$KASRATAN])", "$2$1");
		return text;
	}

	/**
	 * @param rawQuery raw query passed from the user interface.
	 * @return search result in a {@link SearchResultModel} instance
	 */
	protected abstract SearchResultModel doSearch(String rawQuery);

	public SearchScope getSearchScope() {
		return searchScope;
	}

	/**
	 * Specifies whether result should be sorted ascending or not.
	 * 
	 * @return
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * @param ascending specifies whether result should be sorted ascending or not.
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void setSearchResultComparator(AbstractSearchResultComparator searchResultComparator) {
		this.searchResultComparator = searchResultComparator;
	}

	public AbstractSearchResultComparator getSearchResultComparator() {
		return searchResultComparator;
	}
}
