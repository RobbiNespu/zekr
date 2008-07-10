/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 28, 2008
 */
package net.sf.zekr.engine.page;

import net.sf.zekr.ZekrBaseTest;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.QuranLocation;

public class JuzPagingDataTest extends ZekrBaseTest {
	JuzPagingData jpd;

	public JuzPagingDataTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		jpd = new JuzPagingData();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testGetId() {
		assertEquals(jpd.getId(), JuzPagingData.ID);
	}

	public final void testJuzPagingData() {
		assertEquals("Quran has 30 juzs", jpd.getPageList().size(), 30);
		assertEquals(((IQuranPage) jpd.getPageList().get(0)).getFrom(), new QuranLocation(1, 1));
		assertEquals(((IQuranPage) jpd.getPageList().get(1)).getFrom(), new QuranLocation(2, 142));
		assertEquals(((IQuranPage) jpd.getPageList().get(29)).getTo(), new QuranLocation(114, 6));
	}

	public final void testGetContainerPage() {
		assertEquals(jpd.getContainerPage(new QuranLocation(1, 1)), jpd.getQuranPage(1));
		assertEquals(jpd.getContainerPage(new QuranLocation(2, 280)), jpd.getQuranPage(3));
		assertEquals(jpd.getContainerPage(new QuranLocation(3, 93)), jpd.getQuranPage(4));
		assertEquals(jpd.getContainerPage(new QuranLocation(100, 1)), jpd.getQuranPage(30));
	}
}
