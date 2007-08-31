/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

import net.sf.zekr.common.util.UriUtils;

/**
 * An abstract HTTP server model. An HTTP server implementation can be accessed through <code>getInstance()</code>.
 * 
 * @author Mohsen Saboorian
 */
public abstract class HttpServer implements Runnable {
	protected HttpServer() {
	}

	public static HttpServer getInstance() {
		return new DefaultHttpServer();
	}

	/**
	 * @return port on which HTTP server is listening for input connections.
	 * @throws HttpServerException
	 */
	abstract public int getPort() throws HttpServerException;

	/**
	 * @return canonical (fully qualified name) HTTP server address. Examples are <tt>"localhost"</tt> and
	 *         <tt>"zekr.org"</tt>.
	 * @throws HttpServerException
	 */
	abstract public String getCanonicalAddress() throws HttpServerException;

	/**
	 * @return HTTP server URL with a trailing slash. Examples are: <tt>"http://localhost:8920/"</tt> and
	 *         <tt>"http://zekr:80/"</tt>.
	 * @throws HttpServerException
	 */
	public String getUrl() throws HttpServerException {
		try {
			return UriUtils.toHttpUrl(getCanonicalAddress(), getPort());
		} catch (Exception e) {
			throw new HttpServerException(e);
		}
	}
}
