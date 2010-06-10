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
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.server.HttpServer;
import net.sf.zekr.engine.server.HttpServerUtils;
import net.sf.zekr.engine.template.AdvancedQuranSearchResultTemplate;
import net.sf.zekr.engine.template.ITransformer;
import net.sf.zekr.engine.template.MixedViewTemplate;
import net.sf.zekr.engine.template.MultiTranslationViewTemplate;
import net.sf.zekr.engine.template.QuranViewTemplate;
import net.sf.zekr.engine.template.TranslationViewTemplate;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * HTML creator object.
 * 
 * @author Mohsen Saboorian
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
	 * @param sura sura number <b>(which is counted from 1) </b>
	 * @param aya the aya number (this will affect on the end of the URL, which appends something like: #
	 *           <code>sura</code>, e.g. <code>file:///somepath/sura.html#5</code>. <b>Please note that
	 *           <code>aya</code> should be sent and counted from 1. </b> If <code>aya</code> is 0 the URL will
	 *           not have <code>#ayaNumber</code> at the end of it.
	 * @param update Specify whether recreate the HTML file if it also exists.
	 * @return URL to the sura HTML file
	 * @throws HtmlGenerationException
	 */
	public static String getQuranUri(int sura, int aya, boolean update) throws HtmlGenerationException {
		try {
			IUserView uvc = config.getUserViewController();
			String fileName = uvc.getPage() + ".html";
			File file = new File(Naming.getQuranCacheDir() + File.separator + fileName);
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			// update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create simple Quran HTML file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);

				ITransformer transformer = new QuranViewTemplate(new FilteredQuranText(), uvc);
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
			IUserView uvc = config.getUserViewController();
			String fileName = uvc.getPage() + "_" + td.id + ".html";
			File file = new File(Naming.getTransCacheDir() + "/" + fileName);
			// if the file doesn't exist, or a zero-byte file exists
			// update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0) {
				logger.info("Create simple translation HTML file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);

				ITransformer transformer = new TranslationViewTemplate(td, uvc);
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
			IUserView uvc = config.getUserViewController();
			String fileName = uvc.getPage() + "_" + td.id + ".html";
			File file = new File(Naming.getMixedCacheDir() + File.separator + fileName);
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			// update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create Quran mixed HTML file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);

				ITransformer transformer = new MixedViewTemplate(new FilteredQuranText(), td, uvc);

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
			List<TranslationData> tdList = config.getCustomTranslationList();
			StringBuffer tidList = new StringBuffer();
			for (int i = 0; i < tdList.size(); i++) {
				String tid = (tdList.get(i)).id;
				tidList.append(tid);
				if (i + 1 < tdList.size()) {
					tidList.append("-");
				}
			}
			IUserView uvc = config.getUserViewController();
			String fileName = uvc.getPage() + "_" + tidList + ".html";
			File file = new File(Naming.getMixedCacheDir() + File.separator + fileName);
			// update |= config.isAudioEnabled(); // if audio is enabled do not use precached html, always generate new one
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create Quran file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)),
						GlobalConfig.OUT_HTML_ENCODING);
				TranslationData[] transData = tdList.toArray(new TranslationData[] {});

				// ITransformer tx = new MultiTranslationViewTemplate(new FilteredQuranText(), transData, sura, aya);
				ITransformer tx = new MultiTranslationViewTemplate(new FilteredQuranText(), transData, uvc);

				osw.write(tx.transform());
				osw.close();
			}
			return HttpServerUtils.getUrl(Naming.getMixedCacheDir(getBase()) + "/" + fileName);
		} catch (Exception e) {
			throw new HtmlGenerationException(e);
		}
	}

	/**
	 * @param searchResult
	 * @param pageNo 0-based page number
	 * @return generated search result HTML
	 * @throws HtmlGenerationException
	 */
	public static String getAdvancedSearchQuranUri(SearchResultModel searchResult, int pageNo)
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

	public static String getTransUri(IQuranLocation location) throws HtmlGenerationException {
		return getTransUri(location.getSura(), location.getAya());
	}

	public static String getMixedUri(IQuranLocation loc) throws HtmlGenerationException {
		return getMixedUri(loc.getSura(), loc.getAya());
	}

	public static String getCustomMixedUri(IQuranLocation loc) throws HtmlGenerationException {
		return getCustomMixedUri(loc.getSura(), loc.getAya());
	}

	public static String getQuranUri(IQuranLocation loc) throws HtmlGenerationException {
		return getQuranUri(loc.getSura(), loc.getAya());
	}
}
