/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2008
 */
package net.sf.zekr.engine.revelation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class contains RevelationData loaded from *.revel.zip files from <tt>res/text/metadata</tt>.
 * 
 * @author Mohsen Saboorian
 */
public class Revelation {
	private RevelationData defaultRevelationData;
	private Map<String, RevelationData> revelList = new LinkedHashMap<String, RevelationData>();

	public void add(RevelationData rd) {
		revelList.put(rd.getId(), rd);
	}

	public RevelationData get(String revelId) {
		return revelList.get(revelId);
	}

	public Collection<RevelationData> getAllRevels() {
		return revelList.values();
	}

	public void setDefault(RevelationData defaultRevelationData) {
		this.defaultRevelationData = defaultRevelationData;
	}

	public RevelationData getDefault() {
		return defaultRevelationData;
	}
}
