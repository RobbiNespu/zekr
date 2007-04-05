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
	private static final String ARABIC_STR2 = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ فَلْيَسْتَجِيبُواْ لِي وَلْيُؤْمِنُواْ بِي لَعَلَّهُمْ يَرْشُدُونَ";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFilterNext1() throws Exception {
		TokenStream stream = new WhitespaceTokenizer(new StringReader(ARABIC_STR1));
		ArabicFilter filter = new ArabicFilter(stream);
		assertEquals("ونزلنا", filter.next().termText());
		assertEquals("عليك", filter.next().termText());
		assertEquals("الكتاب", filter.next().termText());
		assertEquals("تبيانا", filter.next().termText());
		assertEquals("لكل", filter.next().termText());
		assertEquals("شيء", filter.next().termText());
		assertEquals("وهدي", filter.next().termText());
		assertEquals("ورحمة", filter.next().termText());
		assertEquals("وبشري", filter.next().termText());
		assertEquals("للمسلمين", filter.next().termText());
	}

	public void testFilterNext2() throws Exception {
		TokenStream stream = new WhitespaceTokenizer(new StringReader(ARABIC_STR2));
		ArabicFilter filter = new ArabicFilter(stream);
		assertEquals("واذا", filter.next().termText());
		assertEquals("سالك", filter.next().termText());
		assertEquals("عبادي", filter.next().termText());
		assertEquals("عني", filter.next().termText());
		assertEquals("فاني", filter.next().termText());
		assertEquals("قريب", filter.next().termText());
		assertEquals("اجيب", filter.next().termText());
		assertEquals("دعوة", filter.next().termText());
		assertEquals("الداع", filter.next().termText());
		assertEquals("اذا", filter.next().termText());
		assertEquals("دعان", filter.next().termText());
		assertEquals("فليستجيبوا", filter.next().termText());
		assertEquals("لي", filter.next().termText());
		assertEquals("وليومنوا", filter.next().termText());
		assertEquals("بي", filter.next().termText());
		assertEquals("لعلهم", filter.next().termText());
		assertEquals("يرشدون", filter.next().termText());
	}
}
