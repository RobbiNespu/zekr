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

public class SearchScopeItemTest extends ZekrBaseTest {

	private SearchScopeItem ssi;
	public SearchScopeItemTest() throws Exception {
		super();
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScopeItem.SearchScopeItem()'
	 */
	public void testSearchScopeItem() {
		assertNotNull(new SearchScopeItem());
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScopeItem.SearchScopeItem(int, int, int, int,
	 * boolean)'
	 */
	public void testSearchScopeItemIntIntIntIntBoolean() {
		try {
			ssi = new SearchScopeItem(1, 4, 1, 2, false);
			fail("IllegalSearchScopeItemException shall be thrown");
		} catch (IllegalSearchScopeItemException e) {
			// ok
		}

		try {
			ssi = new SearchScopeItem(1, 4, 1, 4, false);
		} catch (IllegalSearchScopeItemException e) {
			fail("IllegalSearchScopeItemException shall not be thrown");
		}

		ssi = new SearchScopeItem(7, 3, 9, 10, false);
		assertEquals(ssi.getSuraFrom(), 7);
		assertEquals(ssi.getAyaFrom(), 3);
		assertEquals(ssi.getSuraTo(), 9);
		assertEquals(ssi.getAyaTo(), 10);
		assertFalse(ssi.isExclusive());
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScopeItem.equals(Object)'
	 */
	public void testEqualsObject() {
		assertEquals(new SearchScopeItem(), new SearchScopeItem(1, 1, 1, 1, false));
		assertFalse(new SearchScopeItem().equals(new SearchScopeItem(1, 1, 1, 1, true)));
	}

	/*
	 * Test method for 'net.sf.zekr.engine.search.SearchScopeItem.includes(int, int)'
	 */
	public void testIncludes() {
		ssi = new SearchScopeItem(3, 4, 5, 6, true);
		assertFalse(ssi.includes(1, 2));
		assertFalse(ssi.includes(5, 6));
		assertFalse(ssi.includes(3, 4));

		ssi = new SearchScopeItem(3, 4, 5, 6, false);
		assertFalse(ssi.includes(1, 2));
		assertTrue(ssi.includes(5, 6));
		assertTrue(ssi.includes(3, 4));
	}

}
