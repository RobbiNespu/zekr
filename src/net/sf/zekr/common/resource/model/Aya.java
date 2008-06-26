/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Apr 29, 2008
 */
package net.sf.zekr.common.resource.model;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SajdaProperties;

public class Aya {
	private IQuranLocation location = null;
	private SajdaProperties sajda = null;
	private JuzProperties juz = null;
	private int hizbQuarter = -1;

	public Aya(IQuranLocation location) {
		this.location = location;
		this.sajda = QuranPropertiesUtils.getSajda(location);

		JuzProperties jp = QuranPropertiesUtils.getJuzOf(location);
		this.juz = jp.getLocation().equals(location) ? jp : null;

		QuranLocation[] hq = juz == null ? null : juz.getHizbQuarters();
		for (int i = 0; i < hq.length; i++) {
			if (location.equals(hq)) {
				hizbQuarter = i;
				break;
			}
		}
	}

	public boolean hasJuz() {
		return juz != null;
	}

	public boolean hasHizb() {
		return hizbQuarter != -1;
	}

	public boolean hasSajda() {
		return sajda != null;
	}

	public SajdaProperties getSajda() {
		return sajda;
	}

	public JuzProperties getJuz() {
		return juz;
	}

	public int getHizbQuarter() {
		return hizbQuarter;
	}

	public IQuranLocation getLocation() {
		return location;
	}
}
