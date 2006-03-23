/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 17, 2006
 */
package net.sf.zekr.common.resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class Translation {
	private final static Logger logger = Logger.getLogger(Translation.class);

	TranslationData defaultTrans;
	private Map translations = new HashMap();

	public TranslationData getDefault() {
		return defaultTrans;
	}

	public void setDefault(TranslationData defaultTrans) {
		this.defaultTrans = defaultTrans;
	}

	public TranslationData get(String transId) {
		return (TranslationData) translations.get(getKey(transId));
	}

	private String getKey(String transId) {
		return transId;
	}

	public void add(TranslationData td) {
		translations.put(getKey(td.id), td);
	}
	
	public Collection getAllTranslation() {
		return translations.values();
	}

}
