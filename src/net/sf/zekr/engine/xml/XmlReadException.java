/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 22, 2006
 */
package net.sf.zekr.engine.xml;

public class XmlReadException extends Exception {
	private static final long serialVersionUID = -3900273272033548514L;

	protected XmlReadException() {
		super();
	}

	protected XmlReadException(String message, Throwable cause) {
		super(message, cause);
	}

	protected XmlReadException(String message) {
		super(message);
	}

	protected XmlReadException(Throwable cause) {
		super(cause);
	}

}
