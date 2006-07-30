/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     21/01/2005
 */

package net.sf.zekr.common.resource.dynamic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.TranslationData;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.UrlUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.AbstractQuranViewTemplate;
import net.sf.zekr.engine.theme.MixedViewTemplate;
import net.sf.zekr.engine.theme.QuranViewTemplate;
import net.sf.zekr.engine.theme.SearchResultTemplate;
import net.sf.zekr.engine.theme.TranslationViewTemplate;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class HtmlRepository {
	private final static Logger logger = Logger.getLogger(HtmlRepository.class);
	private static ApplicationConfig config = ApplicationConfig.getInstance();

	/**
	 * The method will create a new html file if
	 * <ul>
	 * <li>Sura html file does not exist at <code>QURAN_CACHE_DIR</code>
	 * <li>HTML file exists but the file size is zero
	 * <li><code>update</code> is true
	 * </ul>
	 * Otherwise the file will be read from the html cache.
	 * 
	 * @param sura sura number <b>(which is counted from 1) </b>
	 * @param aya the aya number (this will affect on the end of the URL, which appends
	 *            something like: #<code>sura</code>, e.g.
	 *            <code>file:///somepath/sura.html#5</code>. <b>Please note that
	 *            <code>aya</code> should be sent and counted from 1. </b> If
	 *            <code>aya</code> is 0 the URL will not have <code>#ayaNumber</code>
	 *            at the end of it.
	 * @param update Specify whether recreate the html file if it also exists.
	 * @return URL to the sura HTML file
	 */
	public static String getQuranUrl(int sura, int aya, boolean update) {
		File file = new File(Naming.QURAN_CACHE_DIR + File.separator + sura + ".html");
		try {
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create Quran file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
						new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
				AbstractQuranViewTemplate aqvt;
				aqvt = new QuranViewTemplate(QuranText.getInstance());
				osw.write(aqvt.transform(sura));
				osw.close();
			}
		} catch (IOException e) {
			logger.log(e);
		}
		return UrlUtils.toURI(file) + ((aya == 0) ? "" : "#" + aya);
	}

	public static String getMixedUrl(int sura, int aya, boolean update) {
		TranslationData td = config .getTranslation().getDefault();
		File file = new File(Naming.MIXED_CACHE_DIR + File.separator + sura + ".html");
		try {
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			if (!file.exists() || file.length() == 0 || update) {
				logger.info("Create Quran file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
						new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
				td.load(); // load if not loaded before
				MixedViewTemplate mvt = new MixedViewTemplate(QuranText.getInstance(), td);
				osw.write(mvt.transform(sura));
				osw.close();
			}
		} catch (IOException e) {
			logger.log(e);
		}
		return UrlUtils.toURI(file) + ((aya == 0) ? "" : "#" + aya);
	}

	public static String getSearchQuranUrl(String keyword, boolean matchDiac) {
		File file = new File(Naming.SEARCH_CACHE_DIR + File.separator + keyword.hashCode()
				+ ".html");

		try {
			// TODO: no search cache for now
			if (file.exists()) {
				logger.info("Delete search file: " + file);
				file.delete();
			}
			logger.info("Create search file: " + file + " for keyword: \"" + keyword + "\".");
			OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
					new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
			SearchResultTemplate qrt = new SearchResultTemplate();
			osw.write(qrt.transform(keyword, matchDiac));
			osw.close();
		} catch (IOException e) {
			logger.log(e);
		}
		return UrlUtils.toURI(file);
	}

	/**
	 * @param sura
	 * @param aya
	 * @return <code>getQuranUrl(sura, aya, false);</code>
	 */
	public static String getQuranUrl(int sura, int aya) {
		return getQuranUrl(sura, aya, false);
	}

	public static String getTransUrl(int sura, int aya) {
		TranslationData td = config .getTranslation().getDefault();
		File file = new File(Naming.TRANS_CACHE_DIR + "/" + sura + "_" + td.id + ".html");
		try {
			// if the file doesn't exist, or a zero-byte file exists
			if (!file.exists() || file.length() == 0) {
				logger.info("Create translation file: " + file);
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
						new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
				td.load(); // load if not loaded before
				AbstractQuranViewTemplate qvt = new TranslationViewTemplate(td);
				osw.write(qvt.transform(sura));
				osw.close();
			}
		} catch (IOException e) {
			logger.log(e);
		}
		return UrlUtils.toURI(file) + ((aya == 0) ? "" : "#" + aya);
	}

	/**
	 * @param sura
	 * @param aya
	 * @return <code>getMixedUrl(sura, aya, false);</code>
	 */
	public static String getMixedUrl(int sura, int aya) {
		return getMixedUrl(sura, aya, false);
	}

}
