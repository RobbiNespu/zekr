/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.engine.search;

import net.sf.zekr.common.ZekrBaseRuntimeException;

public class IllegalSearchScopeItemException extends ZekrBaseRuntimeException {
	private static final long serialVersionUID = 2901617556801390701L;

	protected IllegalSearchScopeItemException() {
		super();
	}

	protected IllegalSearchScopeItemException(String message) {
		super(message);
	}
}
