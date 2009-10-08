/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import java.io.IOException;

import net.sf.zekr.common.ZekrBaseException;

/**
 * Exception thrown when error occurs in playlist provision. Naturally an {@link IOException} in creating the
 * file on the local disk can cause this exception.
 * 
 * @author Mohsen Saboorian
 */
public class PlaylistProvisionException extends ZekrBaseException {
	private static final long serialVersionUID = 7785842186957678833L;

	public PlaylistProvisionException() {
		super();
	}

	public PlaylistProvisionException(Throwable cause) {
		super(cause);
	}
}
