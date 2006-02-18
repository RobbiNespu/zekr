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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class Translation {
	private final static Logger logger = Logger.getLogger(Translation.class);

	TranslationData defaultTrans;
	private Map map = new HashMap();

	public TranslationData getDefault() {
		return defaultTrans;
	}

	public void setDefault(TranslationData defaultTrans) {
		this.defaultTrans = defaultTrans;
	}

	public TranslationData get(String langId, String transId) {
		return (TranslationData) map.get(getKey(langId, transId));
	}

	private String getKey(String langId, String transId) {
		return langId + "_" + transId;
	}

	public void add(TranslationData td) {
		map.put(getKey(td.langId, td.transId), td);
	}
	
	public Collection getAllTranslation() {
		return map.values();
	}

}
