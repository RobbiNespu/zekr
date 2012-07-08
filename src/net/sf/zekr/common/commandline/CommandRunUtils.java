/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2007
 */
package net.sf.zekr.common.commandline;

import java.util.List;

import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CommandRunUtils {
	/**
	 * This method performs all command-related tasks, and catch and display any {@link CommandException}
	 * occurred.
	 * 
	 * @param args
	 * @return <code>true</code> if application should be also launched, <code>false</code> otherwise. Normally
	 *         if user enters an argument, Zekr is not launched, executing that command instead.
	 */
	public static boolean performAll(String[] args) {
		try {
			List<CommandHandler> cmds = CommandHandlerFactory.getCommandHandler(args);
			boolean la = true;
			for (CommandHandler cmd : cmds) {
				cmd.execute();
				la &= cmd.launchAfter();
			}
			return la;
		} catch (CommandException e) {
			Logger logger = Logger.getLogger(CommandRunUtils.class);
			System.err.println(e);
			logger.implicitLog(e);
			return false;
		}
	}
}
