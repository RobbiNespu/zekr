/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 29, 2005
 */
package net.sf.zekr.engine.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.QuranTextProperties;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranLocation;

public class SearchQuran {
	public static final int MAX_SEARCH_RESULT = 200; // TODO: move this to xml file

	IQuranText quran;

	public SearchQuran(IQuranText quran) {
		this.quran = quran;
	}

	public static List find(String src, String keyword) {
		int i = src.indexOf(keyword);
		if (i == -1)
			return null;

		List ret = new ArrayList(2);
		while (i != -1) {
			ret.add(Integer.valueOf(i));
			i = src.indexOf(keyword, i + 1);
		}

		return ret;
	}

	public Map findAll(String keyword) {
		Map ret = new LinkedHashMap();
		String aya;
		int ayaNum;
		List l;
		for (int i = 1; i <= 114; i++) {
			ayaNum = quran.getSoora(i).length;
			for (int j = 1; j <= ayaNum; j++) { 
				aya = quran.get(i, j);
				if ((l = find(aya, keyword)) != null) {
					ret.put(SearchUtils.getKey(i, j), l);
					if (ret.size() >= MAX_SEARCH_RESULT)
						return ret;
				}
			}
		}

		return ret;
	}

	
	/**
	 * This is a test case
	 */
	public static void main(String[] args) {
		try {
//			IQuranText qt = QuranText.getInstance();
//			SearchQuran sq = new SearchQuran(qt);
//			Map m = sq.findAll("Öó");
//			System.out.println(m);
			Map m = new LinkedHashMap();
			m.put("Mohsen", "saboorian");
			m.put("Ali", "Alavi");
			m.put("Mazaher", "Tahmasbi");
			m.put("Homayoun", "Ghadami");
			m.put("Mahsan", "Saharzade");
			m.put("gholeidoon", "garian");
			
//			for (Iterator iter = m.entrySet().iterator(); iter.hasNext();) {
			Set set = m.entrySet();
			for (int j = 0; j < set.size(); j++) {
				System.out.println(set.toArray()[j]);
			}				
//				Entry ent = (Entry) iter.next();
//				String key = (String) ent.getKey();
//				String value = (String) ent.getValue();
//				System.out.println(value);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
