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
import java.util.List;
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

import org.apache.commons.lang.StringUtils;

class ZeroHighlighter implements ISearchHighlighter {
	public String highlight(String text) {
		return text;
	}

	public String highlight(String text, String matchedQueryPart) {
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
	private ISearchHighlighter highlighter;
	private SearchScope searchScope;
	private IQuranText quranText;
	private ISearchScorer scorer;

	private List locations;
	private SearchResultComparator ayaComparator;

	public AdvancedTextSearch(IQuranText quranText, ISearchHighlighter highlighter) {
		this(quranText, highlighter, null, null, null);
	}

	public AdvancedTextSearch(IQuranText quranText, SearchScope searchScope) {
		this(quranText, new ZeroHighlighter(), searchScope, null, null);
	}

	public AdvancedTextSearch(IQuranText quranText, ISearchHighlighter highlighter, SearchScope searchScope,
			SearchResultComparator ayaComparator, ISearchScorer scorer) {
		this.highlighter = highlighter;
		this.searchScope = searchScope;
		this.quranText = quranText;
		this.ayaComparator = ayaComparator;
		this.scorer = scorer;

		if (this.scorer == null)
			this.scorer = new ZeroScorer();

		logger.debug("Initializing searchable locations.");
		IQuranLocation[] locs = QuranPropertiesUtils.getLocations();
		if (searchScope != null) {
			for (int i = 0; i < locs.length; i++) {
				if (!searchScope.includes(locs[i]))
					locations.add(locs[i]);
			}
		} else {
			locations = Arrays.asList(locs);
		}
		logger.debug("Searching through '" + locations.size() + "' ayas.");
	}

	public SearchResult search(String rawQuery) {
		logger.debug("Searching for query: " + rawQuery);
		rawQuery = rawQuery.replaceAll("\\-", "!");
		String pattern = RegexSeachUtils.enrichPattern(rawQuery, false);

		// TODO: '!' characters present in the highlighter pattern, but it seems to be safe
		String highlightPattern = pattern.replaceAll("\\+", "|");
		logger.debug("Rewritten query: " + pattern);

		pattern = StringUtils.replace(pattern, "!", "+!");
		if (pattern.startsWith("+")) {
			pattern = pattern.substring(1);
		}
		String[] patterns = pattern.split("\\+");
		List clauses = new ArrayList();
		SearchResult res = null;
		for (int i = 0; i < patterns.length; i++) {
			res = filterBucket(locations, clauses, patterns[i], i + 1 >= patterns.length);
			locations = res.getResults();
		}

		// score results
		logger.debug("Score results.");
		List resultItems = res.getResults();
		for (int i = 0; i < resultItems.size(); i++) {
			SearchResultItem sri = (SearchResultItem) resultItems.get(i);
			for (int j = 0; j < patterns.length; j++) {
				sri.score += scorer.score(sri);
			}
			sri.score /= (double) patterns.length;
		}

		// highlight results
		logger.debug("Highlight results.");
		for (int i = 0; i < resultItems.size(); i++) {
			SearchResultItem sri = (SearchResultItem) resultItems.get(i);
			sri.ayaText = highlighter.highlight(highlightPattern, sri.ayaText);
		}

		return new SearchResult(res.getResults(), CollectionUtils.toString(clauses, ","), rawQuery, res.getTotalMatch(),
				ayaComparator);
	}

	private SearchResult filterBucket(List locations, List clauses, String pattern, boolean lastTime) {
		int total = 0; // this variable is only used if lastTime is true.
		List res = new ArrayList();
		boolean exclude;
		if (exclude = (pattern.charAt(0) == '!'))
			pattern = pattern.substring(1);

		Pattern regex = Pattern.compile(pattern);
		for (int i = 0; i < locations.size(); i++) {
			IQuranLocation loc = (IQuranLocation) locations.get(i);
			String line = ' ' + quranText.get(loc.getSura(), loc.getAya()) + ' ';
			Matcher matcher = regex.matcher(line);

			if (matcher.find()) {
				if (!lastTime) {
					if (!exclude)
						res.add(loc);
				} else {
					int items = 0;
					List matchedParts = new ArrayList();
					do {
						String clause = matcher.group();
						items++; // one item is already matched in previous matcher.find()
						matchedParts.add(clause);
					} while (matcher.find());
					clauses.addAll(matchedParts);
					total += items;
					if (!exclude)
						res.add(new SearchResultItem(line, loc, matchedParts));
				}
			} else if (exclude) {
				if (!lastTime) {
					res.add(loc);
				} else {
					res.add(new SearchResultItem(line, loc, null));
				}
			}
		}
		return new SearchResult(res, total);
	}

	public static void main(String[] args) throws IOException {
		String s = "";
		s = "سَلام";
		s = "عنی إذا";
		s = "\"هو الذی\" لا \"الله\" به";
		s = "-حسن";
		s = "عنی اذا";
		s = "\"سلام علي\"";
		s = "سلام علی";
		s = "-حسن غضب";
		System.out.println(RegexSeachUtils.enrichPattern(s, false));
		System.out.println("Initialize AdvancedTextSearch" + new Date());
		AdvancedTextSearch ats = new AdvancedTextSearch(QuranText.getSimpleTextInstance(), new SimpleSearchHighlighter(),
				null, null, new DefaultSearchScorer());
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
