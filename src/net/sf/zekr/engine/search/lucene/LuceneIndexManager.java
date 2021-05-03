/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 16, 2008
 */
package net.sf.zekr.engine.search.lucene;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
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
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * This class manages Lucene indices for Qur'an and translations.
 * 
 * @author Mohsen Saboorian
 */
public class LuceneIndexManager {
	private static final String QURAN_INDEX = "quran";
	private Map<String, ZekrIndexReader> indexReaderMap = new HashMap<String, ZekrIndexReader>();
	private PropertiesConfiguration props;

	public LuceneIndexManager(PropertiesConfiguration props) {
		this.props = props;
	}

	/**
	 * This method first checks if the Qur'an is previously indexed (first in user home and then in
	 * installation directory). If not, it will try to index it, asking user where to index. If this is already
	 * indexed, returns the directory where Qur'an indices are.
	 * 
	 * @return directory of the Qur'an index if indexing was successful (or found index somewhere),
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

	/**
	 * Creates an index in the place user selects. This method first checks if an index already exists for
	 * all-users or not. If not it continues to ask where to create index files.<br>
	 * It uses underlying cache to store {@link ZekrIndexReader}s already read in this session.
	 * 
	 * @param pathArray the first element should be for me-only mode, the second element for all-users.
	 * @param quranText
	 * @param indexId
	 * @param indexPath
	 * @param indexPathKey
	 * @param indexVersionKey
	 * @return cached or newly-created {@link ZekrIndexReader} instance
	 * @throws IndexingException
	 */
	@SuppressWarnings("unchecked")
	private ZekrIndexReader getIndex(String[] pathArray, IQuranText quranText, String indexId, String indexPath,
			String indexPathKey, String indexVersionKey) throws IndexingException {
		try {
			ZekrIndexReader zir = indexReaderMap.get(indexId);
			if (zir == null) {
				if (indexPath != null && IndexReader.indexExists(new SimpleFSDirectory(new File(indexPath)))) {
					return newIndexReader(quranText, indexId, indexPath);
				} else {
					// check if index is already created for all-users, and its modify date is newer than zekr build date
					File indexDir = new File(pathArray[1]);
					SimpleFSDirectory dir = new SimpleFSDirectory(indexDir);
					if (IndexReader.indexExists(dir)) {
						Collection<File> listFiles = FileUtils.listFiles(indexDir, new String[] { "cfs" }, false);
						if (listFiles.size() > 0) {
							if (FileUtils.isFileNewer(listFiles.iterator().next(), GlobalConfig.ZEKR_BUILD_DATE)) {
								ZekrIndexReader res;
								res = newIndexReader(quranText, indexId, pathArray[1]);
								props.setProperty(indexPathKey, pathArray[1]);
								props.setProperty(indexVersionKey, GlobalConfig.ZEKR_BUILD_NUMBER);
								return res;
							}
						}
					}
					IndexCreator indexCreator = new IndexCreator(pathArray, quranText, LuceneAnalyzerFactory
							.getAnalyzer(quranText));
					if (indexCreator.indexQuranText()) {
						props.setProperty(indexPathKey, indexCreator.getIndexDir());
						props.setProperty(indexVersionKey, GlobalConfig.ZEKR_BUILD_NUMBER);
						return newIndexReader(quranText, indexId, indexCreator.getIndexDir());
					} else {
						// a non-interruption (bad) exception occurred
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
		SimpleFSDirectory directory = new SimpleFSDirectory(new File(indexPath));
		IndexReader ir = IndexReader.open(directory, null, true);
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
