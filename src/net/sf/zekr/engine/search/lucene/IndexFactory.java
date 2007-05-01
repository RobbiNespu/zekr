/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 25, 2007
 */
package net.sf.zekr.engine.search.lucene;

import java.io.File;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.ProgressForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

/**
 * This class holds some methods to do all indexing jobs associated with a special document such as Quran text. Each
 * document which needs a kind of indexing will have a public method inside this class.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class IndexFactory {
	private static final LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
	private final static Logger logger = Logger.getLogger(IndexFactory.class);
	private Display display;
	private boolean indexQuranText_finished = false;
	private String indexDir;

	public IndexFactory(Display display) {
		this.display = display;
	}

	/**
	 * @return <code>true</code> if indexing done successfully, <code>false</code> otherwise.
	 */
	public boolean indexQuranText() {
		int result = MessageBoxUtils.radioQuestionPrompt(new String[] {
				lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"), lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") },
				1, lang.getMeaningById("IMPORT_QUESTION", "FOLLOWING_USERS"), lang.getMeaning("QUESTION"));

		if (result == -1)
			return false;

		if (result == 0) {
			indexDir = Naming.QURAN_INDEX_DIR;
		} else {
			indexDir = ApplicationPath.QURAN_INDEX_DIR;
		}

		// start a modal, non-cancelable progress bar
//		new Thread() {
//			public void run() {
				display.asyncExec(new ProgressThread());
//			}
//		}.start();

		// start indexing thread
		logger.debug("Start Quran text indexer in a separate thread.");
		Thread indexThread = new Thread() {
			public void run() {
				QuranTextIndexer qti = null;
				try {
					qti = new QuranTextIndexer(QuranText.getSimpleTextInstance(), new File(indexDir));
					qti.doIndex();
					indexQuranText_finished = true;
				} catch (Exception e) {
					logger.log(e);
					logger.error("Quran indexing failed! Rolling back indexing...");
					if (qti != null)
						qti.roleBack();
				}
			}
		};
		indexThread.start();
		while (indexThread.isAlive()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return indexQuranText_finished;
	}

	public String getIndexDir() {
		return indexDir;
	}

	private class ProgressThread extends Thread {
		public void run() {
			ProgressForm pf = new ProgressForm(MessageBoxUtils.getShell(), meaning("PLEASE_WAIT"), meaning("INDEXING")
					+ "..." + "\n\n" + meaning("TAKE_TIME"));
			pf.show();
			while (!pf.getShell().isDisposed()) {
				if (indexQuranText_finished)
					EventUtils.sendEvent(EventProtocol.END_WAITING);
				if (!pf.getDisplay().readAndDispatch()) {
					pf.getDisplay().sleep();
				}
			}
		}

		String meaning(String key) {
			return lang.getMeaningById("PROGRESS", key);
		}
	}

}
