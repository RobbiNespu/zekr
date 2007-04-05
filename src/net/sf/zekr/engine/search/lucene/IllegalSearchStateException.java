/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 1, 2007
 */
package net.sf.zekr.engine.search.lucene;

public class IllegalSearchStateException extends RuntimeException {
	private static final long serialVersionUID = 7631403367185637684L;

	public IllegalSearchStateException(String message) {
		super(message);
	}
}
