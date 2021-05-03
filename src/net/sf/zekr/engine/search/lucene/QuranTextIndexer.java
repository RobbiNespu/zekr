/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2007
 */
package net.sf.zekr.engine.search.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.util.FileUtils;
import net.sf.zekr.engine.log.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * An abstract Quran text indexer. This class is capable of indexing subclasses of {@link IQuranText}. It's
 * intended to index Quran text as well as translations.
 * 
 * @author Mohsen Saboorian
 */
public class QuranTextIndexer implements IIndexer {
	public static final String LOCATION_FIELD = "location";
	public static final String CONTENTS_FIELD = "contents";

	private final static Logger logger = Logger.getLogger(QuranTextIndexer.class);
	private final static ApplicationConfig config = ApplicationConfig.getInstance();
	private IQuranText quranText;
	private File indexDir;
	private Analyzer analyzer;

	public QuranTextIndexer(IQuranText quranText, File indexDir, Analyzer analyzer) {
		this.quranText = quranText;
		this.indexDir = indexDir;
		this.analyzer = analyzer;
	}

	public void doIndex() throws IndexingException, InterruptedException {
		try {
			Date d1 = new Date();
			logger.debug("Indexing started for: " + quranText);
			// IndexWriter indexWriter = new IndexWriter(indexDir, analyzer, true);
			IndexWriter indexWriter = new IndexWriter(new SimpleFSDirectory(indexDir), analyzer, true, MaxFieldLength.LIMITED);
			indexWriter.setMergeFactor(20);
			indexWriter.setMaxBufferedDocs(2 * (config.getProps().getInt("index.speed", 50) + 2));
			logger.debug("A new instance of IndexWriter created.");
			logger.debug("Adding suras...");
			for (int sura = 1; sura <= 114; sura++) {
				int ayaCount = quranText.getSura(sura).length;
				for (int aya = 1; aya <= ayaCount; aya++) {
					Document doc = new Document();
					doc.add(new Field(CONTENTS_FIELD, quranText.get(sura, aya), Store.YES, Field.Index.ANALYZED));
					doc.add(new Field(LOCATION_FIELD, QuranPropertiesUtils.getLocation(sura, aya).toSortableString(),
							Store.YES, Field.Index.NOT_ANALYZED));
					indexWriter.addDocument(doc);

					if (Thread.interrupted()) { // test and clear interrupted status
						indexWriter.close(); // if not closed, operation cannot be rolled back (because index files are locked)
						throw new InterruptedException("Indexing task interrupted.");
					}
				}
				logger.debug("Sura " + sura + " indexed.");
			}
			logger.debug("Optimizing indices...");
			indexWriter.optimize();
			indexWriter.close();
			Date d2 = new Date();
			logger.info("Indexing " + quranText + " done. Took: " + (d2.getTime() - d1.getTime()) + "ms.");
		} catch (IOException e) {
			logger.debug("Error during indexing process: " + e);
			throw new IndexingException(e);
		}
	}

	/**
	 * Should be called when doIndex is not successful (when returned any exception).
	 */
	public void rollBack() {
		logger.info("Remove index directory: " + indexDir);
		FileUtils.delete(indexDir);
	}
}
