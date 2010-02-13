/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 24, 2008
 */
package net.sf.zekr.engine.page;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;

/**
 * A single page of the Holy Quran.
 * 
 * @author Mohsen Saboorian
 */
public class QuranPage implements IQuranPage {
	private int index;
	private IQuranLocation from;
	private IQuranLocation to;

	public int getPageNum() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public IQuranLocation getFrom() {
		return from;
	}

	public void setFrom(IQuranLocation from) {
		this.from = from;
	}

	public IQuranLocation getTo() {
		return to;
	}

	public void setTo(IQuranLocation to) {
		this.to = to;
	}

	public String toString() {
		return "[" + getPageNum() + ": " + getFrom() + " " + getTo() + "]";
	}
}
