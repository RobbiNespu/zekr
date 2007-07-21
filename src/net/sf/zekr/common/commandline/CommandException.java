/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 15, 2007
 */
package net.sf.zekr.common.commandline;

import net.sf.zekr.common.ZekrBaseException;

/**
 * Command line execution exception. A wrapper class for any exception occurred on execution of terminal commands.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CommandException extends ZekrBaseException {
	private static final long serialVersionUID = -8668022873024742830L;

	public CommandException() {
		super();
	}

	public CommandException(String message) {
		super(message);
	}

	public CommandException(Throwable cause) {
		super(cause);
	}

}
