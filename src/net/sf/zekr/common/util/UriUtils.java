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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Simple URI utility methods.
 * 
 * @author Mohsen Saboorian
 */
public class UriUtils {
	/**
	 * @param file
	 *           thr source file to return its URI.
	 * @return file:///path_to_file (replacing special characters with URLEncoding equivalents)
	 */
	public static String toUri(File file) {
		String uri = null;
//		if (ApplicationConfig.getInstance().isHttpServerEnabled()) {
//			String addr;
//			try {
//				addr = HttpServer.getServer().getUrl();
//			} catch (HttpServerRuntimeException e) {
//				addr = "/";
//			}
//			uri = addr + file.getPath().replace('\\', '/');
//		} else {
			uri = file.toURI().toString();

			// Fix IE 7.0 and JRE incompatibility
			if (!uri.startsWith("file://")) {
				uri = new String("file:///" + uri.substring(uri.indexOf("file:/") + 6));
			}
//		}
		return uri;
	}

	public static String toUri(String path) {
		return toUri(new File(path));
	}

	public static File toFile(String url) throws URISyntaxException {
		return new File(new URI(url).getPath());
	}

	public static String toHttpUrl(String addr, int port) throws MalformedURLException {
		return new URL("http", addr, port, "/").toString();
	}

}
