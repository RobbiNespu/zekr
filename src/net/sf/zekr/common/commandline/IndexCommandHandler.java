/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2007
 */
package net.sf.zekr.common.commandline;

import java.util.Date;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.filter.QuranIndexerFilter;
import net.sf.zekr.engine.search.lucene.IndexCreator;
import net.sf.zekr.engine.search.lucene.LuceneIndexManager;
import net.sf.zekr.engine.translation.TranslationData;

import org.apache.commons.lang.StringUtils;

/**
 * Class for handling index (<tt>-index</tt>) command.
 * 
 * @author Mohsen Saboorian
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
		String tid = null;
		ApplicationConfig config = ApplicationConfig.getInstance();
		if (options.length >= 2) {
			tid = options[1].trim();
			if (StringUtils.isBlank(tid)) {
				throw new CommandException("Enter a valid translation id.");
			} else if (config.getTranslation().get(tid) == null) {
				throw new CommandException("No such translation pack: " + tid);
			}
		}

		int target;
		String path = null;
		if (o.equals("me")) {
			target = IndexCreator.ME_ONLY;
		} else if (o.equals("all")) {
			target = IndexCreator.ALL_USERS;
		} else {
			throw new CommandException("No such target: " + o);
		}
		try {
			Date date1 = new Date();
			LuceneIndexManager lim = config.getLuceneIndexManager();
			if (tid == null) { // index Quran
				lim.createQuranIndex(new FilteredQuranText(new QuranIndexerFilter(), IQuranText.SIMPLE_MODE), target, path,
						stdout);
			} else { // index translation
				TranslationData td = config.getTranslation().get(tid);
				td.load();
				lim.createQuranIndex(td, target, path, stdout);
			}
			Date date2 = new Date();
			config.saveConfig();
			stdout.println("Indexing took " + (date2.getTime() - date1.getTime()) + " ms.");
		} catch (Exception e) {
			throw new CommandException("Indexing aborted with the following error: " + e);
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

}
