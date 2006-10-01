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
import net.sf.zekr.common.resource.RangedQuranTextTest;
import net.sf.zekr.engine.search.SearchScopeItemTest;
import net.sf.zekr.engine.search.SearchScopeTest;

public class AllTests extends TestCase {

	public static Test suite() {
		final TestSuite suite = new TestSuite();
		suite.addTest(new TestSuite(RangedQuranTextTest.class));
		suite.addTest(new TestSuite(SearchScopeItemTest.class));
		suite.addTest(new TestSuite(SearchScopeTest.class));
		return suite;
	}

}
