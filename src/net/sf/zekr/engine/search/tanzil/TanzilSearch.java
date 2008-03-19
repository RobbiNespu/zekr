/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 18, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.engine.search.SearchScope;

import org.apache.commons.lang.StringUtils;

class Result {
	public Result(List results, int totalMatch) {
		this.results = results;
		this.totalMatch = totalMatch;
	}
	public int totalMatch;
	public List results;
}

class ZeroHighlighter implements ISearchHighlighter {
	public String highlight(String text) {
		return text;
	}

	public String highlight(String text, String matchedQueryPart) {
		return text;
	}
}

public class TanzilSearch {

	private ISearchHighlighter highlighter;
	private SearchScope searchScope;
	private IQuranText quranText;

	private List locations;
	private Result result;

	public TanzilSearch(IQuranText quranText, ISearchHighlighter highlighter) {
		this(quranText, highlighter, null, null);
	}

	public TanzilSearch(IQuranText quranText, SearchScope searchScope) {
		this(quranText, new ZeroHighlighter(), searchScope, null);
	}

	public TanzilSearch(IQuranText quranText, ISearchHighlighter highlighter, SearchScope searchScope,
			QuranAyaComparator ayaComparator) {

		this.highlighter = highlighter;
		this.searchScope = searchScope;
		this.quranText = quranText;

		IQuranLocation[] locs = QuranPropertiesUtils.getLocations();
		if (searchScope != null) {
			for (int i = 0; i < locs.length; i++) {
				if (!searchScope.includes(locs[i]))
					locations.add(locs[i]);
			}
		} else {
			locations = Arrays.asList(locs);
		}

		// sort ayas based on result sorter (aya comparator passed)
		if (ayaComparator != null)
			Collections.sort(locations, ayaComparator);
	}

	public void search(String pattern) {
		pattern = StringUtils.replace(pattern, "!", "!+");
		String[] patterns = pattern.split("\\+");

		int total = 0;
		for (int i = 0; i < patterns.length; i++) {
			Result res = filterBucket(locations, patterns[i]);
			locations = res.results;
			total = (i == 0) ? res.totalMatch : locations.size();
		}
		result = new Result(locations, total);
	}

	private Result filterBucket(List locations, String pattern) {
		int total = 0;
		List res = new ArrayList();
		boolean exclude;
		if (exclude = (pattern.charAt(0) == '!'))
			pattern = pattern.substring(1);

		Pattern regex = Pattern.compile(pattern);
		for (int i = 0; i < locations.size(); i++) {
			IQuranLocation loc = (IQuranLocation) locations.get(i);
			String line = ' ' + quranText.get(loc.getSura(), loc.getAya()) + ' ';
			Matcher matcher = regex.matcher(line);
			int items = 0;
			while (matcher.find()) {
				items++;
			}
			total += items;
			if (items > 0 ^ exclude)
				res.add(loc);
		}
		return new Result(res, total);
	}

	public Result getResult() {
		return result;
	}

	public static void main(String[] args) throws IOException {
		String s = "سَلام";
		//		String s = "\"هو الذی\" لا \"الله\" به";
		//		String s = "\"سلام علی\"";
		// s = s.replaceAll("\\-", "!");
		s = RegexSeachUtils.enrichPattern(s, false);

		TanzilSearch ts = new TanzilSearch(QuranText.getSimpleTextInstance(), new SimpleSearchHighlighter(), null, null);
		System.out.println(s);
		ts.search(s);
		Result res = ts.getResult();
		System.out.println(res.totalMatch + " - " + res.results.size() + ":");
		System.out.println(res.results);
	}

}
