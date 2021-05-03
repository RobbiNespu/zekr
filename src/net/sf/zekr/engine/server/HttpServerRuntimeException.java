/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

import net.sf.zekr.common.ZekrBaseRuntimeException;

/**
 * General exceptions related to HTTP server and it's utility functions.
 * 
 * @author Mohsen Saboorian
 */
public class HttpServerRuntimeException extends ZekrBaseRuntimeException {
	private static final long serialVersionUID = 4255843475568392756L;

	public HttpServerRuntimeException() {
		super();
	}

	public HttpServerRuntimeException(Throwable cause) {
		super(cause);
	}

}
