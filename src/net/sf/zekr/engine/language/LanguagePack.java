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
 * A JavaBean for storing a <tt>pack</tt> node defined <tt>zekr-config.xml</tt>.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.2
 */
public class LanguagePack {
	String id;
	String name;
	String file;
	String icon;
	String latinName;

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
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLatinName() {
		return latinName;
	}

	public void setLatinName(String latinName) {
		this.latinName = latinName;
	}
	
	public String getIcon() {
		return id.substring(3).toLowerCase() + ".png";
	}

	/**
	 * @return language pack xml file
	 */
	public String getPath() {
		return ApplicationPath.LANGUAGE_DIR + getFile();
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
		return latinName + "(" + id + ")";
	}

}
