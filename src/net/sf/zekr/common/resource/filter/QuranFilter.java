/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 28, 2008
 */
package net.sf.zekr.common.resource.filter;

public class QuranFilter implements IFilter {

	public QuranFilter() {
	}

	public String filter(FilterContext fc) {
		QuranFilterContext qfc = (QuranFilterContext) fc;
		if (qfc.ayaNum == 1 && qfc.suraNum != 1 && qfc.suraNum != 9) {
			int sp = 0;
			for (int i = 0; i < 4; i++) { // ignore 4 whitespaces.
				sp = qfc.text.indexOf(' ', sp);
			}
			return qfc.text.substring(sp + 1);
		}
		return qfc.text;
	}

}
