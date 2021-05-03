/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 31, 2008
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.ZekrBaseRuntimeException;

/**
 * Super-class for all search-related exception.
 * 
 * @author Mohsen Saboorian
 */
public class SearchException extends ZekrBaseRuntimeException {
	private static final long serialVersionUID = -1334796009811764981L;

	public SearchException() {
		super();
	}

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchException(String message) {
		super(message);
	}

	public SearchException(Throwable cause) {
		super(cause);
	}

}
