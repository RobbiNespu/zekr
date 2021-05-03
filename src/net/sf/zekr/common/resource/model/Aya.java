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
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SajdaProperties;

public class Aya {
	private IQuranLocation location = null;
	private SajdaProperties sajda = null;
	private JuzProperties juz = null;
	private int hizbQuarter = -1;
	private IQuranText quranText = null;
	private String text = null;
	private String bismillah = null;
	private boolean hasJuz;

	public Aya(IQuranText quranText, IQuranLocation location) {
		this.location = location;
		this.quranText = quranText;
		this.sajda = QuranPropertiesUtils.getSajda(location);
		this.text = quranText.get(location);
		this.bismillah = quranText.getBismillah(location.getSura());

		this.juz = QuranPropertiesUtils.getJuzOf(location);
		this.hasJuz = juz.getLocation().equals(location) ? true : false;

		IQuranLocation[] hq = juz.getHizbQuarters();
		for (int i = 0; i < hq.length; i++) {
			if (location.equals(hq[i])) {
				hizbQuarter = i;
				break;
			}
		}
	}

	public boolean hasJuz() {
		return hasJuz;
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

	public IQuranText getQuranText() {
		return quranText;
	}

	public String getBismillah() {
		return bismillah;
	}

	public String getText() {
		return text;
	}
}
