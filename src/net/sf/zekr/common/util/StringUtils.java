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

		System.out.println(abbreviate(STR, 12));
		System.out.println(abbreviate(STR, 0));
		System.out.println(abbreviate(STR, 2));
		System.out.println(abbreviate(STR, 40));
		System.out.println(abbreviate("a b", 30));
	}

	/**
	 * Similar to {@link org.apache.commons.lang.StringUtils#abbreviate(String, int)} method but adds no
	 * ellipsis to the end of the abbreviated string. This method also abbreviates to the nearest space
	 * character (\x20), so that the result string size is smaller or equal to size parameter.<br>
	 * This method returns an empty string if no space character found within the given size range.
	 * 
	 * @param str string to be abbreviated
	 * @param size boundary to which the result size of abbreviated string is less or equal
	 * @return abbreviated string
	 */
	public static String abbreviate(String str, int size) {
		if (str.length() <= size)
			return str;
		int index = str.lastIndexOf(' ', size);
		if (index <= -1)
			return "";
		return str.substring(0, index);
	}
}
