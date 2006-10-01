/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.common;

public class ZekrBaseException extends Exception {
	private static final long serialVersionUID = -1507511837671369953L;

	protected ZekrBaseException() {
		super();
	}

	protected ZekrBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ZekrBaseException(String message) {
		super(message);
	}

	protected ZekrBaseException(Throwable cause) {
		super(cause);
	}
}
