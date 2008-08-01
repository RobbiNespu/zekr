/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.common.util;

/**
 * @author Mohsen Saboorian
 */
public class StringUtils {
	public static class Region {
		public int from;
		public int to;

		public Region(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}

	public static Region getNthRegion(String src, int n, char delim) {
		int f = -1, t = 0;
		int i;
		for (i = 0; i < n - 1; i++) {
			f = src.indexOf(delim, f + 1);
		}
		if (f == -1 && n > 1)
			return null;
		t = src.indexOf(delim, f + 1);
		if (t == -1)
			return new Region(f + 1, src.length());
		else
			return new Region(f + 1, t);
	}

	public static String getNthWord(String src, int n, char delim) {
		Region r = getNthRegion(src, n, delim);
		return r == null ? null : src.substring(r.from, r.to);
	}

	public static void main(String[] args) {
		final String STR = "This page was last modified s";

		System.out.println(StringUtils.getNthWord(STR, 1, ' '));
		System.out.println(StringUtils.getNthWord(STR, 3, ' '));
		System.out.println(StringUtils.getNthWord(STR, 6, ' '));
		System.out.println(StringUtils.getNthWord(STR, 7, ' '));
	}
}
