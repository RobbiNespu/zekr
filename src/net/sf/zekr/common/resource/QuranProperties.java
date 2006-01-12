/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2005
 */
package net.sf.zekr.common.resource;

import java.util.List;

import net.sf.zekr.common.util.IQuranProperties;
import net.sf.zekr.common.util.JozProperties;
import net.sf.zekr.common.util.SajdaProperties;
import net.sf.zekr.common.util.SooraProperties;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranProperties implements IQuranProperties {
	private static QuranProperties thisInstance;
	QuranPropertiesReader quranPropsReader;

	private QuranProperties() {
		quranPropsReader = new QuranPropertiesReader();
	}

	public static QuranProperties getInstance() {
		if (thisInstance == null)
			thisInstance = new QuranProperties();
		return thisInstance;
	}

	public final List getSooraList() {
		return quranPropsReader.sooraProp;
	}

	public final List getJozList() {
		return quranPropsReader.jozProp;
	}

	public final List getSajdaList() {
		return quranPropsReader.sajdaProp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranProperties#getSoora(int)
	 */
	public SooraProperties getSoora(int sooraNum) {
		return (SooraProperties) quranPropsReader.sooraProp.get(sooraNum - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranProperties#getJoz(int)
	 */
	public JozProperties getJoz(int jozNum) {
		return (JozProperties) quranPropsReader.jozProp.get(jozNum - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.zekr.common.util.IQuranProperties#getSajda(int)
	 */
	public SajdaProperties getSajda(int sajdaNum) {
		return (SajdaProperties) quranPropsReader.sajdaProp.get(sajdaNum - 1);
	}
}
