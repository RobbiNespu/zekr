/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 24, 2008
 */
package net.sf.zekr.common.resource;

public class QuranPage implements IQuranPage {
	private int index;
	private IQuranLocation from;
	private IQuranLocation to;

	/* (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranPage#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranPage#getFrom()
	 */
	public IQuranLocation getFrom() {
		return from;
	}

	public void setFrom(IQuranLocation from) {
		this.from = from;
	}

	/* (non-Javadoc)
	 * @see net.sf.zekr.common.resource.IQuranPage#getTo()
	 */
	public IQuranLocation getTo() {
		return to;
	}

	public void setTo(IQuranLocation to) {
		this.to = to;
	}
}
