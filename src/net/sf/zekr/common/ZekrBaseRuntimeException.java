/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.common;

/**
 * Zekr runtime exceptions should extend this class.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class ZekrBaseRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 5681293967162130227L;

	public ZekrBaseRuntimeException() {
		super();
	}

	public ZekrBaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZekrBaseRuntimeException(String message) {
		super(message);
	}

	public ZekrBaseRuntimeException(Throwable cause) {
		super(cause);
	}
}
