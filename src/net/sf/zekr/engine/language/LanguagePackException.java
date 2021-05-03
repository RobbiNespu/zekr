/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     4/02/2005
 */

package net.sf.zekr.engine.language;

import net.sf.zekr.common.ZekrBaseRuntimeException;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class LanguagePackException extends ZekrBaseRuntimeException {
	private static final long serialVersionUID = -7968091247753668046L;

	public LanguagePackException() {
		super();
	}

	public LanguagePackException(String msg) {
		super(msg);
	}

}
