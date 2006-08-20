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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import com.sun.jndi.toolkit.url.UrlUtil;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLHelper;

public class UriUtils {

	/**
	 * @param file
	 * @return file:///path_to_file (replacing special characters with URLEncoding
	 *         equivalents)
	 */
	public static String toURI(File file) {
		String uri = null;
		uri = file.toURI().toString();

		// Fix IE 7.0 and JRE incompatibility
		if (!uri.startsWith("file://")) {
			uri = new String("file:///" + uri.substring(uri.indexOf("file:/") + 6));
		}
		return uri;
	}

	public static String toURI(String path) {
		return toURI(new File(path));
	}

	public static File toFile(String url) throws URISyntaxException {
		return new File(new URI(url).getPath());
	}

}
