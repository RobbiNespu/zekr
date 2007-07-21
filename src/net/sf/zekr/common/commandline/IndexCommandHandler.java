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
import java.util.Date;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.search.lucene.IndexCreator;
import net.sf.zekr.engine.search.lucene.IndexingException;

/**
 * Class for handling index (<tt>-index</tt>) command.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class IndexCommandHandler extends CommandHandler {

	public IndexCommandHandler(String command, String[] options) {
		this.command = command;
		this.options = options;
	}

	public void execute() throws CommandException {
		if (options.length <= 0)
			throw new CommandException("Indexing target not specified. "
					+ "Enter either `me', `all' or a path as indexing target.");
		String o = options[0].trim();
		int target;
		String path = null;
		if (o.equals("me")) {
			target = IndexCreator.ME_ONLY;
		} else if (o.equals("all")) {
			target = IndexCreator.ALL_USERS;
		} else {
			target = IndexCreator.CUSTOM_PATH;
			path = o;
			File indexDir = new File(o);
			if (!indexDir.exists())
				throw new CommandException("Path not found: " + o);
			if (!indexDir.isDirectory())
				throw new CommandException("Not a directory: " + o);
		}
		try {
			Date date1 = new Date();
			ApplicationConfig.getInstance().createQuranIndex(target, path, stdout);
			Date date2 = new Date();
			stdout.println("Indexing took " + (date2.getTime() - date1.getTime()) + " ms.");
		} catch (IndexingException e) {
			throw new CommandException("Indexing aborted with the following error: " + e.getMessage());
		}
	}

}
