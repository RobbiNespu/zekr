/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2008
 */
package net.sf.zekr.engine.revelation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.util.CryptoUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.translation.TranslationData;

public class RevelationData {
	private static final Logger logger = Logger.getLogger(TranslationData.class);
	public static final int SURA_MODE = 1;
	public static final int AYA_MODE = 2;

	public Map names = new HashMap();
	public int[] orders;
	public int[] years;
	public int mode;
	public String id;
	public String version;
	public byte[] signature;
	public File archiveFile;
	public String delimiter;

	private boolean loaded;
	private boolean verified;

	public RevelationData() {
	}

	public Map getNames() {
		return names;
	}

	public String getName(String langCode) {
		return (String) names.get(langCode);
	}

	/**
	 * Cautious: this method should only be called upon instantiation of {@link ApplicationConfig}.
	 * 
	 * @return
	 */
	public String getName() {
		return getName(LanguageEngine.getInstance().getLocale().getLanguage());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMode() {
		return mode;
	}

	public int[] getOrders() {
		return orders;
	}

	public void setOrders(int[] orders) {
		this.orders = orders;
	}

	public int[] getYears() {
		return years;
	}

	public void setYears(int[] years) {
		this.years = years;
	}

	public void load() throws IOException {
		if (!loaded) {
			Date date1 = new Date();
			loadAndVerify();
			Date date2 = new Date();
			logger.debug("Loading revelation pack \"" + id + "\" took " + (date2.getTime() - date1.getTime()) + " ms.");
		} else {
			logger.debug("Revelation pack already loaded: " + id);
		}
	}

	private boolean verify(InputStream is, byte[] textBuf) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		bis.read(textBuf, 0, textBuf.length);

		try {
			verified = CryptoUtils.verify(textBuf, signature);
		} catch (GeneralSecurityException e) {
			logger.error("Error occurred during revelation order verification: ", e);
		}
		if (verified)
			logger.debug("Revelation pack is valid");
		else
			logger.debug("Revelation pack is not valid.");
		return verified;
	}

	private void loadAndVerify() throws IOException {
		ZipFile zf = null;
		try {
			logger.info("Load revelation order pack: " + this);
			zf = new ZipFile(archiveFile);
			ZipEntry ze = zf.getEntry(id + ".order.txt");
			if (ze == null) {
				logger.error("File load failed. No proper entry found in \"" + archiveFile.getName() + "\".");
				return;
			}

			byte[] textBuf = new byte[(int) ze.getSize()];
			if (!verify(zf.getInputStream(ze), textBuf))
				logger.warn("Unauthorized revelation order pack: " + this);

			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textBuf)));
			for (int i = 0; i < orders.length; i++) {
				String order = br.readLine();
				orders[i] = Integer.parseInt(order.trim());
			}
			br.close();
			logger.log("Revelation order pack loaded successfully: " + this);
		} finally {
			try {
				zf.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
}
