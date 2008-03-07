/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 28, 2008
 */
package net.sf.zekr.common.resource.filter;

public interface IFilter {
	/**
	 * Implementations of this method manipulates the input parameter as needed.
	 * 
	 * @param str input string to be manipulated
	 * @return manipulated string
	 */
	public String filter(FilterContext filterContext);
}
