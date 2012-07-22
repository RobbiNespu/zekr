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
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.ProgressForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.apache.lucene.analysis.Analyzer;
import org.eclipse.swt.widgets.Display;

/**
 * This class holds some methods to do all indexing jobs associated with a special document such as Quran
 * text. Each document which needs a kind of indexing will have some public methods inside this class.<br>
 * Indexing process is done in a separate thread.<br>
 * <br>
 * This class is immutable, hence thread-safe.
 * 
 * @author Mohsen Saboorian
 */
public class IndexCreator {
   private static final LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
   private final static Logger logger = Logger.getLogger(IndexCreator.class);
   private Display display;
   private boolean indexQuranTextFinished = false;
   private boolean indexingErrorOccurred = false;
   private String indexDir;
   private IndexingException indexingException;
   private Analyzer analyzer;
   private IQuranText quranText;
   private String[] pathArray;

   public static final int ME_ONLY = 1;
   public static final int ALL_USERS = 2;
   public static final int CUSTOM_PATH = 3;

   /**
    * @param path a two-value array of paths. The first one is for {@link #ME_ONLY} and the second one is for
    *           {@link #ALL_USERS} index path.
    * @param quranText the abstract Quran text to index
    * @param analyzer the analyzer to be used for indexing. The same analyzer should be used later for query
    *           parsing.
    * @param display graphical display to use for showing indexing progress on (if a non-silent indexing is
    *           performed)
    */
   public IndexCreator(String[] path, IQuranText quranText, Analyzer analyzer, Display display) {
      this.pathArray = path;
      this.quranText = quranText;
      this.analyzer = analyzer;
      this.display = display;
   }

   /**
    * @param path a two-value array of paths. The first one is for {@link #ME_ONLY} and the second one is for
    *           {@link #ALL_USERS} index path.
    * @param quranText the abstract Quran text to index
    * @param analyzer the analyzer to be used for indexing. The same analyzer should be used later for query
    *           parsing.
    */
   public IndexCreator(String[] path, IQuranText quranText, Analyzer analyzer) {
      this(path, quranText, analyzer, Display.getCurrent());
   }

   private Thread indexThread = new Thread("Quran text indexer") {
      public void run() {
         QuranTextIndexer qti = null;
         try {
            qti = new QuranTextIndexer(quranText, new File(indexDir), analyzer);
            qti.doIndex();
            indexQuranTextFinished = true;
            logger.debug("Index files created successfully.");
         } catch (InterruptedException ie) {
            logger.error("Indexing interrupted on: " + quranText);
         } catch (Exception e) {
            logger.implicitLog(e);
            indexingException = new IndexingException(e);
            logger.error("Indexing failed on: " + quranText);
         } finally {
            if (!indexQuranTextFinished) {
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
    * @param path path for creating indices in.
    * @param stdout standard output to write progressing data to
    * @throws IndexingException if any exception occurred during indexing process
    */
   public void indexSilently(String path, PrintStream stdout) throws IndexingException {
      indexDir = path;

      logger.debug("Start Quran/translation text indexer in a separate thread.");
      indexThread.start();
      logger.info("Start indexing text " + quranText + "...");

      stdout.print("Indexing [" + quranText + "] .");
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
            0, lang.getMeaningById("IMPORT_QUESTION", "FOLLOWING_USERS"), lang.getMeaning("QUESTION"));

      if (result == -1)
         return false;

      if (result == 0) {
         indexDir = pathArray[0];
      } else {
         indexDir = pathArray[1];
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
      return indexQuranTextFinished;
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
            if (indexQuranTextFinished || indexingErrorOccurred)
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

   public IndexingException getIndexingException() {
      return indexingException;
   }

   public boolean isIndexingErrorOccurred() {
      return indexingErrorOccurred;
   }
}
