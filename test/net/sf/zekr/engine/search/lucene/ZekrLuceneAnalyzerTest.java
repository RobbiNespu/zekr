/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 25, 2008
 */

package net.sf.zekr.engine.search.lucene;

import java.io.StringReader;

import net.sf.zekr.ZekrBaseTest;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 * Test case for {@link ZekrLuceneAnalyzer} class. It should test with as many language as possible.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrLuceneAnalyzerTest extends ZekrBaseTest {
	private static final String ARABIC_STR_ORIG1 = "وَنَزَّلْنَا عَلَيْكَ الْكِتَابَ تِبْيَانًا لِّكُلِّ شَيْءٍ وَهُدًى وَرَحْمَةً وَبُشْرَى لِلْمُسْلِمِينَ";
	private static final String ARABIC_STR1 = "ونزلنا عليك الكتاب تبيانا لكل شيء وهدي ورحمت وبشري للمسلمين";

	private static final String ARABIC_STR_ORIG2 = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ ۖفَلْيَسْتَجِيبُوا لِي وَلْيُؤْمِنُوا بِي لَعَلَّهُمْ يَرْشُدُونَ";
	private static final String ARABIC_STR2 = "واذا سالك عبادي عني فاني قريب اجيب دعوت الداع اذا دعان فليستجيبوا لي وليومنوا بي لعلهم يرشدون";

	public ZekrLuceneAnalyzerTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNextToken1() throws Exception {
		ZekrLuceneAnalyzer zla = new ZekrLuceneAnalyzer(ZekrLuceneAnalyzer.QURAN_LANG_CODE, null);
		TokenStream ts1 = zla.tokenStream(null, new StringReader(ARABIC_STR_ORIG1));
		TokenStream ts2 = new WhitespaceTokenizer(new StringReader(ARABIC_STR1));
		boolean hasMore = ts1.incrementToken();
		ts2.incrementToken();
		TermAttribute t1 = (TermAttribute) ts1.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
		TermAttribute t2 = (TermAttribute) ts2.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
		while (hasMore) {
			assertEquals(new String(t1.termBuffer(), 0, t1.termLength()), new String(t2.termBuffer(), 0, t2.termLength()));
			hasMore = ts1.incrementToken();
			ts2.incrementToken();
			t1 = (TermAttribute) ts1.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
			t2 = (TermAttribute) ts2.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
		}
	}

	public void testNextToken2() throws Exception {
		ZekrLuceneAnalyzer zla = new ZekrLuceneAnalyzer(ZekrLuceneAnalyzer.QURAN_LANG_CODE, null);
		TokenStream ts1 = zla.tokenStream(null, new StringReader(ARABIC_STR_ORIG2));
		TokenStream ts2 = new WhitespaceTokenizer(new StringReader(ARABIC_STR2));
		boolean hasMore = ts1.incrementToken();
		ts2.incrementToken();
		TermAttribute t1 = (TermAttribute) ts1.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
		TermAttribute t2 = (TermAttribute) ts2.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
		while (hasMore) {
			assertEquals(new String(t1.termBuffer(), 0, t1.termLength()), new String(t2.termBuffer(), 0, t2.termLength()));
			hasMore = ts1.incrementToken();
			ts2.incrementToken();
			t1 = (TermAttribute) ts1.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
			t2 = (TermAttribute) ts2.getAttribute(org.apache.lucene.analysis.tokenattributes.TermAttribute.class);
		}
	}
}
