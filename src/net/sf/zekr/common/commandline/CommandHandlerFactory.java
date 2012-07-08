/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 15, 2007
 */
package net.sf.zekr.common.commandline;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates command handler classes.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CommandHandlerFactory implements CommandConstants {

	/**
	 * @param args arguments to be parsed
	 * @return a list of <code>CommandHandler</code> items
	 */
	public static List<CommandHandler> getCommandHandler(String[] args) {
		List<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
		WorkspaceCommandHandler workspaceCommand = null;
		for (int i = 0; i < args.length; i++) {
			String command = args[i].trim();
			if (HELP_COMMAND_VERBOSE.equals(command) || HELP_COMMAND.equals(command)) {
				commandHandlers.add(new HelpCommandHandler(command));
			} else if (CLEAN_COMMAND.equals(command)) {
				List<String> options = new ArrayList<String>();
				i = fillOptions(options, args, i);
				commandHandlers.add(new CleanCommandHandler(command, options.toArray(new String[0])));
			} else if (INDEX_COMMAND.equals(command)) {
				List<String> options = new ArrayList<String>();
				i = fillOptions(options, args, i);
				commandHandlers.add(new IndexCommandHandler(command, options.toArray(new String[0])));
			} else if (WORKSPACE_COMMAND.equals(command)) {
				List<String> options = new ArrayList<String>();
				i = fillOptions(options, args, i);
				workspaceCommand = new WorkspaceCommandHandler(command, options.toArray(new String[0]));
			} else {
				commandHandlers.add(new OtherCommandHandler(command, null));
			}
		}

		// -workspace has precedence over other options
		if (workspaceCommand != null) {
			commandHandlers.add(0, workspaceCommand);
		}

		return commandHandlers;
	}

	private static int fillOptions(List<String> options, String[] args, int i) {
		while (i + 1 < args.length && !args[i + 1].startsWith("-")) { // check if there is some options for this command
			i++;
			options.add(args[i]);
		}
		return i;
	}
}
