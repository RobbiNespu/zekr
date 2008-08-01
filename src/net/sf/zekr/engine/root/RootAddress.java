/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.engine.root;

import net.sf.zekr.common.resource.IQuranLocation;

/**
 * Data model containing a root reference in an {@link IQuranLocation}.
 * 
 * @author Mohsen Saboorian
 */
public class RootAddress {
	public IQuranLocation loc;
	public int wordIndex;

	public RootAddress(IQuranLocation loc, int wordIndex) {
		this.loc = loc;
		this.wordIndex = wordIndex;
	}
}
