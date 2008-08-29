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
import net.sf.zekr.ui.MessageBoxUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

/**
 * This class manages Lucene indices for Quran and translations.
 * 
 * @author Mohsen Saboorian
 */
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
	public ZekrIndexReader getQuranIndex() throws IndexingException {
		try {
			String quranIndexPath = props.getString("index.quran.path");
			return getIndex(new String[] { Naming.getQuranIndexDir(), ApplicationPath.QURAN_INDEX_DIR },
					new FilteredQuranText(new QuranIndexerFilter(), IQuranText.SIMPLE_MODE), QURAN_INDEX, quranIndexPath,
					"index.quran.path", "index.quran.version");
		} catch (IOException e) {
			throw new IndexingException(e);
		}
	}

	public ZekrIndexReader getIndex(IQuranText quranText) throws IndexingException {
		if (quranText instanceof TranslationData)
			return getIndex((TranslationData) quranText);
		return getQuranIndex();
	}

	public ZekrIndexReader getIndex(TranslationData td) throws IndexingException {
		String id = td.getId();
		String pathKey = "index.trans.path." + id;
		String versionKey = "index.trans.version." + id;
		String[] pathArray = new String[] { Naming.getTransIndexDir(td.getId()),
				ApplicationPath.TRANS_INDEX_DIR + "/" + td.getId() };
		return getIndex(pathArray, td, id, props.getString(pathKey), pathKey, versionKey);
	}

	private ZekrIndexReader getIndex(String[] pathArray, IQuranText quranText, String indexId, String indexPath,
			String indexPathKey, String indexVersionKey) throws IndexingException {
		try {
			ZekrIndexReader zir = (ZekrIndexReader) indexReaderMap.get(indexId);
			if (zir == null) {
				if (indexPath != null && IndexReader.indexExists(indexPath)) {
					return newIndexReader(quranText, indexId, indexPath);
				} else {
					IndexCreator indexCreator = new IndexCreator(pathArray, quranText, LuceneAnalyzerFactory
							.getAnalyzer(quranText));
					if (indexCreator.indexQuranText()) {
						props.setProperty(indexPathKey, indexCreator.getIndexDir());
						props.setProperty(indexVersionKey, GlobalConfig.ZEKR_BUILD_NUMBER);
						return newIndexReader(quranText, indexId, indexCreator.getIndexDir());
					} else {
						// a non-interruption error occurred
						if (indexCreator.isIndexingErrorOccurred() && indexCreator.getIndexingException() != null) {
							MessageBoxUtils.showActionFailureError(indexCreator.getIndexingException());
						}
						return null;
					}
				}
			} else {
				return zir;
			}
		} catch (Exception e) {
			throw new IndexingException(e);
		}
	}

	private ZekrIndexReader newIndexReader(IQuranText quranText, String indexReaderKey, String indexPath)
			throws CorruptIndexException, IOException {
		IndexReader ir = IndexReader.open(indexPath);
		ZekrIndexReader zir = new ZekrIndexReader(quranText, indexReaderKey, ir);
		indexReaderMap.put(indexReaderKey, zir);
		return zir;
	}

	/**
	 * This method silently indexes a Quran text. It should only be used for command line indexing. This method
	 * sets <tt>index.[quran/trans].path[.trans id]</tt> property to the index directory, if indexing finished
	 * without throwing <code>IndexingException</code>.
	 * 
	 * @param mode can be {@link IndexCreator#ME_ONLY} or {@link IndexCreator#ALL_USERS}.
	 * @param path path for creating indices in. Used iff mode is equal to {@link IndexCreator#CUSTOM_PATH}
	 * @param stdout standard output to write progressing data to
	 * @throws IndexingException if any error occurred during indexing process.
	 */
	public void createQuranIndex(IQuranText quranText, int mode, String path, PrintStream stdout)
			throws IndexingException {
		String pathKey;
		String versionKey;
		String[] pathArray;
		if (quranText instanceof TranslationData) {
			String id = ((TranslationData) quranText).getId();
			pathKey = "index.trans.path." + id;
			versionKey = "index.trans.version." + id;
			pathArray = new String[] { Naming.getTransIndexDir(id), ApplicationPath.TRANS_INDEX_DIR + "/" + id };
		} else {
			pathKey = "index.quran.path";
			versionKey = "index.quran.version";
			pathArray = new String[] { Naming.getQuranIndexDir(), ApplicationPath.QURAN_INDEX_DIR };
		}
		String indexPath = mode == IndexCreator.ME_ONLY ? pathArray[0] : pathArray[1];

		IndexCreator indexCreator = new IndexCreator(null, quranText, LuceneAnalyzerFactory.getAnalyzer(quranText));
		indexCreator.indexSilently(indexPath, stdout);

		props.setProperty(pathKey, indexCreator.getIndexDir());
		props.setProperty(versionKey, GlobalConfig.ZEKR_BUILD_NUMBER);
	}
}
