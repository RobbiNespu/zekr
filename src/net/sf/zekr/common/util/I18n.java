/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 20, 2006
 */
package net.sf.zekr.common.util;

import java.io.UnsupportedEncodingException;

public class I18n {
	public static String decodeCp1256(String str) throws UnsupportedEncodingException {
		return new String(str.getBytes(), "Cp1256");
	}
}
