/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2007
 */

package net.sf.zekr.common.util;

/**
 * This class contains some helper methods to be used in velocity templates. This is a replacement for
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
		return new Integer(num.toString());
	}

	public Integer round(Object num) {
		Number n = toDouble(num);
		return new Integer((int) Math.rint(n.doubleValue()));
	}

	public int arraySize(Object arr) {
		return ((Object[]) arr).length;
	}
}