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

import org.apache.commons.lang.StringUtils;

/**
 * Data structure to hold Quran word root and their addresses.
 * 
 * @author Mohsen Saboorian
 */
public class QuranRoot {
	private static final int ROOT_LIST_SIZE = 1918;
	List<String> rootList = new ArrayList<String>(ROOT_LIST_SIZE);
	Map<String, List<RootAddress>> rootAddr = new HashMap<String, List<RootAddress>>(ROOT_LIST_SIZE);

	/*
	 * An array of all ayas of the Quran. Each entry is a {@link List} of root of each word in that aya,
	 * exactly at the location at which the word exists in that aya.
	 */
	// OrderedMap[] reverseIndex = new OrderedMap[QuranPropertiesUtils.QURAN_AYA_COUNT];

	public QuranRoot(String rawRootText) {
		parse(rawRootText);
	}

	/**
	 * Parse the raw root text db.
	 * 
	 * @param rawRootText
	 */
	private void parse(String rawRootText) {
		String[] rootLines = StringUtils.split(rawRootText, '\n');
		for (int i = 0; i < ROOT_LIST_SIZE; i++) {
			String[] rootBody = StringUtils.split(rootLines[i], '\t');
			String rootStr = rootBody[0];
			String rootAddrStr;
			// rootrootBody[1] is frequency, not really needed!
			rootAddrStr = rootBody[2];
			rootList.add(rootStr);
			String[] addrList = StringUtils.split(rootAddrStr, ";"); // aya separator
			List<RootAddress> rootAddrList = new ArrayList<RootAddress>();
			RootAddress ra = null;
			for (int j = 0; j < addrList.length; j++) {
				String[] locStr = StringUtils.split(addrList[j], ':');
				String[] wordIndex = StringUtils.split(locStr[1], ',');
				int absoluteAya = Integer.parseInt(locStr[0]);
				IQuranLocation loc = QuranPropertiesUtils.getLocation(absoluteAya + 1);
				try{
				for (int k = 0; k < wordIndex.length; k++) {
					ra = new RootAddress(loc, Integer.parseInt(wordIndex[k].replace("\r", "")));
					rootAddrList.add(ra);
				}
				}catch(Exception e){
					e.printStackTrace();
				}
				// TODO: load it for next release.
				// if (reverseIndex[absoluteAya] == null) {
				// reverseIndex[absoluteAya] = new ListOrderedMap();
				// }
				// reverseIndex[absoluteAya].put(new Integer(wordIndex), rootStr);
			}
			rootAddr.put(rootStr, rootAddrList);
		}
	}

	//	public Map getRootMap(IQuranLocation loc) {
	//		return reverseIndex[QuranPropertiesUtils.getAbsoluteLocation(loc)];
	//	}

	public List<String> getRootList() {
		return rootList;
	}

	public List<RootAddress> getRootAddress(String rootStr) {
		return rootAddr.get(rootStr);
	}
}
