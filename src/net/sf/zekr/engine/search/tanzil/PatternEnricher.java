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
 * @author Mohsen Saboorian
 */
abstract public class PatternEnricher {
	Map params = new HashMap();

	public void setParameter(String name, Object value) {
		params.put(name, value);
	}

	public Object getParameter(String name) {
		return params.get(name);
	}

	public abstract String enrich(String queryPattern);
}
