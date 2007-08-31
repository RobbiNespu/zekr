/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

import net.sf.zekr.common.ZekrBaseException;

/**
 * General exceptions related to HTTP server and it's utility functions.
 * 
 * @author Mohsen Saboorian
 */
public class HttpServerException extends ZekrBaseException {
	private static final long serialVersionUID = -3683201698501229666L;

	public HttpServerException() {
		super();
	}

	public HttpServerException(Throwable cause) {
		super(cause);
	}

}
