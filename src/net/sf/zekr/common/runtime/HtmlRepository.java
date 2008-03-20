/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 21, 2005
 */

package net.sf.zekr.common.runtime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.AbstractRangedQuranText;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.RangedQuranText;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.common.util.UriUtils;
import net.sf.zekr.engine.audio.PlaylistProvider;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.AbstractSearchResult;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.lucene.QuranTextSearcher;
import net.sf.zekr.engine.search.tanzil.SearchResult;
import net.sf.zekr.engine.server.HttpServer;
import net.sf.zekr.engine.server.HttpServerUtils;
import net.sf.zekr.engine.template.AdvancedQuranSearchResultTemplate;
import net.sf.zekr.engine.template.ITransformer;
import net.sf.zekr.engine.template.MixedViewTemplate;
import net.sf.zekr.engine.template.MultiTranslationViewTemplate;
import net.sf.zekr.engine.template.QuranSearchResultTemplate;
import net.sf.zekr.engine.template.QuranViewTemplate;
import net.sf.zekr.engine.template.TransSearchResultTemplate;
import net.sf.zekr.engine.template.TranslationViewTemplate;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * HTML Creator object.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class HtmlRepository {
	private final static Logger logger = Logger.getLogger(HtmlRepository.class);
	private static ApplicationConfig config = ApplicationConfig.getInstance();

	/**
	 * The method will create a new HTML file if
	 * <ul>
	 * <li>Sura HTML file does not exist at <code>QURAN_CACHE_DIR</code>
	 * <li>HTML file exists but the file size is zero
	 * <li><code>update</code> is true
	 * </ul>
	 * Otherwise the file will be read from the HTML cache.
	 * 
	 * @param sura
	 *           sura number <b>(which is counted from 1) </b>
	 * @param aya
	 *           the aya number (this will affect on the end of the URL, which appends something like: #<code>sura</code>,
	 *           e.g. <code>file:///somepath/sura.html#5</code>. <b>Please note that <code>aya</code> should be sent
	 *           and counted from 1. </b> If <code>aya</code> is 0 the URL will not have <code>#ayaNumber</code> at
	 *           the end of it.
	 * @param update
	 *           Specify whether recreate the HTML file if it also exists.
	 * @return URL to the sura HTML file
	 * @throws HtmlGenerationException
	 */
	public static String getQuranUri(int sura, int aya, boolean update) throws HtmlGenerationException {
		try {
			String fileName = sura + ".html";
			File file = new File(Naming.getQuranCacheDir() + File.separator + fileName);
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create simple Quran HTML file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);

				ITransformer transformer = new QuranViewTemplate(FilteredQuranText.getInstance(), sura, aya);
				addPlaylistProvider(sura, transformer);
				osw.write(transformer.transform());
				osw.close();
			}
			return HttpServerUtils.getUrl(Naming.getQuranCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

	/**
	 * @param sura
	 * @param aya
	 * @return <code>HtmlRepository#getQuranUri(sura, aya, false);</code>
	 */
	public static String getQuranUri(int sura, int aya) throws HtmlGenerationException {
		return getQuranUri(sura, aya, false);
	}

	public static String getTransUri(int sura, int aya, boolean update) throws HtmlGenerationException {
		try {
			TranslationData td = config.getTranslation().getDefault();
			String fileName = sura + "_" + td.id + ".html";
			File file = new File(Naming.getTransCacheDir() + "/" + fileName);
			// if the file doesn't exist, or a zero-byte file exists
			update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0) {
				logger.info("Create simple translation HTML file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);

				ITransformer transformer = new TranslationViewTemplate(td, sura, aya);
				addPlaylistProvider(sura, transformer);
				osw.write(transformer.transform());
				osw.close();
			}
			return HttpServerUtils.getUrl(Naming.getTransCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

	public static String getTransUri(int sura, int aya) throws HtmlGenerationException {
		return getTransUri(sura, aya, false);
	}

	public static String getMixedUri(int sura, int aya, boolean update) throws HtmlGenerationException {
		try {
			TranslationData td = config.getTranslation().getDefault();
			String fileName = sura + "_" + td.id + ".html";
			File file = new File(Naming.getMixedCacheDir() + File.separator + fileName);
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create Quran mixed HTML file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);

				ITransformer transformer = new MixedViewTemplate(FilteredQuranText.getInstance(), td, sura, aya);
				addPlaylistProvider(sura, transformer);

				osw.write(transformer.transform());
				osw.close();
			}
			return HttpServerUtils.getUrl(Naming.getMixedCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

	public static String getCustomMixedUri(int sura, int aya, boolean update) throws HtmlGenerationException {
		try {
			List tdList = config.getCustomTranslationList();
			StringBuffer tidList = new StringBuffer();
			for (int i = 0; i < tdList.size(); i++) {
				String tid = ((TranslationData) tdList.get(i)).id;
				tidList.append(tid);
				if (i + 1 < tdList.size())
					tidList.append("-");
			}
			String fileName = sura + "_" + tidList + ".html";
			File file = new File(Naming.getMixedCacheDir() + File.separator + fileName);
			update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create Quran file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);
				TranslationData[] transData = (TranslationData[]) tdList.toArray(new TranslationData[] {});

				ITransformer tx = new MultiTranslationViewTemplate(FilteredQuranText.getInstance(), transData, sura, aya);
				addPlaylistProvider(sura, tx);

				osw.write(tx.transform());
				osw.close();
			}
			return HttpServerUtils.getUrl(Naming.getMixedCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

	/**
	 * @param searcher
	 * @param pageNo
	 *           0-based page number
	 * @return generated search result HTML
	 * @throws HtmlGenerationException
	 */
	public static String getAdvancedSearchQuranUri(AbstractSearchResult searchResult, int pageNo)
			throws HtmlGenerationException {
		try {
			String fileName = searchResult.getRawQuery().hashCode() + "_" + pageNo + ".html";
			File file = new File(Naming.getSearchCacheDir() + File.separator + fileName);
			logger.info("Create search file: " + file + " for keyword: \"" + searchResult.getRawQuery() + "\".");
			OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
					GlobalConfig.OUT_HTML_ENCODING);

			ITransformer tx = new AdvancedQuranSearchResultTemplate(searchResult, pageNo);
			osw.write(tx.transform());
			osw.close();
			return HttpServerUtils.getUrl(Naming.getSearchCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

//	public static String getSearchQuranUri(SearchResult searchResult, int pageNo)
//			throws HtmlGenerationException {
//		String fileName = searchResult.getRawQuery().hashCode() + "_" + pageNo + ".html";
//		File file = new File(Naming.getSearchCacheDir() + File.separator + fileName);
//		logger.info("Create search file: " + file + " for keyword: \"" + searcher.getRawQuery() + "\".");
//		OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
//				GlobalConfig.OUT_HTML_ENCODING);
//
//		ITransformer tx = new AdvancedQuranSearchResultTemplate(searcher, pageNo);
//		osw.write(tx.transform());
//		osw.close();
//		return HttpServerUtils.getUrl(Naming.getSearchCacheDir(getBase()) + "/" + fileName);
//	} catch (Exception e) {
//		throw new HtmlGenerationException(e);
//	}

//		try {
//			String fileName = keyword.hashCode() + ".html";
//			File file = new File(Naming.getSearchCacheDir() + File.separator + fileName);
//			// if (!file.exists() || file.length() == 0) {
//			logger.info("Create search file: " + file + " for keyword: \"" + keyword + "\".");
//			OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
//					GlobalConfig.OUT_HTML_ENCODING);
//
//			RangedQuranText rqt = new RangedQuranText(FilteredQuranText.getSimpleTextInstance(), searchScope);
//			ITransformer tx = new QuranSearchResultTemplate(rqt, keyword, matchDiac);
//			osw.write(tx.transform());
//			osw.close();
//			// }
//			return HttpServerUtils.getUrl(Naming.getSearchCacheDir(getBase()) + "/" + fileName);
//		} catch (Exception e) {
//			throw new HtmlGenerationException(e);
//		}
//	}

/*	public static String getSearchQuranUri(String keyword, boolean matchDiac, SearchScope searchScope)
			throws HtmlGenerationException {
		try {
			String fileName = keyword.hashCode() + ".html";
			File file = new File(Naming.getSearchCacheDir() + File.separator + fileName);
			// if (!file.exists() || file.length() == 0) {
			logger.info("Create search file: " + file + " for keyword: \"" + keyword + "\".");
			OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
					GlobalConfig.OUT_HTML_ENCODING);

			RangedQuranText rqt = new RangedQuranText(FilteredQuranText.getSimpleTextInstance(), searchScope);
			ITransformer tx = new QuranSearchResultTemplate(rqt, keyword, matchDiac);
			osw.write(tx.transform());
			osw.close();
			// }
			return HttpServerUtils.getUrl(Naming.getSearchCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}
*/
	
	public static String getSearchTransUri(String keyword, boolean matchDiac, boolean matchCase, SearchScope searchScope)
			throws HtmlGenerationException {
		try {
			TranslationData td = config.getTranslation().getDefault();
			AbstractRangedQuranText eqt = new RangedQuranText(td, searchScope);

			String fileName = keyword.hashCode() + "_" + td.id + "_" + matchCase + ".html";
			File file = new File(Naming.getSearchCacheDir() + File.separator + fileName);

			// if (!file.exists() || file.length() == 0) {
			logger.info("Create search file: " + file + " for keyword: \"" + keyword + "\".");
			OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
					GlobalConfig.OUT_HTML_ENCODING);

			ITransformer tx = new TransSearchResultTemplate(eqt, keyword, matchCase);
			osw.write(tx.transform());
			osw.close();
			// }
			return HttpServerUtils.getUrl(Naming.getSearchCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

	/**
	 * @param sura
	 * @param aya
	 * @return <code>HtmlRepository#getMixedUri(sura, aya, false);</code>
	 */
	public static String getMixedUri(int sura, int aya) throws HtmlGenerationException {
		return getMixedUri(sura, aya, false);
	}

	/**
	 * @param sura
	 * @param aya
	 * @return <code>HtmlRepository#getCustomMixedUri(sura, aya, false);</code>
	 */
	public static String getCustomMixedUri(int sura, int aya) throws HtmlGenerationException {
		return getCustomMixedUri(sura, aya, false);
	}

	private static String getBase() {
		return config.isHttpServerEnabled() ? HttpServer.CACHED_RESOURCE : Naming.getViewCacheDir();
	}

	private static void addPlaylistProvider(int sura, ITransformer transformer) throws Exception {
		if (config.getAudio().getCurrent() == null) {
			transformer.setProperty("AUDIO_DISABLED", Boolean.TRUE);
		} else {
			transformer.setProperty("AUDIO_DISABLED", Boolean.valueOf(!config.isAudioEnabled()));
			PlaylistProvider playlistProvider = config.getAudio().getCurrent().newPlaylistProvider(sura);
			String playlistPath = playlistProvider.providePlaylist();

			List list = new ArrayList();
			list.add(new Integer(playlistProvider.getSpecialItem(PlaylistProvider.SPECIAL_PRESTART)));
			list.add(new Integer(playlistProvider.getSpecialItem(PlaylistProvider.SPECIAL_START)));
			list.add(new Integer(playlistProvider.getSpecialItem(PlaylistProvider.SPECIAL_END)));
			transformer.setProperty("SPECIAL_INDEX_LIST", CollectionUtils.toSimpleJson(list));

			transformer.setProperty("VOLUME", config.getProps().getProperty("audio.volume"));
			transformer.setProperty("AUD_CONT_SURA", config.getProps().getProperty("audio.continuousSura"));
			transformer.setProperty("AUD_CONT_AYA", config.getProps().getProperty("audio.continuousAya"));
			transformer.setProperty("PLAYLIST_PROVIDER", playlistProvider);
			transformer.setProperty("PLAYLIST_URL", HttpServer.getServer().toUrl(playlistPath));
		}
	}

}
