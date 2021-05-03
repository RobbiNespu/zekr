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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.search.AbstractSearcher;
import net.sf.zekr.engine.search.ISearchScorer;
import net.sf.zekr.engine.search.SearchException;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.ZeroScorer;
import net.sf.zekr.engine.search.tanzil.PatternEnricherFactory.QuranPatternEnricher;
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
public class AdvancedQuranTextSearch extends AbstractSearcher {
	private ISearchResultHighlighter highlighter;
	private IQuranText quranText;
	private ISearchScorer searchScorer;
	private List<IQuranLocation> locations;

	public void setSearchScope(SearchScope searchScope) {
		this.searchScope = searchScope;
		locations = CollectionUtils.toArrayList(QuranPropertiesUtils.getLocations());
		if (searchScope != null) {
			logger.debug("Initializing searchable locations.");
			for (int i = locations.size() - 1; i >= 0; i--) {
				if (!searchScope.includes(locations.get(i))) {
					locations.remove(i);
				}
			}
			logger.debug("Searching through '" + locations.size() + "' ayas.");
		}
	}

	public void setSearchScorer(ISearchScorer searchScorer) {
		this.searchScorer = searchScorer;
	}

	public AdvancedQuranTextSearch(IQuranText quranText, ISearchResultHighlighter highlighter, ISearchScorer searchScorer) {
		if (highlighter == null) {
			this.highlighter = new ZeroHighlighter();
		} else {
			this.highlighter = highlighter;
		}
		if (searchScorer == null) {
			this.searchScorer = new ZeroScorer();
		} else {
			this.searchScorer = searchScorer;
		}
		this.quranText = quranText;
		setSearchScope(null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected SearchResultModel doSearch(String rawQuery) throws SearchException {
		try {
			logger.debug("Searching for query: " + rawQuery);
			rawQuery = rawQuery.replaceAll("\\-", "!");

			boolean thisIsQuran = !quranText.isTranslation();
			PatternEnricher enricher = PatternEnricherFactory.getEnricher(quranText.getLanguage(), thisIsQuran);
			if (thisIsQuran) { // it's translation
				enricher.setParameter(QuranPatternEnricher.IGNORE_HARAKA, Boolean.FALSE);
			}
			String pattern = enricher.enrich(rawQuery);

			String highlightPattern = pattern.replaceAll("[+!]", "|");
			highlightPattern = highlightPattern.replaceAll("^[|]+", "").replaceAll("!", ""); // remove leading '|'s
			logger.debug("Rewritten query: " + pattern);

			pattern = StringUtils.replace(pattern, "!", "+!");
			if (pattern.startsWith("+")) {
				pattern = pattern.substring(1);
			}
			String[] patterns = pattern.split("\\+");
			Set<String> clauses = new LinkedHashSet<String>();
			List intermediateResult = locations;
			for (int i = 0; i < patterns.length; i++) {
				// TODO: for queries with patterns.length > 1, first search for larger (more filtering) patterns[i]
				String p = patterns[i];
				boolean exclude;
				if (exclude = p.charAt(0) == '!') {
					p = p.substring(1);
				}
				intermediateResult = filterBucket(intermediateResult, p, exclude, i == 0, enricher);
			}

			// extract matched parts and clauses
			int total = 0;
			List resultItems = intermediateResult;
			for (int i = 0; i < patterns.length; i++) {
				if (patterns[i].charAt(0) == '!') {
					continue;
				}
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

			return new SearchResultModel(quranText, resultItems, StringUtils.join(clauses, " "), rawQuery, total,
					searchResultComparator, ascending);
		} catch (SearchException se) {
			throw se;
		} catch (Exception e) {
			throw new SearchException(e);
		}
	}

	/**
	 * Score and highlight the search result.
	 * 
	 * @param resultItems
	 * @param highlightPattern
	 * @param patternCount
	 */
	@SuppressWarnings("rawtypes")
   private void scoreSearchResult(List resultItems, String highlightPattern, int patternCount) {
		for (int i = 0; i < resultItems.size(); i++) {
			SearchResultItem sri = (SearchResultItem) resultItems.get(i);
			sri.score = searchScorer.score(sri);
			sri.score /= patternCount;
			sri.text = highlighter.highlight(sri.text, highlightPattern);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List filterBucket(List intermediateResult, String pattern, boolean exclude, boolean firstTime,
			PatternEnricher enricher) throws SearchException {
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
				// matcher = regex.matcher(enricher.enrich(line));
				matcher = regex.matcher(line);
				if (matcher.find() ^ exclude) {
					if (firstTime) {
						res.add(new SearchResultItem(line, loc));
					} else {
						res.add(intermediateResult.get(i));
					}
				}
			}
			return res;
		} catch (PatternSyntaxException pse) {
			logger.implicitLog(pse);
			throw new SearchException(pse.getMessage());
		}
	}

	private String getClause(String text, Matcher matcher) {
		int a = text.substring(0, matcher.start() + 1).lastIndexOf(' ');
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
		s = "ذُكِّرُوا";
		s = ".حسن";
		s = "_حق_";
		System.out.println("Initialize AdvancedQuranTextSearch" + new Date());
		// QuranText text = QuranText.getSimpleTextInstance();
		TranslationData quranText = ApplicationConfig.getInstance().getTranslation().get("makarem");
		PatternEnricher enricher = PatternEnricherFactory
				.getEnricher(quranText.getLanguage(), !quranText.isTranslation());
		System.out.println("Enriched query: " + enricher.enrich(s));
		AdvancedQuranTextSearch ats = new AdvancedQuranTextSearch(quranText, new SimpleSearchResultHighlighter(),
				new DefaultSearchScorer());
		Date d1 = new Date();
		System.out.println("Before search: " + d1);
		SearchResultModel res = ats.doSearch(s);
		Date d2 = new Date();
		System.out.println("Matches: " + res.getClause());
		System.out.println("After search: " + d2 + ". Took: " + (d2.getTime() - d1.getTime()) / 1000.0 + "seconds.");
		System.out.println(res.getTotalMatch() + " - " + res.getResults().size() + ":");
		for (SearchResultItem sri : res.getResults()) {
			System.out.println(sri);
		}
	}
}
