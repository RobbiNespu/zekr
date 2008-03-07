/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 7, 2005
 */
package net.sf.zekr.common.resource;

public interface IQuranLocation {
	public int getAya();

	public void setAya(int aya);

	public int getSura();

	public void setSura(int sura);

	public String getSuraName();

	/**
	 * Returns the next location (sura-aya pair).
	 * 
	 * @return the next Quran location, or <code>null</code> if there is nothing
	 */
	public IQuranLocation getNext();

	/**
	 * Pads the sura/aya number so that all strings are of the same width and sortable.
	 * 
	 * @return
	 */
	public String toSortableString();
}
