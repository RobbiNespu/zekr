/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 8, 2008
 */
package net.sf.zekr.common;

public class ZekrMessageException extends ZekrBaseException {
	private static final long serialVersionUID = -5226474932286938582L;
	private String[] params = new String[0];

	public ZekrMessageException() {
		super();
	}

	public ZekrMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZekrMessageException(String message) {
		super(message);
	}

	public ZekrMessageException(Throwable cause) {
		super(cause);
	}

	public ZekrMessageException(String messageKey, String[] params) {
		super(messageKey);
		this.params = params;
	}

	public String[] getParams() {
		return params;
	}
}
