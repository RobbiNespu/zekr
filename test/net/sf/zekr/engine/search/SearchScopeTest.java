/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.ZekrBaseTest;

public class SearchScopeTest extends ZekrBaseTest {
	private SearchScope ss;

	public SearchScopeTest() throws Exception {
		super();
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScope.add(SearchScopeItem)'
	 */
	public void testAdd() {
		ss = new SearchScope();
		ss.add(new SearchScopeItem());
		assertEquals(ss.getScopeItems().size(), 1);
		ss.add(new SearchScopeItem());
		assertEquals(ss.getScopeItems().size(), 1);
		// search scope underling container acts as a set (no duplicate elements)
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScope.getScopeItems()'
	 */
	public void testGetScopeItems() {
		ss = new SearchScope();
		ss.add(new SearchScopeItem());
		ss.add(new SearchScopeItem(1, 2, 3, 4, false));
		ss.add(new SearchScopeItem(1, 2, 3, 4, true));
		assertEquals(ss.getScopeItems().size(), 3);
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScope.includes(IQuranLocation)'
	 */
	public void testIncludesIQuranLocation() {
		ss = new SearchScope();
		assertFalse(ss.includes(1, 1));
		
		ss.add(new SearchScopeItem(1, 2, 1, 2, true));
		ss.add(new SearchScopeItem(1, 3, 114, 4, false));
		assertTrue(ss.includes(1, 3));
		assertFalse(ss.includes(1, 2));
		assertFalse(ss.includes(1, 1));
		assertTrue(ss.includes(114, 4));

		ss = new SearchScope();
		ss.add(new SearchScopeItem(1, 2, 13, 4, false));
		ss.add(new SearchScopeItem(5, 6, 7, 8, true));
		assertTrue(ss.includes(1, 4));
		assertFalse(ss.includes(114, 4));
		assertTrue(ss.includes(2, 9));
		assertFalse(ss.includes(5, 9));
		assertFalse(ss.includes(7, 1));
		assertTrue(ss.includes(7, 9));
	}

}
