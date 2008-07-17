/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2008
 */
package net.sf.zekr.engine.search.lucene;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.filter.QuranIndexerFilter;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.translation.TranslationData;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

public class LuceneIndexManager {
	private static final String QURAN_INDEX = "quran";
	private Map indexReaderMap = new HashMap();
	private PropertiesConfiguration props;

	public LuceneIndexManager(PropertiesConfiguration props) {
		this.props = props;
	}

	/**
	 * This method first checks if the Quran is previously indexed (first in user home and then in installation
	 * directory). If not, it will try to index it, asking user where to index. If this is already indexed,
	 * returns the directory where Quran indices are.
	 * 
	 * @return directory of the Quran index if indexing was successful (or found index somewhere),
	 *         <code>null</code> otherwise
	 * @throws IndexingException if indexing or opening existing index failed
	 */
	public IndexReader getQuranIndex() throws IndexingException {
		try {
			String quranIndexPath = props.getString("index.quran.path");
			return getIndex(new String[] { Naming.getQuranIndexDir(), ApplicationPath.QURAN_INDEX_DIR },
					new FilteredQuranText(new QuranIndexerFilter(), IQuranText.SIMPLE_MODE), QURAN_INDEX, quranIndexPath,
					"index.quran.path", "index.quran.version");
		} catch (IOException e) {
			throw new IndexingException(e);
		}
	}

	public IndexReader getIndex(IQuranText quranText) throws IndexingException {
		if (quranText instanceof TranslationData)
			return getIndex((TranslationData) quranText);
		return getQuranIndex();
	}

	public IndexReader getIndex(TranslationData td) throws IndexingException {
		String lang = td.getLocale().getLanguage();
		String id = td.getId();
		String pathKey = "index.trans.path." + id;
		String versionKey = "index.trans.version." + id;
		String[] pathArray = new String[] { Naming.getTransIndexDir(td.getId()),
				ApplicationPath.TRANS_INDEX_DIR + "/" + lang };
		String quranIndexPath = props.getString("index.quran.path");
		return getIndex(pathArray, td, id, quranIndexPath, pathKey, versionKey);
	}

	private IndexReader getIndex(String[] pathArray, IQuranText quranText, String indexId, String indexPath,
			String indexPathKey, String indexVersionKey) throws IndexingException {
		try {
			IndexReader ir = (IndexReader) indexReaderMap.get(indexId);
			if (ir == null) {
				if (indexPath != null && IndexReader.indexExists(indexPath)) {
					return addIndexReader(indexId, indexPath);
				} else {
					IndexCreator indexCreator = new IndexCreator(pathArray, quranText, LuceneAnalyzerFactory
							.getAnalyzer(quranText));
					if (indexCreator.indexQuranText()) {
						props.setProperty(indexPathKey, indexCreator.getIndexDir());
						props.setProperty(indexVersionKey, GlobalConfig.ZEKR_BUILD_NUMBER);
						return addIndexReader(indexId, indexCreator.getIndexDir());
					} else {
						return null;
					}
				}
			} else {
				return ir;
			}
		} catch (Exception e) {
			throw new IndexingException(e);
		}
	}

	private IndexReader addIndexReader(String indexReaderKey, String indexPath) throws CorruptIndexException,
			IOException {
		IndexReader ir = IndexReader.open(indexPath);
		indexReaderMap.put(indexReaderKey, ir);
		return ir;
	}

	/**
	 * This method silently indexes Quran text. It should only be used for command line indexing. This method
	 * sets <tt>index.quran.path</tt> property to the index directory, if indexing finished without throwing
	 * <code>IndexingException</code>.
	 * 
	 * @param mode can be {@link IndexCreator#ME_ONLY}, {@link IndexCreator#ALL_USERS}, or
	 *           {@link IndexCreator#CUSTOM_PATH}. If mode is equal to <code>CUSTOM_PATH</code>, path parameter
	 *           is also used, otherwise this parameter is unused.
	 * @param path path for creating indices in. Used iff mode is equal to {@link IndexCreator#CUSTOM_PATH}
	 * @param stdout standard output to write progressing data to
	 * @throws IndexingException if any error occurred during indexing process.
	 */
	public void createQuranIndex(int mode, String path, PrintStream stdout) throws IndexingException {
		try {
			IndexCreator indexCreator = new IndexCreator(null, new FilteredQuranText(new QuranIndexerFilter(),
					IQuranText.SIMPLE_MODE), LuceneAnalyzerFactory.getAnalyzer(ZekrSnowballAnalyzer.QURAN_ANALYZER));
			indexCreator.indexQuranTextSilently(mode, path, stdout);
			props.setProperty("index.quran.path", indexCreator.getIndexDir());
			props.setProperty("index.quran.version", GlobalConfig.ZEKR_BUILD_NUMBER);
		} catch (IOException e) {
			throw new IndexingException(e);
		}
	}

}
