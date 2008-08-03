/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.engine.search.root;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.filter.QuranFilterUtils;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.common.util.StringUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.root.RootAddress;
import net.sf.zekr.engine.search.ISearchScorer;
import net.sf.zekr.engine.search.SearchException;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.ZeroScorer;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

public class QuranRootSearch {
	private final Logger logger = Logger.getLogger(this.getClass());
	private final ApplicationConfig config = ApplicationConfig.getInstance();
	private RootHighlighter highlighter;
	private SearchScope searchScope;
	private IQuranText quranText;
	private ISearchScorer searchScorer;

	private List locations;
	private AbstractSearchResultComparator searchResultComparator;
	private boolean ascending = true;

	public SearchScope getSearchScope() {
		return searchScope;
	}

	public void setSearchScope(SearchScope searchScope) {
		this.searchScope = searchScope;
		this.locations = CollectionUtils.toArrayList(QuranPropertiesUtils.getLocations());
		if (searchScope != null) {
			logger.debug("Initializing searchable locations.");
			for (int i = locations.size() - 1; i >= 0; i--) {
				if (!searchScope.includes((IQuranLocation) locations.get(i)))
					locations.remove(i);
			}
			logger.debug("Searching through '" + locations.size() + "' ayas.");
		}
	}

	public void setSearchResultComparator(AbstractSearchResultComparator searchResultComparator) {
		this.searchResultComparator = searchResultComparator;
	}

	public void setSearchScorer(ISearchScorer searchScorer) {
		this.searchScorer = searchScorer;
	}

	public QuranRootSearch(IQuranText quranText, ISearchScorer searchScorer) {
		this.highlighter = new RootHighlighter();
		if (searchScorer == null)
			this.searchScorer = new ZeroScorer();
		else
			this.searchScorer = searchScorer;
		this.quranText = quranText;
		setSearchScope(null);
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isAscending() {
		return ascending;
	}

	public SearchResultModel search(String rootStr) throws SearchException {
		logger.debug("Searching for root: " + rootStr);
		List resultItems = new ArrayList();
		Set clauses = new LinkedHashSet();
		List addrList = config.getQuranRoot().getRootAddress(rootStr);
		int totalResult = 0;

		for (int i = 0; i < addrList.size(); i++) {
			List wordIndexList = new ArrayList();

			RootAddress rootAddr = (RootAddress) addrList.get(i);
			IQuranLocation loc = rootAddr.loc;

			// check if the aya is inside valid search locations
			if (!locations.contains(loc)) {
				continue;
			}
			totalResult++;

			wordIndexList.add(new Integer(rootAddr.wordIndex));
			// find occurrences in the same aya
			// it assumes that addrList holds items sorted by Quran location
			while (i < addrList.size()) {
				if (i + 1 < addrList.size()) {
					RootAddress nextAddr = (RootAddress) addrList.get(i + 1);
					if (nextAddr.loc.equals(loc)) {
						totalResult++;
						wordIndexList.add(new Integer(nextAddr.wordIndex));
						i++;
					} else {
						break;
					}
				} else {
					break;
				}
			}

			String aya = quranText.get(loc);
			aya = QuranFilterUtils.filterSearchResult(aya);

			SearchResultItem sri = new SearchResultItem(aya, loc);
			for (int j = 0; j < wordIndexList.size(); j++) {
				int wordIndex = ((Integer) wordIndexList.get(j)).intValue();
				sri.matchedParts.add(StringUtils.getNthWord(aya, wordIndex, ' '));
				clauses.add(StringUtils.getNthWord(aya, wordIndex, ' '));
			}

			sri.score = searchScorer.score(sri);
			sri.text = highlighter.highlight(sri.text, wordIndexList);
			resultItems.add(sri);
		}

		return new SearchResultModel(quranText, resultItems, CollectionUtils.toString(clauses, " "), rootStr,
				totalResult, searchResultComparator, ascending);
	}
}
