/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 15, 2007
 */
package net.sf.zekr.common.commandline;

/**
 * Command interface.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public interface Command {
	/**
	 * Execute the command
	 * 
	 * @throws CommandException
	 */
	void execute() throws CommandException;

	/**
	 * Specifies whether application should be launched after performing the command or not.
	 * 
	 * @return <code>true</code> if application should be launched after performing this command; <code>false</code>
	 *         otherwise.
	 */
	boolean launchAfter();
}
