/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 8, 2008
 */
package net.sf.zekr.engine.revelation;

import net.sf.zekr.common.ZekrMessageException;

public class RevelationException extends ZekrMessageException {
	private static final long serialVersionUID = -2012664294381396315L;

	public RevelationException() {
		super();
	}

	public RevelationException(String messageKey, String[] params) {
		super(messageKey, params);
	}

	public RevelationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RevelationException(String message) {
		super(message);
	}

	public RevelationException(Throwable cause) {
		super(cause);
	}

}
