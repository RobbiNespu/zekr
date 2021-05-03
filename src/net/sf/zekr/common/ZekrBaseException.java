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
 * All non-runtime Zekr exceptions should extend this class.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrBaseException extends Exception {
	private static final long serialVersionUID = -1507511837671369953L;

	public ZekrBaseException() {
		super();
	}

	public ZekrBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZekrBaseException(String message) {
		super(message);
	}

	public ZekrBaseException(Throwable cause) {
		super(cause);
	}
}
