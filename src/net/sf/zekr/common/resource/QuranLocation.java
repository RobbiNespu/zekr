/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 10, 2005
 */
package net.sf.zekr.common.resource;

import org.apache.commons.lang.StringUtils;

/**
 * This data structure is the primitive structure of addressing somewhere in the Quran. Addressing is possible
 * by just having aya number and sura number. <br>
 * Note that this class does not provide any range checking or explicit exception handling for performance
 * purposes.<br>
 * Both sura and aya numbers are counted from 1.
 * 
 * @author Mohsen Saboorian
 */
public class QuranLocation implements IQuranLocation {
	private int sura;
	private int aya;

	/**
	 * No range check is performed.
	 * 
	 * @param sura counted from 1
	 * @param aya counted from 1
	 */
	public QuranLocation(int sura, int aya) {
		setAya(aya);
		setSura(sura);
	}

	/**
	 * Loads a QuranLocation with the format <tt>sura#-aya#</tt>. Sura and Aya numbers are both counted from 1.
	 * If <code>location</code> is not of format <tt>sura#-aya#</tt>, an <code>IllegalArgumentException</code>
	 * is thrown.<br>
	 * Please note that no range check is performed for this method.
	 * 
	 * @param location Quran location to be parsed
	 * @throws IllegalArgumentException if <code>location</code> is not well-formed, ie. <tt>sura#-aya#</tt>
	 */
	public QuranLocation(String location) {
		int i = location.indexOf('-');
		if (i == -1) {
			throw new IllegalArgumentException(location);
		}
		setSura(Integer.parseInt(location.substring(0, i)));
		setAya(Integer.parseInt(location.substring(i + 1)));
	}

	/**
	 * Checks if the given QuranLocation's compliant string is valid (is of the form of sura#-aya# and the
	 * location actually exists).
	 * 
	 * @param loc the location string to be verified
	 * @return <code>true</code> if this is a valid Quran location, <code>false</code> otherwise.
	 */
	public static boolean isValidLocation(String loc) {
		IQuranLocation qloc;
		try {
			qloc = new QuranLocation(loc);
		} catch (RuntimeException e) {
			return false;
		}
		return qloc.isValid();
	}

	/**
	 * Checks if the location (sura, aya) actually exists.
	 * 
	 * @param suraNum 1-based sura number
	 * @param ayaNum 1-based aya number
	 * @return <code>true</code> if this is a valid Qur'an location, <code>false</code> otherwise.
	 */
	public static boolean isValidLocation(int suraNum, int ayaNum) {
		return new QuranLocation(suraNum, ayaNum).isValid();
	}

	public boolean isValid() {
		QuranProperties qp = QuranProperties.getInstance();
		return between(getSura(), 1, QuranPropertiesUtils.QURAN_SURA_COUNT)
				&& between(getAya(), 1, qp.getSura(getSura()).getAyaCount());
	}

	static boolean between(int num, int from, int to) {
		return num >= from && num <= to;
	}

	public final int getAya() {
		return aya;
	}

	public final void setAya(int aya) {
		this.aya = aya;
	}

	public final int getSura() {
		return sura;
	}

	public final void setSura(int sura) {
		this.sura = sura;
	}

	public String getSuraName() {
		return getSuraName(true);
	}

	public String getSuraName(boolean localize) {
		SuraProperties suraProps = QuranPropertiesUtils.getSura(sura);
		return localize ? suraProps.toText() : suraProps.getName();
	}

	public boolean isLastAya() {
		SuraProperties suraProps = QuranPropertiesUtils.getSura(sura);
		return aya >= suraProps.getAyaCount();
	}

	public boolean isLastSura() {
		return sura >= 114;
	}

	public IQuranLocation getNext() {
		IQuranLocation newLoc;
		SuraProperties sp = QuranPropertiesUtils.getSura(sura);
		if (aya < sp.getAyaCount()) {
			newLoc = new QuranLocation(sura, aya + 1);
		} else if (sura < 114) {
			newLoc = new QuranLocation(sura + 1, 1);
		} else {
			newLoc = null;
		}
		return newLoc;
	}

	public IQuranLocation getPrev() {
		IQuranLocation newLoc;
		if (aya > 1) {
			newLoc = new QuranLocation(sura, aya - 1);
		} else if (sura > 1) {
			SuraProperties sp = QuranPropertiesUtils.getSura(sura - 1);
			newLoc = new QuranLocation(sura - 1, sp.getAyaCount());
		} else {
			newLoc = null;
		}
		return newLoc;
	}

	public String toString() {
		return new StringBuffer(String.valueOf(sura)).append("-").append(aya).toString();
	}

	/**
	 * Makes a string representation of this class as: <tt>sura(sura#) - aya#</tt>
	 */
	public String toDetailedString() {
		return new StringBuffer(getSuraName()).append(" (").append(sura).append(") - ").append(aya).toString();
	}

	public String toSortableString() {
		String suraStr = StringUtils.leftPad(String.valueOf(sura), 3, '0');
		String ayaStr = StringUtils.leftPad(String.valueOf(aya), 3, '0');
		return new StringBuffer(suraStr).append("-").append(ayaStr).toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof QuranLocation) {
			QuranLocation loc = (QuranLocation) obj;
			return loc.aya == aya && loc.sura == sura;
		} else {
			return obj.equals(this);
		}
	}

	public int compareTo(IQuranLocation location) {
		IQuranLocation l = location;
		if (sura > l.getSura() || sura == l.getSura() && aya > l.getAya()) {
			return 1;
		}
		if (sura < l.getSura() || sura == l.getSura() && aya < l.getAya()) {
			return -1;
		}
		return 0;
	}

	public int getAbsoluteAya() {
		return QuranPropertiesUtils.getAggregateAyaCount(sura) + aya;
	}
}
