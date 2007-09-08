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
		int k = QuranPropertiesUtils.getAggregativeAyaCount(3);
		assertEquals(k, 293);
		k += qp.getSura(3).getAyaCount() + qp.getSura(4).getAyaCount();
		assertEquals(QuranPropertiesUtils.getAggregativeAyaCount(5), k);

		for (int i = 5; i < 114; i++) {
			k += qp.getSura(i).getAyaCount();
		}
		assertEquals(QuranPropertiesUtils.getAggregativeAyaCount(114), k);
	}
}
