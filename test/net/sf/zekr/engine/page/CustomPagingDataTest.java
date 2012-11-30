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
	private static final String HINDI_NASKH = "hindi-naskh";
	IPagingData cpd1;
	IPagingData cpd2;

	public CustomPagingDataTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		cpd1 = ApplicationConfig.getInstance().getQuranPaging().get(UTHMAN_TAHA);
		cpd2 = ApplicationConfig.getInstance().getQuranPaging().get(HINDI_NASKH);
		cpd1.load();
		cpd2.load();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testHizbQuadPagingData() {
		assertNotNull(cpd1);
		assertNotNull(cpd2);
	}

	public final void testGetName() {
		assertNotNull(cpd1.getName());
		assertNotNull(cpd2.getName());
	}

	public final void testGetQuranPage1() {
		assertEquals(cpd1.getQuranPage(10).getFrom(), new QuranLocation(2, 62));
		assertEquals(cpd1.getQuranPage(10).getTo(), new QuranLocation(2, 69));

		assertEquals(cpd1.getQuranPage(100).getFrom(), new QuranLocation(4, 135));
		assertEquals(cpd1.getQuranPage(100).getTo(), new QuranLocation(4, 140));

		assertEquals(cpd1.getQuranPage(250).getFrom(), new QuranLocation(13, 6));
		assertEquals(cpd1.getQuranPage(250).getTo(), new QuranLocation(13, 13));
	}

	public final void testGetQuranPage2() {
		assertEquals(cpd2.getQuranPage(1).getFrom(), new QuranLocation(1, 1));
		assertEquals(cpd2.getQuranPage(1).getTo(), new QuranLocation(1, 1));
		assertEquals(cpd2.getQuranPage(2).getFrom(), new QuranLocation(1, 1));
		assertEquals(cpd2.getQuranPage(2).getTo(), new QuranLocation(1, 7));
		assertEquals(cpd2.getQuranPage(3).getTo(), new QuranLocation(2, 4));

		// assertEquals(cpd2.getQuranPage(100).getFrom(), new QuranLocation(4, 135));
		// assertEquals(cpd2.getQuranPage(100).getTo(), new QuranLocation(4, 140));

		// assertEquals(cpd2.getQuranPage(250).getFrom(), new QuranLocation(13, 6));
		// assertEquals(cpd2.getQuranPage(250).getTo(), new QuranLocation(13, 13));
	}

	public final void testGetId() {
		assertEquals(cpd1.getId(), UTHMAN_TAHA);
		assertEquals(cpd2.getId(), HINDI_NASKH);
	}

	public final void testSize() {
		assertEquals(cpd1.size(), 604);
		// assertEquals(cpd1.size(), 604);
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
		assertEquals(cpd1.getContainerPage(new QuranLocation(2, 1)), cpd1.getQuranPage(2));
		assertEquals(cpd1.getContainerPage(new QuranLocation(5, 96)), cpd1.getQuranPage(124));
		assertEquals(cpd1.getContainerPage(new QuranLocation(14, 2)), cpd1.getQuranPage(255));
		assertEquals(cpd1.getContainerPage(new QuranLocation(36, 70)), cpd1.getQuranPage(444));
		assertEquals(cpd1.getContainerPage(new QuranLocation(114, 6)), cpd1.getQuranPage(604));
	}

}
