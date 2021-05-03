/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 8, 2008
 */
package net.sf.zekr.engine.translation;

import net.sf.zekr.common.ZekrMessageException;

public class TranslationException extends ZekrMessageException {
	private static final long serialVersionUID = -2012664294381396315L;

	public TranslationException() {
		super();
	}

	public TranslationException(String messageKey, String[] params) {
		super(messageKey, params);
	}

	public TranslationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TranslationException(String message) {
		super(message);
	}

	public TranslationException(Throwable cause) {
		super(cause);
	}

}
