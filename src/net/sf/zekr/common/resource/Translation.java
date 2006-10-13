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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

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

	/**
	 * Backing sorted list for <code>TranslationData</code> entries.
	 */
	private TreeSet sortedTransList = new TreeSet(new Comparator() {
		public int compare(Object o1, Object o2) {
			TranslationData td1 = (TranslationData) o1;
			TranslationData td2 = (TranslationData) o2;
			int res = td1.locale.toString().compareTo(td2.locale.toString());
			return res > 0 ? 1 : -1;
		}

		public boolean equals(Object obj) {
			return false;
		}
	});

	public TranslationData getDefault() {
		return defaultTrans;
	}

	public void setDefault(TranslationData defaultTrans) {
		this.defaultTrans = defaultTrans;
	}

	public TranslationData get(String transId) {
		return (TranslationData) translations.get(transId);
	}

	public void add(TranslationData td) {
		translations.put(td.id, td);
		sortedTransList.add(td);
	}

	public Collection getAllTranslation() {
		return sortedTransList;
	}

}
