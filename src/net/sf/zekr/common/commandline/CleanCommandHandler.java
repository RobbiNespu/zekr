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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.runtime.Naming;

import org.apache.commons.io.FileUtils;

/**
 * Class for handling clean (<tt>-clean</tt>) command.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class CleanCommandHandler extends CommandHandler {

	public CleanCommandHandler(String command, String[] options) {
		this.command = command;
		this.options = options;
	}

	public void execute() throws CommandException {
		if (options.length <= 0)
			throw new CommandException("No clean target specified. "
					+ "Enter either `cache', `config', `index-me', or `index-all' as cleaning target.");
		String targets = options[0];
		StringTokenizer st = new StringTokenizer(targets, ",");
		List delList = new ArrayList();
		while (st.hasMoreTokens()) {
			String target = st.nextToken();
			if (target.trim().equals("cache")) {
				delList.add(Naming.getCacheDir());
			} else if (target.trim().equals("config")) {
				delList.add(Naming.getConfigDir());
			} else if (target.trim().equals("index-me")) {
				delList.add(Naming.getQuranIndexDir());
			} else if (target.trim().equals("index-all")) {
				delList.add(ApplicationPath.QURAN_INDEX_DIR);
			} else {
				throw new CommandException("Invalid clean target: " + target);
			}
		}

		for (Iterator iter = delList.iterator(); iter.hasNext();) {
			String path = (String) iter.next();
			try {
				File f = new File(path);
				stdout.print("-> Deleting \"" + path + "\"");
				if (!f.exists()) {
					stdout.println(" failed. Path does not exist!");
					continue;
				}
				FileUtils.deleteDirectory(f);
				stdout.println(" done.");
			} catch (IOException e) {
				throw new CommandException("Cleaning error: " + e);
			}
		}
	}
}
