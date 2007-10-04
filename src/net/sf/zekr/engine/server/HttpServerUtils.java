/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 3, 2007
 */
package net.sf.zekr.engine.server;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.UriUtils;

/**
 * @author Mohsen Saboorian
 */
public class HttpServerUtils {
	public static String getUrl(String relativePath) {
		if (ApplicationConfig.getInstance().isHttpServerEnabled())
			return HttpServer.getServer().getUrl() + relativePath;
		return UriUtils.toUri(relativePath);
	}
}
