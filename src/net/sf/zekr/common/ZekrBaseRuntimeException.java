/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.common;

public class ZekrBaseRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 5681293967162130227L;

	protected ZekrBaseRuntimeException() {
		super();
	}

	protected ZekrBaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ZekrBaseRuntimeException(String message) {
		super(message);
	}

	protected ZekrBaseRuntimeException(Throwable cause) {
		super(cause);
	}
}
