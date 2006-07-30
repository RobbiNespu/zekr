/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 28, 2006
 */
package net.sf.zekr.common.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import com.sun.jndi.toolkit.url.UrlUtil;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLHelper;

public class UrlUtils {

	public static String toURI(File file) {
		try {
			return "file://" + URLEncoder.encode(file.getAbsolutePath(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toURI(String path) {
		return toURI(new File(path));
	}

}
