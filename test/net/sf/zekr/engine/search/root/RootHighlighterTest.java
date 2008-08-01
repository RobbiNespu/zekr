/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.engine.search.root;

import java.util.Arrays;

import net.sf.zekr.ZekrBaseTest;

public class RootHighlighterTest extends ZekrBaseTest {
	public RootHighlighterTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testHighlight() {
		final String str = "This wildcard matches any sequence of zero or more letters. For example, the search phrase...";
		final String resuls = "<span class=\"highlight\">This</span> wildcard matches "
				+ "<span class=\"highlight\">any</span> <span class=\"highlight\">sequence</span>"
				+ " of zero or more letters. For example, <span class=\"highlight\">the</span> "
				+ "search <span class=\"highlight\">phrase...</span>";
		RootHighlighter rh = new RootHighlighter();
		assertEquals(rh.highlight(str, Arrays.asList(new Integer[] { new Integer(1), new Integer(4), new Integer(5),
				new Integer(13), new Integer(15) })), resuls);
	}
}
