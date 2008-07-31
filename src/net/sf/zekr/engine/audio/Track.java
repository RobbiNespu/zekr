/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 3, 2007
 */
package net.sf.zekr.engine.audio;

import net.sf.zekr.common.resource.IQuranLocation;

/**
 * @author Mohsen Saboorian
 */
public class Track {
	private IQuranLocation location;
	private String url;

	public Track(IQuranLocation location, String url) {
		this.location = location;
		this.url = url;
	}

	public IQuranLocation getLocation() {
		return location;
	}

	public void setLocation(IQuranLocation location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
