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
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;

public class HizbQuadPagingDataTest extends ZekrBaseTest {
	HizbQuadPagingData h;

	public HizbQuadPagingDataTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		h = new HizbQuadPagingData();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testHizbQuadPagingData() {
		assertNotNull(h);
	}

	public final void testGetName() {
		assertNotNull(h.getName());
	}

	public final void testGetPageList() {
		IQuranPage p11 = (IQuranPage) h.getPageList().get(10);
		IQuranPage p12 = (IQuranPage) h.getPageList().get(11);
		JuzProperties j11 = QuranPropertiesUtils.getJuzOf(p11.getFrom());
		int p11hq = QuranPropertiesUtils.getHizbQuadIndex(p11.getFrom());
		QuranLocation hqBegin = j11.getHizbQuarters()[p11hq];
		assertEquals(hqBegin, p11.getFrom());

		QuranPropertiesUtils.getJuzOf(p12.getTo());
		JuzProperties j12 = QuranPropertiesUtils.getJuzOf(p12.getTo());
		int p12hq = QuranPropertiesUtils.getHizbQuadIndex(p12.getFrom());

		assertEquals(j11.getIndex() * 8 + p11hq + 1, j12.getIndex() * 8 + p12hq);
	}

	public final void testGetQuranPage() {
		assertEquals(h.getQuranPage(1).getTo(), new QuranLocation(2, 25));
	}

	public final void testGetId() {
		assertEquals(h.getId(), HizbQuadPagingData.ID);
	}

	public final void testSize() {
		assertEquals(h.size(), 30 * 8);
	}
	
	public final void testGetPageNum() {
		assertEquals(1, h.getQuranPage(1).getPageNum());
		assertEquals(12, h.getQuranPage(12).getPageNum());
		assertEquals(31, h.getQuranPage(31).getPageNum());
		assertEquals(55, h.getQuranPage(55).getPageNum());
	}

	public final void testGetContainerPage() {
		assertEquals(h.getContainerPage(new QuranLocation(1, 1)), h.getQuranPage(1));
		assertEquals(h.getContainerPage(new QuranLocation(2, 280)), h.getQuranPage(19));
		assertEquals(h.getContainerPage(new QuranLocation(3, 92)), h.getQuranPage(24));
		assertEquals(h.getContainerPage(new QuranLocation(3, 93)), h.getQuranPage(25));
	}

}
