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
 * @author Mohsen Saboorian
 */
public class QuranFilterContext {
	public QuranFilterContext(String ayaText, int suraNum, int ayaNum) {
		this.text = ayaText;
		this.suraNum = suraNum;
		this.ayaNum = ayaNum;
		params = 0;
	}

	public QuranFilterContext(String ayaText, int suraNum, int ayaNum, int params) {
		this.text = ayaText;
		this.suraNum = suraNum;
		this.ayaNum = ayaNum;
		this.params = params;
	}

	/** Filter parameters. */
	public int params;

	/** Aya text. */
	public String text;

	/** Aya number, counted from 1. */
	public int ayaNum;

	/** Sura number, counted from 1. */
	public int suraNum;
}
