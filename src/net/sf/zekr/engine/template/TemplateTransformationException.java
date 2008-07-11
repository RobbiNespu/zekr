/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 21, 2007
 */
package net.sf.zekr.engine.template;

import net.sf.zekr.common.ZekrBaseException;

/**
 * This exception is thrown if any template transformation exception encountered.
 * 
 * @author Mohsen Saboorian
 */
public class TemplateTransformationException extends ZekrBaseException {
	private static final long serialVersionUID = 4306422359147802898L;

	public TemplateTransformationException(Throwable th) {
		super(th);
	}
}
