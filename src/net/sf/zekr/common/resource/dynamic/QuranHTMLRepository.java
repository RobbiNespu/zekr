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

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.template.QuranViewTemplate;
import net.sf.zekr.engine.template.SearchResultTemplate;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class QuranHTMLRepository {

	/**
	 * The method will create a new html file if
	 * <ul>
	 * <li>Sura html file does not exist at <code>HTML_QURAN_CACHE_DIR</code>
	 * <li>Html file exists but the file size is zero
	 * <li><code>update</code> is true
	 * </ul>
	 * Otherwise the file will be read from the html cache.
	 * 
	 * @param sura
	 *            sura number <b>(which is counted from 1) </b>
	 * @param aya
	 *            the aya number (this will affect on the end of the URL, which appends
	 *            something like: #<code>sura</code>, e.g.
	 *            <code>file:///somepath/sura.html#5</code>. <b>Please note that
	 *            <code>aya</code> should be sent and counted from 1. </b> If
	 *            <code>aya</code> is 0 the URL will not have <code>#ayaNumber</code>
	 *            at the end of it.
	 * @param update
	 *            Specify whether recreate the html file if it also exists.
	 * @return URL to the sura HTML file
	 */
	public static String getUrl(int sura, int aya, boolean update) {
		File file = new File(Naming.HTML_QURAN_CACHE_DIR + File.separator + sura + ".html");
		try {
			// if the file doesn't exist, or a zero-byte file exists, or if the
			// update flag (which signals to recreate the html file) is set
			if (!file.exists() || file.length() == 0 || update) {
				OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
						new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
				QuranViewTemplate qvt = new QuranViewTemplate();
				osw.write(qvt.transform(sura));
				osw.close();
			}
		} catch (IOException e) {
			Logger.getLogger(QuranHTMLRepository.class).log(e);
		}
		return file.getAbsolutePath() + ((aya == 0) ? "" : "#" + aya);
	}
	
	public static String getSearchUrl(String keyword, boolean matchDiac) {
		File file = new File(Naming.HTML_SEARCH_CACHE_DIR + File.separator + keyword.hashCode() + ".html");

		try {
			// FIXME: no search cache for now
			if (file.exists()) file.delete();
			OutputStreamWriter osw = new OutputStreamWriter(new BufferedOutputStream(
					new FileOutputStream(file)), GlobalConfig.OUT_HTML_ENCODING);
			SearchResultTemplate qrt = new SearchResultTemplate();
			osw.write(qrt.transform(keyword, matchDiac));
			osw.close();
		} catch (IOException e) {
			Logger.getLogger(QuranHTMLRepository.class).log(e);
		}
		return file.getAbsolutePath();
	}

	/**
	 * @param sura
	 * @param aya
	 * @return <code>getUrl(sura, aya, false);</code>
	 */
	public static String getUrl(int sura, int aya) {
		return getUrl(sura, aya, false);
	}

}
