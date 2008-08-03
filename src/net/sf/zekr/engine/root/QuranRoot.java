/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 31, 2008
 */
package net.sf.zekr.engine.root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.ListOrderedMap;

/**
 * Data structure to hold Quran word root and their addresses.
 * 
 * @author Mohsen Saboorian
 */
public class QuranRoot {
	private static final int ROOT_LIST_SIZE = 1919;
	List rootList = new ArrayList(ROOT_LIST_SIZE);
	Map rootAddr = new HashMap(ROOT_LIST_SIZE);

	/**
	 * An array of all ayas of the Quran. Each entry is a {@link List} of root of each word in that aya,
	 * exactly at the location at which the word exists in that aya.
	 */
	OrderedMap[] reverseIndex = new OrderedMap[QuranPropertiesUtils.QURAN_AYA_COUNT];

	public QuranRoot(String rawRootText) {
		String[] rootLines = rawRootText.split("\n");
		for (int i = 0; i < ROOT_LIST_SIZE; i++) {
			String[] rootBody = rootLines[i].split("\t");
			String rootStr = rootBody[0];
			String rootAddrStr;
			// rootrootBody[1] is frequency, not really needed!
			rootAddrStr = rootBody[2];
			rootList.add(rootStr);
			String[] addrList = rootAddrStr.split(",");
			List rootAddrList = new ArrayList();
			RootAddress ra = null;
			for (int j = 0; j < addrList.length; j++) {
				String[] locStr = addrList[j].split(":");
				int absoluteAya;
				IQuranLocation loc;
				int wordIndex;
				if (locStr.length == 1) { // location is the same as previous aya
					loc = ra.loc;
					wordIndex = Integer.parseInt(locStr[0]);
				} else {
					absoluteAya = Integer.parseInt(locStr[0]);
					loc = QuranPropertiesUtils.getLocation(absoluteAya + 1);
					wordIndex = Integer.parseInt(locStr[1]);
				}
				ra = new RootAddress(loc, wordIndex);
				rootAddrList.add(ra);

				// TODO: load it for next release.
				// if (reverseIndex[absoluteAya] == null) {
				// reverseIndex[absoluteAya] = new ListOrderedMap();
				// }
				// reverseIndex[absoluteAya].put(new Integer(wordIndex), rootStr);
			}
			rootAddr.put(rootStr, rootAddrList);
		}
	}

	public Map getRootMap(IQuranLocation loc) {
		return reverseIndex[QuranPropertiesUtils.getAbsoluteLocation(loc)];
	}

	public List getRootList() {
		return rootList;
	}

	public List getRootAddress(String rootStr) {
		return (List) rootAddr.get(rootStr);
	}
}
