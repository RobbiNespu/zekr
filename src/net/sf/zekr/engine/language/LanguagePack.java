/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 23, 2005
 */
package net.sf.zekr.engine.language;

import java.io.File;

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.ResourceManager;

/**
 * A JavaBean for storing a language pack defined as an XML file in <tt>[zekr]/res/lang</tt> directory.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class LanguagePack {

	/** Java locale-like ID: en_US, fa_IR, ... */
	public String id;

	public String localizedName;

	public String file;

	public String icon;

	public String direction;

	/** English name */
	public String name;

	/** The author of the language translation pack */
	public String author;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Internationalized name of the language
	 */
	public String getName() {
		return localizedName;
	}

	public void setName(String name) {
		this.localizedName = name;
	}

	public String getLatinName() {
		return name;
	}

	public void setLatinName(String latinName) {
		this.name = latinName;
	}

	public String getIcon() {
		return id.substring(3).toLowerCase() + ".png";
	}

	/**
	 * @return language pack .XML file
	 */
	public String getPath() {
		return ApplicationPath.LANGUAGE_DIR + "/" + getFile();
	}

	/**
	 * @return language pack icon file or default icon if no icon specified
	 */
	public String getIconPath() {
		ResourceManager res = ResourceManager.getInstance();
		if (new File(res.getString("icon.flag.baseDir") + getIcon()).exists())
			return res.getString("icon.flag.baseDir") + getIcon();
		return res.getString("icon.flag.default");
	}

	public String toString() {
		return name + " (" + id + ")";
	}

}
