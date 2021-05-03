/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 29, 2007
 */
package net.sf.zekr.engine.bookmark;

import net.sf.zekr.common.ZekrBaseException;

public class BookmarkException extends ZekrBaseException {
	private static final long serialVersionUID = 2599975838413678091L;

	public BookmarkException(String string) {
		super(string);
	}
}