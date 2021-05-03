/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Hamid Zarrabi-Zadeh, Mohsen Saboorian
 * Start Date:     Mar 24, 2007
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.regex.Pattern;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.filter.QuranFilterUtils;

/**
 * Simple search result highlighter. This class is optimized for fast highlighting by compiling almost all the
 * highlighting patterns once.
 * 
 * @author Hamid Zarrabi-Zadeh
 * @author Mohsen Saboorian
 */
public class SimpleSearchResultHighlighter implements ISearchResultHighlighter {
	private static Pattern p1 = Pattern.compile("◄\\s");
	private static Pattern p2 = Pattern.compile("\\s►");
	private static Pattern p3 = Pattern.compile("([^\\s]*)◄");
	private static Pattern p4 = Pattern.compile("►([^\\s]*)");
	private static Pattern p5 = Pattern.compile("◄[^\\s]*◄");
	private static Pattern p6 = Pattern.compile("(◄[^\\s]*)◄");
	private static Pattern p7 = Pattern.compile("►([^\\s]*►)");
	private static Pattern p8 = Pattern.compile("◄([^◄►]*)►");
	private int highlightCount;

	public String highlight(String text, String pattern) {
		text = Pattern.compile('(' + pattern + ')', Pattern.CASE_INSENSITIVE).matcher(text).replaceAll("◄$1►");
		text = p1.matcher(text).replaceAll(" ◄");
		text = p2.matcher(text).replaceAll("► ");

		text = p3.matcher(text).replaceAll("◄$1");
		text = p4.matcher(text).replaceAll("$1►");

		while (p5.matcher(text).find()) {
			text = p6.matcher(text).replaceAll("$1");
			text = p7.matcher(text).replaceAll("$1");
		}

		String format = FORMAT_STRING_REGEX;
		try {
			format = ApplicationConfig.getInstance().getProps().getString("view.search.regexHighlightFormat",
					FORMAT_STRING_REGEX);
		} catch (Exception e) {
			// silently ignore it.
		}
		text = p8.matcher(text).replaceAll(format);

		// FIXME: this is a Quran-text specific method call!
		return QuranFilterUtils.filterSearchResult(text);
	}
}
