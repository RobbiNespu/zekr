/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 25, 2008
 */
package net.sf.zekr.engine.search.tanzil;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementations of this class will enrich a String into a regular expression to handle language character
 * similarities for searching.
 * 
 * @author Mohsen Saboorian
 */
abstract public class PatternEnricher {
	Map<String, Object> params = new HashMap<String, Object>();

	public void setParameter(String name, Object value) {
		params.put(name, value);
	}

	public Object getParameter(String name) {
		return params.get(name);
	}

	public abstract String enrich(String queryPattern);
}
