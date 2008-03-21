/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 24, 2007
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.regex.Pattern;

public class SimpleSearchHighlighter implements ISearchHighlighter {
	public String highlight(String text, String pattern) {
		text = text.replaceAll('(' + pattern + ')', "◄$1►");
		text = text.replaceAll("◄\\s", " ◄").replaceAll("\\s►", "► ");
		text = text.replaceAll("([^\\s]*)◄", "◄$1").replaceAll("►([^\\s]*)", "$1►");
		while (Pattern.matches("◄[^\\s]*◄", text)) {
			text = text.replaceAll("(◄[^\\s]*)◄", "$1").replaceAll("►([^\\s]*►)", "$1");
		}
		text = text.replaceAll("◄([^◄►]*)►", "<span class=\"highlight\"> $1 </span>");
		// str = str.replaceAll("◄([^◄►]*)►", "[$1]");
		return text;
	}
}
