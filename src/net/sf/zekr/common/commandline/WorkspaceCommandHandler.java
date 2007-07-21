/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2007
 */
package net.sf.zekr.common.commandline;

import java.io.File;

import net.sf.zekr.common.runtime.Naming;

public class WorkspaceCommandHandler extends CommandHandler {

	public WorkspaceCommandHandler(String command, String[] options) {
		this.command = command;
		this.options = options;
	}

	public void execute() throws CommandException {
		if (options.length <= 0)
			throw new CommandException("Workspace not specified");
		String o = options[0];
		File ws = new File(o);
		if (!ws.exists())
			throw new CommandException("Error setting workspace! Path not found: " + o);
		if (!ws.isDirectory())
			throw new CommandException("Error setting workspace! Not a directory: " + o);

		Naming.setWorkspace(o);
	}

	public boolean launchAfter() {
		return true;
	}
}
