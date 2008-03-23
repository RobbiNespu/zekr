/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 18, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

import org.apache.commons.lang.StringUtils;

class ZeroHighlighter implements ISearchResultHighlighter {
	public String highlight(String text, String pattern) {
		return text;
	}
}

class ZeroScorer implements ISearchScorer {
	public double score(SearchResultItem sri) {
		return 0;
	}
}

/**
 * An advanced search, mainly inspired and converted from <a href="http://tanzil.info">tanzil.info</a>
 * JavaScript and PHP code.
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class AdvancedTextSearch {
	private Logger logger = Logger.getLogger(this.getClass());
	private ISearchResultHighlighter highlighter;
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
		logger.debug("Initializing searchable locations.");
		IQuranLocation[] locs = QuranPropertiesUtils.getLocations();
		for (int i = 0; i < locs.length; i++) {
			if (!searchScope.includes(locs[i]))
				locations.add(locs[i]);
		}
		logger.debug("Searching through '" + locations.size() + "' ayas.");
	}

	public void setSearchResultComparator(AbstractSearchResultComparator searchResultComparator) {
		this.searchResultComparator = searchResultComparator;
	}

	public void setSearchScorer(ISearchScorer searchScorer) {
		this.searchScorer = searchScorer;
	}

	public AdvancedTextSearch(IQuranText quranText, ISearchResultHighlighter highlighter, ISearchScorer searchScorer) {
		if (highlighter == null)
			this.highlighter = new ZeroHighlighter();
		else
			this.highlighter = highlighter;
		if (searchScorer == null)
			this.searchScorer = new ZeroScorer();
		else
			this.searchScorer = searchScorer;
		this.quranText = quranText;
		this.locations = Arrays.asList(QuranPropertiesUtils.getLocations());
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isAscending() {
		return ascending;
	}

	public SearchResult search(String rawQuery) {
		logger.debug("Searching for query: " + rawQuery);
		rawQuery = rawQuery.replaceAll("\\-", "!");
		String pattern = RegexSeachUtils.enrichPattern(rawQuery, false);

		// TODO: '!' characters present in the highlighter pattern, but it seems to be safe
		String highlightPattern = pattern.replaceAll("\\+", "|").replaceAll("!", "");
		logger.debug("Rewritten query: " + pattern);

		pattern = StringUtils.replace(pattern, "!", "+!");
		if (pattern.startsWith("+")) {
			pattern = pattern.substring(1);
		}
		String[] patterns = pattern.split("\\+");
		Set clauses = new LinkedHashSet();
		List intermediateResult = locations;
		for (int i = 0; i < patterns.length; i++) {
			// TODO: for queries with patterns.length > 1, first search for larger patterns[i]
			String p = patterns[i];
			boolean exclude;
			if (exclude = (p.charAt(0) == '!'))
				p = p.substring(1);
			intermediateResult = filterBucket(intermediateResult, p, exclude, i == 0);
		}

		int total = 0;
		List resultItems = intermediateResult;
		for (int i = 0; i < patterns.length; i++) {
			if (patterns[i].charAt(0) == '!') // ignore exclude patterns
				continue;
			Pattern regex = Pattern.compile(patterns[i]);
			for (int j = 0; j < resultItems.size(); j++) {
				SearchResultItem sri = (SearchResultItem) resultItems.get(j);
				Matcher matcher = regex.matcher(sri.text);
				while (matcher.find()) {
					total++;
					sri.matchedParts.add(new String(matcher.group()));
					clauses.add(getClause(sri.text, matcher));
				}
			}
		}

		// score and highlight results
		logger.debug("Score results.");
		for (int i = 0; i < resultItems.size(); i++) {
			SearchResultItem sri = (SearchResultItem) resultItems.get(i);
			sri.score = searchScorer.score(sri);
			sri.score /= (double) patterns.length;
			sri.text = highlighter.highlight(sri.text, highlightPattern);
		}

		return new SearchResult(resultItems, CollectionUtils.toString(clauses, ", "), rawQuery, total,
				searchResultComparator, ascending);
	}

	private List filterBucket(List intermediateResult, String pattern, boolean exclude, boolean firstTime) {
		List res = new ArrayList();
		Pattern regex = Pattern.compile(pattern);
		for (int i = 0; i < intermediateResult.size(); i++) {
			Matcher matcher;
			String line;
			IQuranLocation loc;
			if (firstTime) {
				loc = (IQuranLocation) intermediateResult.get(i);
				line = ' ' + quranText.get(loc) + ' ';
			} else {
				SearchResultItem sri = (SearchResultItem) intermediateResult.get(i);
				loc = sri.location;
				line = sri.text;
			}
			matcher = regex.matcher(line);
			if ((matcher.find() ^ exclude)) {
				if (firstTime)
					res.add(new SearchResultItem(line, loc));
				else
					res.add((SearchResultItem) intermediateResult.get(i));
			}
			//			if (matcher.find()) {
			//				if (!lastTime) {
			//					if (!exclude) {
			//						clauses.add(matcher.group());
			//						res.add(loc);
			//					}
			//				} else {
			//					List wholeClases = new ArrayList();
			//					do {
			//						String clause = getClause(line, matcher);
			//						items++; // one item is already matched in previous matcher.find()
			//						wholeClases.add(clause);
			//					} while (matcher.find());
			//					clauses.addAll(wholeClases);
			//					total += items;
			//					if (!exclude)
			//						
			//				}
			//			} else if (exclude) {
			//				if (!lastTime) {
			//					res.add(loc);
			//				} else {
			//					res.add(new SearchResultItem(line, loc));
			//				}
			//			}
		}
		return res;
	}

	private String getClause(String text, Matcher matcher) {
		int a = text.substring(0, matcher.start()).lastIndexOf(' ');
		int b = text.indexOf(' ', matcher.end());
		return new String(text.substring(a + 1, b));
	}

	public static void main(String[] args) throws IOException {
		String s = "";
		s = "سَلام";
		s = "-حسن";
		s = "عنی اذا";
		s = "\"هو الذی\" لا \"الله\" به";
		s = "-حسن غضب";
		s = "\"سلام علی\"";
		s = "عنی إذا";
		s = "سلام علی";
		System.out.println(RegexSeachUtils.enrichPattern(s, false));
		System.out.println("Initialize AdvancedTextSearch" + new Date());
		AdvancedTextSearch ats = new AdvancedTextSearch(QuranText.getSimpleTextInstance(),
				new SimpleSearchResultHighlighter(), new DefaultSearchScorer());
		System.out.println(s);
		System.out.println("Before search: " + new Date());
		SearchResult res = ats.search(s);
		System.out.println("After search: " + new Date());
		System.out.println(res.getTotalMatch() + " - " + res.getResults().size() + ":");
		for (Iterator iterator = res.getResults().iterator(); iterator.hasNext();) {
			SearchResultItem sri = (SearchResultItem) iterator.next();
			System.out.println(sri);
		}
	}
}
