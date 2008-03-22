/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2007
 */

package net.sf.zekr.engine.search.lucene;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * Test case for <code>ArabicFilter</code> class.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ArabicFilterTest extends TestCase {

	private static final String ARABIC_STR1 = "وَنَزَّلْنَا عَلَيْكَ الْكِتَابَ تِبْيَانًا لِّكُلِّ شَيْءٍ وَهُدًى وَرَحْمَةً وَبُشْرَى لِلْمُسْلِمِينَ";
	private static final String ARABIC_STR2 = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ ۖ فَلْيَسْتَجِيبُوا لِي وَلْيُؤْمِنُوا بِي لَعَلَّهُمْ يَرْشُدُونَ";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

/*	public void testFilterNext1() throws Exception {
		StringReader reader = new StringReader(ARABIC_STR1);
		ArabicAnalyzer aa = new ArabicAnalyzer();
		TokenStream filter = aa.tokenStream(null, reader);
		// TokenStream stream = new WhitespaceTokenizer(new StringReader(ARABIC_STR1));
		// ArabicFilter filter = new ArabicFilter(stream);
		Token t = filter.next();
		assertEquals("ونزلنا", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("عليك", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("الكتاب", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("تبيانا", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("لكل", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("شيء", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("وهدي", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("ورحمت", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("وبشري", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("للمسلمين", new String(t.termBuffer(), 0, t.termLength()));
	}*/

	public void testFilterNext2() throws Exception {
		StringReader reader = new StringReader(ARABIC_STR2);
		ArabicAnalyzer aa = new ArabicAnalyzer();
		TokenStream filter = aa.tokenStream(null, reader);
		// String s = ArabicFilter.simplify(ARABIC_STR2);
		// TokenStream stream = new WhitespaceTokenizer(new StringReader(ARABIC_STR2));
		// ArabicFilter filter = new ArabicFilter(stream);
		Token t = filter.next();
		assertEquals("واذا", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("سالك", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("عبادي", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("عني", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("فاني", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("قريب", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("اجيب", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("دعوت", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("الداع", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("اذا", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("دعان", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("فليستجيبوا", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("لي", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("وليومنوا", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("بي", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("لعلهم", new String(t.termBuffer(), 0, t.termLength()));
		t = filter.next();
		assertEquals("يرشدون", new String(t.termBuffer(), 0, t.termLength()));
	}
}
