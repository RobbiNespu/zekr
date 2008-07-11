/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2007
 */

package net.sf.zekr.common.util;

import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.audio.IPlaylistProvider;

/**
 * This class contains some helper methods to be used in Velocity templates. This is a replacement for
 * velocity-tools-generic library.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class VelocityUtils {
	public double mul(Object num1, Object num2) {
		Number n1 = toDouble(num1);
		Number n2 = toDouble(num2);

		double value = n1.doubleValue() * n2.doubleValue();
		return value;
	}

	public int intAdd(Object num1, Object num2) {
		Number n1 = toInteger(num1);
		Number n2 = toInteger(num2);

		return n1.intValue() + n2.intValue();
	}

	public double div1(Object num1, Object num2) {
		Number n1 = toDouble(num1);
		Number n2 = toDouble(num2);

		double value = n1.doubleValue() / n2.doubleValue();
		return ((int) (value * 10)) / 10.0;
	}

	public Number toDouble(Object num) {
		return Double.valueOf(num.toString());
	}

	public Integer toInteger(Object num) {
		return new Integer(toDouble(num).intValue());
	}

	public Integer round(Object num) {
		Number n = toDouble(num);
		return new Integer((int) Math.rint(n.doubleValue()));
	}

	public int arraySize(Object arr) {
		return arr == null ? -1 : ((Object[]) arr).length;
	}

	public String items2JsArray(IPlaylistProvider pp, int sura) {
		int ayaCount = QuranPropertiesUtils.getSura(sura).getAyaCount();
		StringBuffer buf = new StringBuffer("[");
		if (ayaCount > 0) // always true :-)
			buf.append(pp.getItem(sura, 1));
		for (int aya = 2; aya <= ayaCount; aya++) {
			buf.append(", " + pp.getItem(sura, aya));
		}
		buf.append("]");
		return buf.toString();
	}
	
	public Object getItem(Object arr, int index) {
		return ((Object[]) arr)[index];
	}
}