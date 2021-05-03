/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 19, 2009
 */
package net.sf.zekr.engine.audio;

import net.sf.zekr.common.ZekrBaseRuntimeException;

/**
 * @author Mohsen Saboorian
 */
public class PlayerException extends ZekrBaseRuntimeException {
	private static final long serialVersionUID = 7181029194830684107L;

	public PlayerException() {
		super();
	}

	public PlayerException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerException(String message) {
		super(message);
	}

	public PlayerException(Throwable cause) {
		super(cause);
	}
}
