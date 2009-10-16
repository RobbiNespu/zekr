/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 7, 2005
 */
package net.sf.zekr.common.resource;

/**
 * An immutable interface for a Quran location.
 * 
 * @author Mohsen Saboorian
 */
public interface IQuranLocation extends Comparable<IQuranLocation> {
	public int getAya();

	public int getSura();

	/**
	 * @return the sura name localized in the language and format specified in <tt>view.sura.name</tt>
	 *         property.
	 */
	public String getSuraName();

	/**
	 * @param localize specifies whether or not to localize sura name
	 * @return the sura name either in Arabic or the language and format specified in <tt>view.sura.name</tt>
	 *         property (if <code>localize</code> parameter is true).
	 */
	public String getSuraName(boolean localize);

	/**
	 * Counts the aya number from the start of the Quran. The value can be a number between 1 to 6236.
	 * 
	 * @return the absolute aya number counted from the start of the Quran.
	 */
	public int getAbsoluteAya();

	/**
	 * Returns the next location (sura-aya pair).
	 * 
	 * @return the next Quran location, or <code>null</code> if there is nothing
	 */
	public IQuranLocation getNext();

	/**
	 * Returns the previous location (sura-aya pair).
	 * 
	 * @return the previous Quran location, or <code>null</code> if there is nothing
	 */
	public IQuranLocation getPrev();

	/**
	 * Pads the sura/aya number so that all strings are of the same width and sortable.
	 * 
	 * @return a sortable representation of the quran location.
	 */
	public String toSortableString();

	public boolean isValid();

	/**
	 * Makes a string representation of this class as: <tt>sura#-aya#</tt>
	 * 
	 * @return a string representation of the Quran location in the format <tt>sura#-aya#</tt>.
	 */
	public String toString();

	public boolean isLastAya();

	public boolean isLastSura();
}
