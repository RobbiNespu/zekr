/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 17, 2006
 */
package net.sf.zekr.common.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.velocity.runtime.parser.Token;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.IQuranTranslation;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class TranslationData implements IQuranTranslation {
	private final static Logger logger = Logger.getLogger(TranslationData.class);

	public String transId;
	public String langId;
	public String encoding;
	public String file;

	private String[][] transText;

	public String get(int suraNum, int ayaNum) {
		return transText[suraNum - 1][ayaNum - 1];
	}

	public String[] getSura(int suraNum) {
		return transText[suraNum - 1];
	}

	public String[][] getFullTranslation() {
		return transText;
	}

	public void setText(String[][] text) {
		transText = text;
	}

	public String toString() {
		return transId + "(" + langId + ")";
	}

	public void load() {
		if (loaded())
			loadFile(langId, transId);
	}

	public boolean loaded() {
		return transText == null;
	}

	public void loadFile(String langId, String transId) {
		logger.log("Loading translation pack " + this);
		transText = loadTranslation(ApplicationConfig.getQuranTrans(file), encoding);
		logger.log("Translation pack " + this + " loaded!");
	}

	private static String[][] loadTranslation(String file, String encoding) {
		String[][] text = new String[114][];
		try {
			File f = new File(file);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f), encoding);
			char[] buf = new char[(int) f.length()];
			isr.read(buf); // read the translation text fully
			String rawText = new String(buf);
			refineText(text, rawText);
		} catch (IOException e) {
			logger.fatal(e);
		}
		return text;
	}

	private static void refineText(String[][] text, String rawText) {
		QuranProperties quranProps = QuranProperties.getInstance();
		StringTokenizer st = new StringTokenizer(rawText, "\n");
		String[] sura;

		// load the first sura (only the first sura has Bismillah meaning)
		for (int i = 1; st.hasMoreTokens() && i <= 114; i++) {
			sura = new String[quranProps.getSura(i).getAyaCount()];
			for (int j = 0; j < sura.length; j++) {
				sura[j] = st.nextToken();
			}
			text[i - 1] = sura;
		}
	}

}
