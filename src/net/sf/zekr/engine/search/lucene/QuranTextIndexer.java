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
import java.io.StringReader;
import java.util.Date;
import java.util.Iterator;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.util.FileUtils;
import net.sf.zekr.engine.log.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class QuranTextIndexer implements IIndexer {
	public static final String LOCATION_FIELD = "location";
	public static final String CONTENTS_FIELD = "contents";

	private final static Logger logger = Logger.getLogger(QuranTextIndexer.class);
	private final static ApplicationConfig config = ApplicationConfig.getInstance();
	private IQuranText quranText;
	private File indexDir;

	public QuranTextIndexer(IQuranText quranText, File indexDir) {
		this.quranText = quranText;
		this.indexDir = indexDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.engine.search.lucene.IIndexer#doIndex()
	 */
	public void doIndex() throws IndexingException {
		try {
			Date d1 = new Date();
			logger.debug("Indexing Quran text started.");
			IndexWriter indexWriter = new IndexWriter(indexDir, new ArabicAnalyzer(), true);
			indexWriter.setMergeFactor(15);
			indexWriter.setMaxBufferedDocs(2 * (config.getProps().getInt("index.speed") + 2));
			logger.debug("A new instance of IndexWriter created.");
			logger.debug("Adding suras...");
			for (int sura = 1; sura <= 114; sura++) {
				int ayaCount = quranText.getSura(sura).length;
				for (int aya = 1; aya <= ayaCount; aya++) {
					Document doc = new Document();
					doc.add(new Field(CONTENTS_FIELD, quranText.get(sura, aya), Store.YES, Field.Index.TOKENIZED));
					doc.add(new Field(LOCATION_FIELD, new QuranLocation(sura, aya).toSortableString(), Store.YES,
							Field.Index.UN_TOKENIZED));
					indexWriter.addDocument(doc);

					if (Thread.interrupted()) { // test and clear interrupted status
						throw new IndexingException("Indexing thread interrupted!");
					}
				}
				logger.debug("Sura " + sura + " indexed.");
			}
			logger.debug("Optimizing indices...");
			indexWriter.optimize();
			indexWriter.close();
			Date d2 = new Date();
			logger.info("Indexing Quran text done. Took: " + (d2.getTime() - d1.getTime()) + "ms.");
		} catch (IOException e) {
			logger.debug("Error during indexing process: " + e);
			throw new IndexingException(e);
		}
	}

	/**
	 * Should be called when doIndex is not successful (when returned any exception).
	 */
	public void rollBack() {
		logger.info("Remove index files inside " + indexDir);
		FileUtils.delete(indexDir);
	}

	/**
	 * Testing code.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String q = "ال*ك";

			// ArabicIndexer ai = new ArabicIndexer();
			ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer();
			IndexReader reader = IndexReader.open(new File("c:/temp/sss"));
			IndexSearcher is = new IndexSearcher(reader);
			QueryParser parser = new QueryParser(CONTENTS_FIELD, arabicAnalyzer);
			Query query = parser.parse(q);
			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
			query = query.rewrite(reader); // required to expand search terms
			System.out.println("Searching for: " + query.toString());
			Hits hits = is.search(query, Sort.INDEXORDER);
			System.out.println(hits.length());
			for (Iterator iter = hits.iterator(); iter.hasNext();) {
				Hit hit = (Hit) iter.next();
				System.out.println(hit.getDocument().toString());
			}

			Highlighter highlighter = new Highlighter(new ZekrHighlightFormatter(), new QueryScorer(query));
			for (int i = 0; i < hits.length(); i++) {
				String text = hits.doc(i).get(CONTENTS_FIELD);
				TokenStream tokenStream = arabicAnalyzer.tokenStream(CONTENTS_FIELD, new StringReader(text));
				// Get 3 best fragments and separate with a "..."
				String results = highlighter.getBestFragment(tokenStream, text);
				System.out.println(results);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
