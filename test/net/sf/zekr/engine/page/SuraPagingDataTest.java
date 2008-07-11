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
import net.sf.zekr.common.resource.QuranPropertiesUtils;

public class SuraPagingDataTest extends ZekrBaseTest {
	SuraPagingData spd;

	public SuraPagingDataTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		spd = new SuraPagingData();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testSuraPagingData() {
		assertNotNull(spd);
	}

	public final void testGetName() {
		assertNotNull(spd.getName());
	}

	public final void testGetPageList() {
		IQuranPage p19 = (IQuranPage) spd.getPageList().get(18);
		IQuranPage p20 = (IQuranPage) spd.getPageList().get(19);
		int p19i = QuranPropertiesUtils.getAbsoluteLocation(p19.getFrom());
		int p20i = QuranPropertiesUtils.getAbsoluteLocation(p20.getFrom());
		assertEquals(QuranPropertiesUtils.getSura(19).getAyaCount(), p20i - p19i);
	}

	public final void testGetQuranPage() {
		assertEquals(spd.getQuranPage(10).getFrom(), new QuranLocation(10, 1));
		assertEquals(spd.getQuranPage(55).getFrom().getPrev(), new QuranLocation(55, 1).getPrev());
	}

	public final void testGetId() {
		assertEquals(spd.getId(), SuraPagingData.ID);
	}

	public final void testSize() {
		assertEquals(spd.size(), QuranPropertiesUtils.QURAN_SURA_COUNT);
	}

	public final void testGetPageNum() {
		FixedAyaPagingData spd = new FixedAyaPagingData(7);
		assertEquals(1, spd.getQuranPage(1).getPageNum());
		assertEquals(12, spd.getQuranPage(12).getPageNum());
		assertEquals(31, spd.getQuranPage(31).getPageNum());
		assertEquals(55, spd.getQuranPage(55).getPageNum());
		assertEquals(145, spd.getQuranPage(145).getPageNum());
	}

	public final void testGetContainerPage() {
		assertEquals(spd.getContainerPage(new QuranLocation(1, 1)), spd.getQuranPage(1));
		assertEquals(spd.getContainerPage(new QuranLocation(2, 280)), spd.getQuranPage(2));
		assertEquals(spd.getContainerPage(new QuranLocation(3, 93)), spd.getQuranPage(3));
		assertEquals(spd.getContainerPage(new QuranLocation(100, 1)), spd.getQuranPage(100));
	}

}
