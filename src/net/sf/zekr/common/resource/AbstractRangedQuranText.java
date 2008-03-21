/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.common.resource;

import net.sf.zekr.engine.search.SearchScope;

/**
 * @author Mohsen Saboorian
 */
public abstract class AbstractRangedQuranText extends AbstractQuranText {
	/**
	 * Resets the Quran location, setting the pointer to the start of the text. It is called on object creation
	 * to find the start location point.
	 */
	public abstract void reset();

	/**
	 * Returns the current Quran location.
	 * 
	 * @return the current Quran location.
	 */
	public abstract IQuranLocation getCurrentLocation();

	/**
	 * Returns SearchScope object for this ranged text.
	 * 
	 * @return underling <code>SearchScope</code> object
	 */
	public abstract SearchScope getSearchScope();

	/**
	 * Returns the current aya.
	 * 
	 * @return the current aya, of null if there is no more matched aya.
	 */
	public abstract String currentAya();

	/**
	 * Finds the next aya matching range restrictions (after applying scopes). A call to
	 * <code>currentAya()</code>() will return the found aya.
	 * 
	 * @return <code>true</code> if next aya found, or <code>false</code> if no more aya.
	 */
	public abstract boolean findNext();
}
