/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 20, 2005
 */
package net.sf.zekr.common.runtime;

import net.sf.zekr.common.config.ApplicationConfig;

/**
 * <p>
 * A java bean class intended to hold runtime configurations (those which can be changed at runtime). An
 * instance of this class can be accessed through <code>ApplicationConfig</code>.
 * </p>
 * <p>
 * The philosophy of existence of this file is that retrieving application configurations from
 * <code>ApplicationConfig</code> is a time consuming job since it loads the configuration from a heavyweight
 * DOM (level 2) XML object in the memory.
 * </p>
 * 
 * @author Mohsen Saboorian
 * @deprecated use {@link ApplicationConfig} instead. This class will be deleted before final 0.7.0 release.
 */
public class RuntimeConfig {
	String language;
	String textLayout;

	/**
	 * @return Returns the language.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language The language to set.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return Returns the textLayout.
	 */
	public String getTextLayout() {
		return textLayout;
	}

	/**
	 * @param textLayout The textLayout to set.
	 */
	public void setTextLayout(String textLayout) {
		this.textLayout = textLayout;
	}
}
