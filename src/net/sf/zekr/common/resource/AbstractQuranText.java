/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2008
 */
package net.sf.zekr.common.resource;

public abstract class AbstractQuranText implements IQuranText {
	public String get(IQuranLocation quranLocation) {
		return get(quranLocation.getSura(), quranLocation.getAya());
	}
}
