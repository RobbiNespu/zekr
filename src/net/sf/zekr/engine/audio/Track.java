/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import net.sf.zekr.common.resource.SuraProperties;

public class Track {
	private SuraProperties sura;
	private int aya;
	private String url;

	public Track(SuraProperties sura, int aya, String url) {
		this.sura = sura;
		this.aya = aya;
		this.url = url;
	}

	public int getAya() {
		return aya;
	}

	public void setAya(int aya) {
		this.aya = aya;
	}

	public SuraProperties getSura() {
		return sura;
	}

	public void setSura(SuraProperties sura) {
		this.sura = sura;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
