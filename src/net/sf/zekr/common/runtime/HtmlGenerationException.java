/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 21, 2007
 */
package net.sf.zekr.common.runtime;

import net.sf.zekr.common.ZekrBaseException;

public class HtmlGenerationException extends ZekrBaseException {
	private static final long serialVersionUID = -8995727758942094752L;

	public HtmlGenerationException(Throwable th) {
		super(th);
	}
}
