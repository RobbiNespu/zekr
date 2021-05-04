/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 11, 2008
 */
package net.sf.zekr.engine.server;

import org.apache.commons.configuration2.PropertiesConfiguration;

/**
 * An http server should be created through HttpServerFactory, not HttpServer abstract class.
 * 
 * @author Mohsen Saboorian
 */
public class HttpServerFactory {
	public static HttpServer createHttpServer(PropertiesConfiguration props) {
		return new DefaultHttpServer(props);
	}
}
