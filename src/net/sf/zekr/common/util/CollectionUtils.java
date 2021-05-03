/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2005
 */
package net.sf.zekr.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngine;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 */
public class CollectionUtils {
	public static String getLocalizedList(int[] array) {
		StringBuffer ret = new StringBuffer();
		LanguageEngine dict = ApplicationConfig.getInstance().getLanguageEngine();
		String delim = dict.getMeaning("COMMA");
		for (int i = 0; i < array.length - 1; i++) {
			ret.append(array[i]);
			ret.append(delim).append(" ");
		}
		ret.append(array[array.length - 1]);
		return ret.toString();
	}

	public static int[] concat(int[] array1, int[] array2) {
		int[] ret = new int[array1.length + array2.length];
		int i;
		for (i = 0; i < array1.length; i++) {
			ret[i] = array1[i];
		}
		for (int j = 0; i < ret.length; j++, i++) {
			ret[i] = array2[j];
		}
		return ret;
	}

	public static String toString(Collection<?> collection, String delim) {
		StringBuffer buf = new StringBuffer();
		Iterator<?> i = collection.iterator();
		if (i.hasNext())
			buf.append(String.valueOf(i.next()));
		while (i.hasNext()) {
			buf.append(delim);
			buf.append(String.valueOf(i.next()));
		}
		return buf.toString();
	}

	/**
	 * @param col collection parameter to be returned as array
	 * @return an array of <code>collection.eachElement.toString()</code>.
	 */
	public static String[] toStringArray(Collection<?> col) {
		String[] s = new String[col.size()];
		int i = 0;
		for (Iterator<?> iter = col.iterator(); iter.hasNext(); i++) {
			Object element = iter.next();
			s[i] = element.toString();
		}
		return s;
	}

	/**
	 * @param col collection parameter to be returned as array
	 * @param methodName the method name to be called on each item. The method's signature should have no
	 *           argument, and return <code>String</code>.
	 * @return an array of <code>collection.eachElement.toString()</code>.
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	public static String[] toStringArray(Collection<?> col, String methodName) throws InvocationTargetException,
			NoSuchMethodException, IllegalAccessException {
		String[] s = new String[col.size()];
		int i = 0;
		for (Iterator<?> iter = col.iterator(); iter.hasNext(); i++) {
			Object element = iter.next();
			s[i] = (String) element.getClass().getMethod(methodName, new Class[] {}).invoke(element, new Object[] {});
		}
		return s;
	}

	@SuppressWarnings("unchecked")
	public static List fromString(String strList, String delim, Class clazz) throws InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (clazz == null)
			return Arrays.asList(strList.split(delim));

		List list = new ArrayList();
		if (!StringUtils.isBlank(strList)) {
			String[] strs = strList.split(delim);
			for (int i = 0; i < strs.length; i++) {
				list.add(clazz.getConstructor(new Class[] { String.class }).newInstance(new Object[] { strs[i].trim() }));
			}
		}
		return list;
	}

	public static Object indexOf(Collection<?> collection, String method, Object value) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
			Object elem = iter.next();
			Object res = elem.getClass().getMethod(method, new Class[] {}).invoke(elem, new Object[] {});
			if (res.equals(value))
				return elem;
		}
		return null;
	}

	public static String toSimpleJson(List<?> list) {
		return "[" + toString(list, ",") + "]";
	}

	/**
	 * @param list a list of Integer objects
	 * @return an array of int
	 */
	public static int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ((Integer) list.get(i)).intValue();
		}
		return ret;
	}

	public static <T> List<T> toArrayList(T[] objectArray) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < objectArray.length; i++) {
			list.add(objectArray[i]);
		}
		return list;
	}
}
