/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 17, 2006
 */
package net.sf.zekr.engine.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.engine.log.Logger;

/**
 * A collection of all available translations as <code>{@link TranslationData}</code> objects.<br>
 * If this class had at least a single <code>TranslationData</code>, it should be set as default
 * translation as well. No default translation means, there is no translation at all.
 * 
 * @author Mohsen Saboorian
 */
public class Translation {
	private final static Logger logger = Logger.getLogger(Translation.class);

	TranslationData defaultTrans;

	private Map translations = new HashMap();
	private List customGroup = new ArrayList();

	private Comparator localeComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			TranslationData td1 = (TranslationData) o1;
			TranslationData td2 = (TranslationData) o2;
			int res = td1.locale.toString().compareTo(td2.locale.toString());
			return res > 0 ? 1 : -1;
		}
	};

	/**
	 * @return default translation, or <code>null</code> if there is translation at all.
	 */
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
	}

	/**
	 * @return a sorted collection representation of translations. Changing this list may not affect on the
	 *         underling translation list. Returned list is not empty (size = 0, not <code>null</code>) if
	 *         there is no translation data item.
	 */
	public List getAllTranslation() {
		ArrayList ret = new ArrayList(translations.values());
		Collections.sort(ret, localeComparator);
		return ret;
	}

	/**
	 * @return a List of custom translations currently being used. Custom translations are a set of
	 *         translations all displayed side by side.
	 */
	public List getCustomGroup() {
		return customGroup;
	}

	public void setCustomGroup(List customGroup) {
		this.customGroup = customGroup;
	}
}
