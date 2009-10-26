/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 26, 2009
 */
package net.sf.zekr.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 */
public class CommonUtils {
	public static final Runnable EMPTY_RUNNABLE = new Runnable() {
		public void run() {
		}
	};

	/**
	 * The two parameters should be exactly of type x.y.z, in which all x and y and z are integer numbers: 0-9.
	 * 
	 * @param ver1
	 * @param ver2
	 * @return
	 */
	public static int compareVersions(String ver1, String ver2) {
		String[] v1 = StringUtils.split(ver1, ".");
		String[] v2 = StringUtils.split(ver2, ".");
		int k = Integer.parseInt(v1[0]) - Integer.parseInt(v2[0]);
		if (k != 0) {
			return k;
		}
		k = Integer.parseInt(v1[1]) - Integer.parseInt(v2[1]);
		if (k != 0) {
			return k;
		}
		k = Integer.parseInt(v1[2]) - Integer.parseInt(v2[2]);
		return k;
	}
}
