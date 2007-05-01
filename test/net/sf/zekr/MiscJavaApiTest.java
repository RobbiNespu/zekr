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
}
