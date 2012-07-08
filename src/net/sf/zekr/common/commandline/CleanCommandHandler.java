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
 */
public class CleanCommandHandler extends CommandHandler {

	public CleanCommandHandler(String command, String[] options) {
		this.command = command;
		this.options = options;
	}

	public void execute() throws CommandException {
		if (options.length <= 0)
			throw new CommandException("No clean target specified. "
					+ "Enter either `view-cache', `playlist-cache', `config', `index-me', `index-all',"
					+ " `all' (for cleaning all the targets), or a combination of cleaning targets separated with comma.");
		String targets = options[0];
		StringTokenizer st = new StringTokenizer(targets, ",");
		List<String> delList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String target = st.nextToken();
			if (target.trim().equals("view-cache")) {
				delList.add(Naming.getViewCacheDir());
			} else if (target.trim().equals("playlist-cache")) {
				delList.add(Naming.getAudioCacheDir());
			} else if (target.trim().equals("config")) {
				delList.add(Naming.getConfigDir());
			} else if (target.trim().equals("index-me")) {
				delList.add(Naming.getQuranIndexDir());
			} else if (target.trim().equals("index-all")) {
				delList.add(ApplicationPath.QURAN_INDEX_DIR);
			} else if (target.trim().equals("all")) {
				delList.add(Naming.getViewCacheDir());
				delList.add(Naming.getAudioCacheDir());
				delList.add(Naming.getConfigDir());
				delList.add(Naming.getQuranIndexDir());
				delList.add(ApplicationPath.QURAN_INDEX_DIR);
			} else {
				throw new CommandException("Invalid clean target: " + target);
			}
		}

		for (Iterator<String> iter = delList.iterator(); iter.hasNext();) {
			String path = iter.next();
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

	@Override
	public boolean isSingle() {
		return false;
	}

}
