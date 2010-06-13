/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.zekr.common.resource.QuranLocationTest;
import net.sf.zekr.common.resource.QuranPropertiesUtilsTest;
import net.sf.zekr.common.resource.RangedQuranTextTest;
import net.sf.zekr.common.util.CommonUtilsTest;
import net.sf.zekr.engine.audio.NamedBufferedInputStreamTest;
import net.sf.zekr.engine.audio.PlayableObjectTest;
import net.sf.zekr.engine.page.CustomPagingDataTest;
import net.sf.zekr.engine.page.FixedAyaPagingDataTest;
import net.sf.zekr.engine.page.HizbQuarterPagingDataTest;
import net.sf.zekr.engine.page.JuzPagingDataTest;
import net.sf.zekr.engine.page.SuraPagingDataTest;
import net.sf.zekr.engine.search.SearchScopeItemTest;
import net.sf.zekr.engine.search.SearchScopeTest;
import net.sf.zekr.engine.search.lucene.ZekrLuceneAnalyzerTest;
import net.sf.zekr.engine.search.root.RootHighlighterTest;
import net.sf.zekr.engine.server.DefaultHttpServerTest;
import net.sf.zekr.ui.helper.FormUtilsTest;

public class AllTests extends TestCase {

	public static Test suite() {
		final TestSuite suite = new TestSuite();
		suite.addTest(new TestSuite(RangedQuranTextTest.class));
		suite.addTest(new TestSuite(SearchScopeItemTest.class));
		suite.addTest(new TestSuite(SearchScopeTest.class));

		suite.addTest(new TestSuite(ZekrLuceneAnalyzerTest.class));
		suite.addTest(new TestSuite(RootHighlighterTest.class));

		// suite.addTest(new TestSuite(MiscTests.class));
		suite.addTest(new TestSuite(FormUtilsTest.class));
		suite.addTest(new TestSuite(QuranPropertiesUtilsTest.class));
		suite.addTest(new TestSuite(QuranLocationTest.class));

		// http server test
		suite.addTest(new TestSuite(DefaultHttpServerTest.class));

		// paging data test
		suite.addTest(new TestSuite(FixedAyaPagingDataTest.class));
		suite.addTest(new TestSuite(JuzPagingDataTest.class));
		suite.addTest(new TestSuite(HizbQuarterPagingDataTest.class));
		suite.addTest(new TestSuite(SuraPagingDataTest.class));
		suite.addTest(new TestSuite(CustomPagingDataTest.class));

		suite.addTest(new TestSuite(CommonUtilsTest.class));

		suite.addTest(new TestSuite(PlayableObjectTest.class));
		suite.addTest(new TestSuite(NamedBufferedInputStreamTest.class));

		return suite;
	}
}
