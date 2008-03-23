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
import java.io.PrintStream;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.filter.QuranIndexerFilter;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.ProgressForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.eclipse.swt.widgets.Display;

/**
 * This class holds some methods to do all indexing jobs associated with a special document such as Quran
 * text. Each document which needs a kind of indexing will have some public methods inside this class.<br>
 * Indexing process is done in a separate thread.<br>
 * <br>
 * This class is immutable, hence thread-safe.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class IndexCreator {
	private static final LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
	private final static Logger logger = Logger.getLogger(IndexCreator.class);
	private Display display;
	private boolean indexQuranText_finished = false;
	private boolean indexingErrorOccurred = false;
	private String indexDir;
	private IndexingException indexingException;

	public static final int ME_ONLY = 1;
	public static final int ALL_USERS = 2;
	public static final int CUSTOM_PATH = 3;

	public IndexCreator(Display display) {
		this.display = display;
	}

	public IndexCreator() {
		this(null);
	}

	private Thread indexThread = new Thread() {
		public void run() {
			QuranTextIndexer qti = null;
			try {
				qti = new QuranTextIndexer(new FilteredQuranText(new QuranIndexerFilter(), IQuranText.SIMPLE_MODE), new File(
						indexDir));
				qti.doIndex();
				indexQuranText_finished = true;
				logger.debug("Index files created successfully.");
			} catch (Exception e) {
				logger.implicitLog(e);
				indexingException = new IndexingException(e);
				logger.error("Quran indexing failed!");
			} finally {
				if (!indexQuranText_finished) {
					if (qti != null) {
						logger.error("Rolling back indexing...");
						qti.rollBack();
					}
					indexingErrorOccurred = true;
				}
			}
		}
	};

	/**
	 * @param mode can be {@link #ME_ONLY}, {@link #ALL_USERS}, or {@link #CUSTOM_PATH}. If mode is equal to
	 *           <code>CUSTOM_PATH</code>, path parameter is also used, otherwise this parameter is unused.
	 * @param path path for creating indices in. Used iff mode is equal to <code>CUSTOM_PATH</code>
	 * @param stdout standard output to write progressing data to
	 * @throws IndexingException if any exception occurred during indexing process
	 */
	public void indexQuranTextSilently(int mode, String path, PrintStream stdout) throws IndexingException {
		if (mode == ME_ONLY) {
			indexDir = Naming.getQuranIndexDir();
		} else if (mode == ALL_USERS) {
			indexDir = ApplicationPath.QURAN_INDEX_DIR;
		} else if (mode == CUSTOM_PATH) {
			indexDir = path;
		} else {
			throw new IndexingException("Invalid index mode");
		}

		logger.debug("Start Quran text indexer in a separate thread.");
		indexThread.start();
		logger.info("Start indexing Quran text...");

		stdout.print("Indexing.");
		while (indexThread.isAlive()) {
			try {
				Thread.sleep(500);
				stdout.print('.');
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}

		if (!indexingErrorOccurred)
			stdout.print(" done.");
		stdout.println();
		if (indexingErrorOccurred)
			throw indexingException;
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
			indexDir = Naming.getQuranIndexDir();
		} else {
			indexDir = ApplicationPath.QURAN_INDEX_DIR;
		}

		// start indexing thread
		logger.debug("Start progress bar thread.");
		display.asyncExec(new ProgressThread(indexThread));

		logger.debug("Start Quran text indexer in a separate thread.");
		indexThread.start();
		logger.info("Start indexing Quran text...");

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
		private Thread indexThread;

		public ProgressThread(Thread indexThread) {
			this.indexThread = indexThread;
		}

		public void run() {
			ProgressForm pf = new ProgressForm(MessageBoxUtils.getShell(), meaning("PLEASE_WAIT"), meaning("INDEXING")
					+ "..." + "\n\n" + meaning("TAKE_TIME"));
			pf.show();
			while (!pf.getShell().isDisposed()) {
				if (indexQuranText_finished || indexingErrorOccurred)
					EventUtils.sendEvent(EventProtocol.END_WAITING);
				if (!pf.getDisplay().readAndDispatch()) {
					pf.getDisplay().sleep();
				}
			}
			if (pf.getState() == ProgressForm.CALCELED && !indexThread.isInterrupted()) {
				logger.debug("Indexing cancelled by user. Will interrupt indexing now...");
				indexThread.interrupt();
			}
		}

		String meaning(String key) {
			return lang.getMeaningById("PROGRESS", key);
		}
	}

}
