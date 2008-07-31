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
	List rootList = new ArrayList(1919);
	Map rootAddr = new HashMap(1919);

	/**
	 * An array of all ayas of the Quran. Each entry is a {@link List} of root of each word in that aya,
	 * exactly at the location at which the word exists in that aya.
	 */
	OrderedMap[] reverseIndex = new OrderedMap[QuranPropertiesUtils.QURAN_AYA_COUNT];

	public static class RootAddress {
		private IQuranLocation loc;
		private int wordCount;

		public RootAddress(IQuranLocation loc, int wordCount) {
			this.loc = loc;
			this.wordCount = wordCount;
		}
	}

	public QuranRoot(String rawRootText) {
		String[] rootLines = rawRootText.split("\n");
		for (int i = 0; i < rootLines.length - 1; i++) {
			String[] rootBody = rootLines[i].split("=");
			String rootStr = rootBody[0];
			String rootAddrStr;
			rootAddrStr = rootBody[1];
			rootList.add(rootStr);
			String[] addrList = rootAddrStr.split(",");
			List rootAddrList = new ArrayList();
			for (int j = 0; j < addrList.length; j++) {
				String[] locStr = addrList[j].split(":");
				int absoluteAya;
				IQuranLocation loc;
				absoluteAya = Integer.parseInt(locStr[0]);
				loc = QuranPropertiesUtils.getLocation(absoluteAya + 1);
				int wordCount = Integer.parseInt(locStr[1]);
				RootAddress ra = new RootAddress(loc, wordCount);
				rootAddrList.add(ra);

				if (reverseIndex[absoluteAya] == null) {
					reverseIndex[absoluteAya] = new ListOrderedMap();
				}
				reverseIndex[absoluteAya].put(new Integer(wordCount), rootStr);
			}
			rootAddr.put(rootStr, rootAddrList);
		}
	}

	public Map getRootMap(IQuranLocation loc) {
		return reverseIndex[QuranPropertiesUtils.getAbsoluteLocation(loc)];
	}
}
