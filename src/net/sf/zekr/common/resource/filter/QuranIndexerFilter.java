/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 28, 2008
 */
package net.sf.zekr.common.resource.filter;

/**
 * This filter is used for simplifying text being indexed. It removes extra whitespaces, and signs (waqf,
 * hizb, sajda) from the text.
 * 
 * @author Mohsen Saboorian
 */
public class QuranIndexerFilter implements IQuranFilter {
	public String filter(QuranFilterContext qfc) {
		return QuranFilterUtils.filterSign(QuranFilterUtils.filterExtraWhiteSpaces(qfc.text));
	}
}
