/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.common.resource;

import java.io.IOException;

import net.sf.zekr.ZekrBaseTest;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.SearchScopeItem;

public class RangedQuranTextTest extends ZekrBaseTest {
	RangedQuranText rqt;

	public RangedQuranTextTest() throws Exception {
		super();
	}

	/*
	 * Test method for 'net.sf.zekr.common.resource.RangedQuranText.init()'
	 */
	public void testInit() throws Exception {
		SearchScope ss = new SearchScope();
		rqt = new RangedQuranText(QuranText.getSimpleTextInstance(), ss);
		rqt.init();
		assertNull(rqt.getCurrentLocation());

		// test the number of matched aya with no SearchScopeItem set.
		assertFalse(rqt.findNext());
	}

	/*
	 * Test method for 'net.sf.zekr.common.resource.RangedQuranText.reset()'
	 */
	public void testReset() throws Exception {
		SearchScope ss = new SearchScope();
		ss.add(new SearchScopeItem(1, 1, 114, 6, false));
		rqt = new RangedQuranText(QuranText.getSimpleTextInstance(), ss);
		rqt.findNext();
		assertFalse(rqt.getCurrentLocation().equals(new QuranLocation(1, 1)));
		rqt.reset();
		assertTrue(rqt.getCurrentLocation().equals(new QuranLocation(1, 1)));
	}

	/*
	 * Test method for 'net.sf.zekr.common.resource.RangedQuranText.findNext()'
	 */
	public void testFindNext() throws Exception {
		SearchScope ss = new SearchScope();
		ss.add(new SearchScopeItem(1, 1, 2, 1, true));
		ss.add(new SearchScopeItem(4, 1, 8, 1, true));
		rqt = new RangedQuranText(QuranText.getSimpleTextInstance(), ss);
	}

}
