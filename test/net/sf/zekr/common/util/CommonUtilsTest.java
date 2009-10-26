/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 28, 2008
 */
package net.sf.zekr.common.util;

import net.sf.zekr.ZekrBaseTest;

public class CommonUtilsTest extends ZekrBaseTest {
	public CommonUtilsTest() throws Exception {
		super();
	}

	public void testCompareVersions() throws Exception {
		assertTrue(CommonUtils.compareVersions("0.3.5", "0.3.1") > 0);
		assertTrue(CommonUtils.compareVersions("0.3.5", "0.3.11") < 0);
		assertTrue(CommonUtils.compareVersions("1.0.0", "0.99.99") > 0);
		assertTrue(CommonUtils.compareVersions("2.0.1", "2.0.01") == 0);
	}
}
