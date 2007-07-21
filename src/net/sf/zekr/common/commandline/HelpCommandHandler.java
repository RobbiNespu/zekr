/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 15, 2007
 */
package net.sf.zekr.common.commandline;

import net.sf.zekr.common.config.GlobalConfig;

/**
 * Class for handling help (<tt>-h</tt> or <tt>--help</tt>) command.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class HelpCommandHandler extends CommandHandler {
	public HelpCommandHandler(String command) {
	}

	public void execute() throws CommandException {
		stdout.println("Zekr, Open Quranic Project, ver. " + GlobalConfig.ZEKR_VERSION + ".");
		stdout.println("Copyright (c) 2004-2007 Mohsen Saboorian and other contributors, http://zekr.org.");
		stdout.println("This product includes software developed by"
				+ " the Apache Software Foundation http://www.apache.org.");
		stdout.println("Usage: zekr [-option]");
		stdout.println("NOTE: Use only one or zero option at a time, otherwise extra options will be ignored.");
		stdout.println("Options:");
		stdout.println("    -clean <opt>\tRemove cached data or configurations.");
		stdout.println("    \t\t\topt can be `cache', to clean cached data,");
		stdout.println("    \t\t\t`config' to reset to default configurations,");
		stdout.println("    \t\t\t`index-me' to remove indices for the current user,");
		stdout.println("    \t\t\tand `index-all' to remove indices for all users,");
		stdout.println("    \t\t\tor any combination of the above options separated with space.");
		stdout.println("    -h, --help\t\tPrint this help message.");
		stdout.println("    -index <opt>\tIndex Quran text. opt can be `me' or `all'");
		stdout.println("    \t\t\tto index for the current user or all users respectively.");
		stdout.println("    \t\t\tAlternatively you can specify a custom path,");
		stdout.println("    \t\t\tto create indices there.");
		stdout.println("    -workspace <dir>\tStart Zekr using the specified workspace.");
		stdout.println("    \t\t\tBy default ~/.zekr is used as workspace.");
	}
}
