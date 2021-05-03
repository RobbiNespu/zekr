/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.engine.page;

import net.sf.zekr.common.ZekrBaseException;

public class PagingException extends ZekrBaseException {
	private static final long serialVersionUID = 1202125761228038879L;

	public PagingException() {
		super();
	}

	public PagingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PagingException(String message) {
		super(message);
	}

	public PagingException(Throwable cause) {
		super(cause);
	}
}
