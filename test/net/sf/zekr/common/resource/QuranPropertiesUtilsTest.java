/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 6, 2007
 */
package net.sf.zekr.common.resource;

import junit.framework.TestCase;

public class QuranPropertiesUtilsTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAggregativeAyaCount() {
		QuranProperties qp = QuranProperties.getInstance();
		int k = QuranPropertiesUtils.getAggregateAyaCount(3);
		assertEquals(k, 293);
		k += qp.getSura(3).getAyaCount() + qp.getSura(4).getAyaCount();
		assertEquals(QuranPropertiesUtils.getAggregateAyaCount(5), k);

		for (int i = 5; i < 114; i++) {
			k += qp.getSura(i).getAyaCount();
		}
		assertEquals(QuranPropertiesUtils.getAggregateAyaCount(114), k);
	}

	public void testGetJuzOf() throws Exception {
		assertEquals(QuranPropertiesUtils.getJuzOf(1, 1).getIndex(), 1);
		assertEquals(QuranPropertiesUtils.getJuzOf(2, 10).getIndex(), 1);
		assertEquals(QuranPropertiesUtils.getJuzOf(2, 200).getIndex(), 2);
		assertEquals(QuranPropertiesUtils.getJuzOf(3, 1).getIndex(), 3);
		assertEquals(QuranPropertiesUtils.getJuzOf(3, 200).getIndex(), 4);
		assertEquals(QuranPropertiesUtils.getJuzOf(17, 17).getIndex(), 15);
		assertEquals(QuranPropertiesUtils.getJuzOf(41, 46).getIndex(), 24);
		assertEquals(QuranPropertiesUtils.getJuzOf(41, 47).getIndex(), 25);
		assertEquals(QuranPropertiesUtils.getJuzOf(114, 1).getIndex(), 30);
	}

	public void testGetHizbQuadIndex() throws Exception {
		assertEquals(QuranPropertiesUtils.getHizbQuadIndex(1, 1), 0);
		assertEquals(QuranPropertiesUtils.getHizbQuadIndex(2, 25), 0);
		assertEquals(QuranPropertiesUtils.getHizbQuadIndex(2, 26), 1);
		assertEquals(QuranPropertiesUtils.getHizbQuadIndex(13, 35), 5);
		assertEquals(QuranPropertiesUtils.getHizbQuadIndex(114, 1), 7);
	}
}
