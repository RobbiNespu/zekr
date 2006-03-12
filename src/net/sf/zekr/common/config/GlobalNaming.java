/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 11, 2005
 */
package net.sf.zekr.common.config;

/**
 * @author    Mohsen Saboorian
 * @since	  Zekr 1.0
 * @version   0.1
 */
public class GlobalNaming {
	
	public static String getIcon(String iconName) {
		return ApplicationPath.ICON_DIR + iconName;
	}
	
	public static String getImage(String imgName) {
		return ApplicationPath.IMAGE_DIR + imgName;
	}
	
	public static String getLanguagePack(String langId) {
		return ApplicationPath.LANGUAGE_DIR + langId;	
	}
	
	public static String getTranslation(String langId, String transId) {
		return ApplicationPath.TRANSLATION_DIR;
	}

}
