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

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.util.StringUtils;
import net.sf.zekr.common.util.StringUtils.Region;
import net.sf.zekr.engine.search.IZekrHighlighter;
import net.sf.zekr.engine.search.SearchException;

/**
 * @author Mohsen Saboorian
 */
class RootHighlighter implements IZekrHighlighter {
	public String highlight(String aya, List<Integer> wordIndexList) {
		StringBuffer buf = new StringBuffer(aya);
		int addition = 0;
		for (int i = 0; i < wordIndexList.size(); i++) {
			int index = wordIndexList.get(i).intValue();
			Region r = StringUtils.getNthRegion(aya, index, ' ');
			if (r == null) {
				throw new SearchException("Highlight error: unexpected null region.");
			}
			String substring = aya.substring(r.from, r.to);

			String format = FORMAT_STRING;
			try {
				format = ApplicationConfig.getInstance().getProps().getString("view.search.highlightFormat", FORMAT_STRING);
			} catch (Exception e) {
				// silently ignore it.
			}

			buf.replace(addition + r.from, addition + r.to, String.format(format, substring));
			//buf.replace(addition + r.from, addition + r.to, "<span class=\"highlight\">" + substring
			// + "</span>");

			addition += 31; // 31 is the size of highlighter code added to buf
		}
		return buf.toString();
	}
}
