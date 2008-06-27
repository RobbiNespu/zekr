/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 22, 2008
 */
package net.sf.zekr.common.resource;

import net.sf.zekr.ZekrBaseTest;

public class QuranLocationTest extends ZekrBaseTest {

	public QuranLocationTest() throws Exception {
		super();
	}

	public void testIsValidLocationIntInt() {
		assertTrue(QuranLocation.isValidLocation(2, 268));
		assertFalse(QuranLocation.isValidLocation(114, 7));
	}

	public void testGetSuraName() {
		// TODO: check for i18n of sura names
	}

	public void testGetNext() {
		QuranLocation ql1 = new QuranLocation(1, 7);
		QuranLocation ql2 = new QuranLocation(2, 1);
		assertEquals(ql1.getNext(), ql2);

		ql1 = new QuranLocation(5, 5);
		ql2 = new QuranLocation(5, 8);
		assertEquals(ql1.getNext().getNext().getNext(), ql2);
	}

	public void testGetPrev() {
		QuranLocation ql1 = new QuranLocation(1, 7);
		QuranLocation ql2 = new QuranLocation(2, 1);
		assertEquals(ql1, ql2.getPrev());

		ql1 = new QuranLocation(5, 8);
		ql2 = new QuranLocation(5, 5);
		assertEquals(ql1.getPrev().getPrev().getPrev(), ql2);
	}

	public void testCompareTo() {
		QuranLocation ql1 = new QuranLocation(1, 7);
		QuranLocation ql2 = new QuranLocation(2, 1);
		assertTrue(ql1.compareTo(ql2) < 0);

		ql1 = new QuranLocation(5, 8);
		ql2 = new QuranLocation(5, 4);
		assertTrue(ql1.compareTo(ql2) > 0);

		ql1 = new QuranLocation(113, 1);
		ql2 = new QuranLocation(113, 1);
		assertTrue(ql1.compareTo(ql2) == 0);
	}

	public void testGetAbsoluteAya() {
		assertTrue(new QuranLocation(2, 1).getAbsoluteAya() == 8);
		assertTrue(new QuranLocation(3, 1).getAbsoluteAya() == 294);
		assertTrue(new QuranLocation(1, 3).getAbsoluteAya() == 3);
	}
}
