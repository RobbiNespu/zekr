/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 15, 2007
 */
package net.sf.zekr.common.commandline;

public class OtherCommandHandler extends CommandHandler {

	public OtherCommandHandler(String command, String[] options) {
		this.command = command;
		this.options = options;
	}

	public void execute() throws CommandException {
		stdout.println("Unrecognized option: " + command);
		stdout.println("Use `--help' option for more information.");
	}

	@Override
	public boolean isSingle() {
		return false;
	}

}
