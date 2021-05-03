/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 14, 2007
 */
package net.sf.zekr.engine.translation;


/**
 * Some utilities regarding translations.
 * 
 * @see net.sf.zekr.engine.translation.TranslationData
 * @see net.sf.zekr.engine.translation.Translation
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class TranslationUtils {
	private static final String DUMMY_FILE = "/?\\DUMMY/?\\";

	public static TranslationData getDummyTranslationData() {
		TranslationData td = new TranslationData();
		td.file = DUMMY_FILE;
		return td;
	}
	
	public static boolean isDummy(TranslationData td) {
		return DUMMY_FILE.equals(td.file);
	}
}
