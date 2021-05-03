/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 27, 2008
 */
package net.sf.zekr.ui;

import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.zekr.engine.log.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * This class is to be used for default GUI properties. In case that Zekr is about to port to a system with
 * special GUI configs, it should suffice to change <tt>zekr-gui.properties</tt> file.
 * 
 * @author Mohsen Saboorian
 */
public class GlobalGuiConfig {
	final private static Logger logger = Logger.getLogger(GlobalGuiConfig.class);

	public static final int BUTTON_WIDTH;

	static {
		PropertiesConfiguration prop = new PropertiesConfiguration();
		try {
			InputStream is = GlobalGuiConfig.class.getResourceAsStream("zekr-gui.properties");
			prop.load(new InputStreamReader(is));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BUTTON_WIDTH = prop.getInt("zekr.gui.button.width", 80);
		}
	}

}
