/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 11, 2010
 */
package net.sf.zekr.common.util;

/**
 * @author Mohsen Saboorian
 */
public class ExceptionsUtils {

	public static void preventNullParameter(Object obj, String paramName) throws NullPointerException {
		if (obj == null) {
			throw new NullPointerException(String.format("Parameter '%s' cannot be null", paramName));
		}
	}

}
