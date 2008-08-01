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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.ISearchScorer;
import net.sf.zekr.engine.search.SearchException;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.ZeroScorer;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;
import net.sf.zekr.engine.search.tanzil.PatternEnricherFactory.ArabicPatternEnricher;
import net.sf.zekr.engine.translation.TranslationData;

import org.apache.commons.lang.StringUtils;

class ZeroHighlighter implements ISearchResultHighlighter {
	public String highlight(String text, String pattern) {
		return text;
	}
}

/**
 * An advanced search, mainly inspired and converted from <a href="http://tanzil.info">tanzil.info</a>
 * JavaScript and PHP code.
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class AdvancedQuranTextSearch {
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

	public AdvancedQuranTextSearch(IQuranText quranText, ISearchResultHighlighter highlighter, ISearchScorer searchScorer) {
		if (highlighter == null)
			this.highlighter = new ZeroHighlighter();
		else
			this.highlighter = highlighter;
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

	public SearchResultModel search(String rawQuery) throws SearchException {
		logger.debug("Searching for query: " + rawQuery);
		rawQuery = rawQuery.replaceAll("\\-", "!");

		PatternEnricher enricher;
		if (quranText instanceof TranslationData) { // it's translation
			enricher = PatternEnricherFactory.getEnricher(((TranslationData) quranText).getLocale().getLanguage());
		} else { // it's Quran
			enricher = PatternEnricherFactory.getEnricher("ar");
			enricher.setParameter(ArabicPatternEnricher.IGNORE_HARAKA, Boolean.FALSE);
		}
		String pattern = enricher.enrich(rawQuery);

		String highlightPattern = pattern.replaceAll("[+!]", "|");
		highlightPattern.replaceAll("^[|]+", "").replaceAll("!", "");// remove leading '|'s
		logger.debug("Rewritten query: " + pattern);

		pattern = StringUtils.replace(pattern, "!", "+!");
		if (pattern.startsWith("+")) {
			pattern = pattern.substring(1);
		}
		String[] patterns = pattern.split("\\+");
		Set clauses = new LinkedHashSet();
		List intermediateResult = locations;
		for (int i = 0; i < patterns.length; i++) {
			// TODO: for queries with patterns.length > 1, first search for larger (more filtering) patterns[i]
			String p = patterns[i];
			boolean exclude;
			if (exclude = (p.charAt(0) == '!'))
				p = p.substring(1);
			intermediateResult = filterBucket(intermediateResult, p, exclude, i == 0);
		}

		// extract matched parts and clauses
		int total = 0;
		List resultItems = intermediateResult;
		for (int i = 0; i < patterns.length; i++) {
			if (patterns[i].charAt(0) == '!') // ignore exclude patterns
				continue;
			Pattern regex = Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE);
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
		logger.debug("Score and highlight search results.");
		scoreSearchResult(resultItems, highlightPattern, patterns.length);

		return new SearchResultModel(quranText, resultItems, CollectionUtils.toString(clauses, " "), rawQuery, total,
				searchResultComparator, ascending);
	}

	/**
	 * Score and highlight the search result.
	 * 
	 * @param resultItems
	 * @param highlightPattern
	 * @param patternCount
	 */
	private void scoreSearchResult(List resultItems, String highlightPattern, int patternCount) {
		for (int i = 0; i < resultItems.size(); i++) {
			SearchResultItem sri = (SearchResultItem) resultItems.get(i);
			sri.score = searchScorer.score(sri);
			sri.score /= (double) patternCount;
			sri.text = highlighter.highlight(sri.text, highlightPattern);
		}
	}

	private List filterBucket(List intermediateResult, String pattern, boolean exclude, boolean firstTime)
			throws SearchException {
		try {
			List res = new ArrayList();
			Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
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
			}
			return res;
		} catch (PatternSyntaxException pse) {
			logger.implicitLog(pse);
			throw new SearchException(pse.getMessage());
		}
	}

	private String getClause(String text, Matcher matcher) {
		int a = text.substring(0, matcher.start()).lastIndexOf(' ');
		int b = text.indexOf(' ', matcher.end() - 1);
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
		s = "\"محسن\"";
		s = "محسن";
		System.out.println(RegexUtils.enrichPattern(s, false));
		System.out.println("Initialize AdvancedQuranTextSearch" + new Date());
		AdvancedQuranTextSearch ats = new AdvancedQuranTextSearch(QuranText.getSimpleTextInstance(),
				new SimpleSearchResultHighlighter(), new DefaultSearchScorer());
		System.out.println(s);
		System.out.println("Before search: " + new Date());
		SearchResultModel res = ats.search(s);
		System.out.println("After search: " + new Date());
		System.out.println(res.getTotalMatch() + " - " + res.getResults().size() + ":");
		for (Iterator iterator = res.getResults().iterator(); iterator.hasNext();) {
			SearchResultItem sri = (SearchResultItem) iterator.next();
			System.out.println(sri);
		}
	}
}
