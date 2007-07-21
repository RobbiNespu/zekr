/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 20, 2007
 */

package net.sf.zekr;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class MiscJavaApiTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCeiling() throws Exception {
		assertEquals((int) Math.ceil((double)15 / 10), 2);
		assertEquals((int) Math.ceil((double)20 / 10), 2);
		assertEquals((int) Math.ceil((double)21 / 10), 3);
		assertEquals((int) Math.ceil((double)0 / 10), 0);
	}
	
	public void testStringReplaceAll() throws Exception {
		String s = "salam\nsalam\n\r salam\r\n salam\r\t";
		String s1 = s.replaceAll("\\r\\n|\\n\\r|\\n|\\r", " ");
		assertEquals(s1, "salam salam  salam  salam \t");
	}
	
	public void testLFCR() throws Exception {
		assertEquals('\n', 10);
		assertEquals('\r', 13);
		// WIN: CR+LF: \r\n: 13+10
		// UNIX: LF: \n: 10
		// MAC: CR: \r: 13
	}
}
