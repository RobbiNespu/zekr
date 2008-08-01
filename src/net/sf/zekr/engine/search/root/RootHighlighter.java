/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.engine.search.root;

import java.util.List;

import net.sf.zekr.common.resource.filter.QuranFilterUtils;
import net.sf.zekr.common.util.StringUtils;
import net.sf.zekr.common.util.StringUtils.Region;
import net.sf.zekr.engine.search.SearchException;

/**
 * @author Mohsen Saboorian
 */
class RootHighlighter {
	public String highlight(String aya, List wordIndexList) {
		StringBuffer buf = new StringBuffer(aya);
		int addition = 0;
		for (int i = 0; i < wordIndexList.size(); i++) {
			int index = ((Integer) wordIndexList.get(i)).intValue();
			Region r = StringUtils.getNthRegion(aya, index, ' ');
			if (r == null) {
				throw new SearchException("Highlight error: unexpected null region.");
			}
			buf.replace(addition + r.from, addition + r.to, "<span class=\"highlight\">" + aya.substring(r.from, r.to)
					+ "</span>");
			addition += 31; // 31 is the size of highlighter code added to buf
		}
		return buf.toString();
	}
}
