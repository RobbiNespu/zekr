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
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.QuranLocation;

public class CustomPagingDataTest extends ZekrBaseTest {
	private static final String UTHMAN_TAHA = "uthmani";
	IPagingData cpd;

	public CustomPagingDataTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		cpd = ApplicationConfig.getInstance().getQuranPaging().get(UTHMAN_TAHA);
		cpd.load();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testHizbQuadPagingData() {
		assertNotNull(cpd);
	}

	public final void testGetName() {
		assertNotNull(cpd.getName());
	}

	public final void testGetQuranPage() {
		assertEquals(cpd.getQuranPage(10).getFrom(), new QuranLocation(2, 62));
		assertEquals(cpd.getQuranPage(10).getTo(), new QuranLocation(2, 69));

		assertEquals(cpd.getQuranPage(100).getFrom(), new QuranLocation(4, 135));
		assertEquals(cpd.getQuranPage(100).getTo(), new QuranLocation(4, 140));

		assertEquals(cpd.getQuranPage(250).getFrom(), new QuranLocation(13, 6));
		assertEquals(cpd.getQuranPage(250).getTo(), new QuranLocation(13, 13));
	}

	public final void testGetId() {
		assertEquals(cpd.getId(), UTHMAN_TAHA);
	}

	public final void testSize() {
		assertEquals(cpd.size(), 604);
	}

	public final void testGetPageNum() {
		FixedAyaPagingData cpd = new FixedAyaPagingData(7);
		assertEquals(1, cpd.getQuranPage(1).getPageNum());
		assertEquals(12, cpd.getQuranPage(12).getPageNum());
		assertEquals(31, cpd.getQuranPage(31).getPageNum());
		assertEquals(55, cpd.getQuranPage(55).getPageNum());
		assertEquals(145, cpd.getQuranPage(145).getPageNum());
	}

	public final void testGetContainerPage() {
		assertEquals(cpd.getContainerPage(new QuranLocation(2, 1)), cpd.getQuranPage(2));
		assertEquals(cpd.getContainerPage(new QuranLocation(5, 96)), cpd.getQuranPage(124));
		assertEquals(cpd.getContainerPage(new QuranLocation(14, 2)), cpd.getQuranPage(255));
		assertEquals(cpd.getContainerPage(new QuranLocation(36, 70)), cpd.getQuranPage(444));
	}

}
