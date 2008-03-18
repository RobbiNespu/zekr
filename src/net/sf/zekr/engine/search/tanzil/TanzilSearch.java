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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.zekr.common.resource.QuranText;

import org.apache.commons.lang.StringUtils;

class Result {
	public Result(String[] results, int totalMatch) {
		this.results = results;
		this.totalMatch = totalMatch;
	}
	public int totalMatch;
	public String[] results;
}

public class TanzilSearch {

	public static Result search(String pattern) {
		pattern = StringUtils.replace(pattern, "!", "!+");
		String[] patterns = pattern.split("\\+");
		// $bucket = range(0, 6235); // whole text
		String[] bucket = new String[6236];

		int total = 0;
		for (int i = 0; i < patterns.length; i++) {
			Result res = filterBucket(bucket, patterns[i]);
			bucket = res.results;
			total = (i == 0) ? res.totalMatch : bucket.length;
		}
		return new Result(bucket, total);
	}

	// filter bucket according to a pattern
	private static Result filterBucket(String[] bucket, String pattern) {
		QuranText quran = null;
		try {
			quran = QuranText.getInstance();
		} catch (IOException e) {
		}
		int total = 0;
		List res = new ArrayList();
		boolean exclude;
		if (exclude = (pattern.charAt(0) == '!'))
			pattern = pattern.substring(1);

		Pattern regex = Pattern.compile(pattern);
		for (int i = 1; i <= bucket.length; i++) {
			String line = ' ' + quran.get(i) + ' ';
			Matcher matcher = regex.matcher(line);
			int items = 0;
			while (matcher.find()) {
				items++;
				System.out.println(matcher.group(0));
			}
			total += items;
			if (items > 0 ^ exclude)
				res.add("" + i);
		}
		return new Result((String[]) res.toArray(new String[0]), total);
	}

	public static void main(String[] args) {
		// String s = "سَلام";
		// String s = "\"هو الذی\" لا \"الله\" به";
		String s = "\"سلام علی\"";
		s = s.replaceAll("\\-", "!");
		s = RegexSeachUtils.enrichPattern(s, false);
		System.out.println(s);
		Result res = search(s);
		System.out.println(res.totalMatch);
		System.out.println(Arrays.asList(res.results));
	}
}
