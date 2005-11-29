/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 17, 2004
 */

package net.sf.zekr.ui;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.language.LanguageEngineNaming;

import org.eclipse.swt.SWT;

/**
 * @author    Mohsen Saboorian
 * @since	  Zekr 1.0
 * @see       TODO
 * @version   0.1
 */
public class PropertyGenerator {

	private ApplicationConfig config;
	private LanguageEngine langEngine;

	public PropertyGenerator(ApplicationConfig config) {
		this.config = config;		
		langEngine = config.getLanguageEngine();
	}
	
	public int getWidgetStyle() {
		int style = SWT.NONE;
		if(langEngine.getDirection().equals(LanguageEngineNaming.RIGHT_TO_LEFT))
			style |= SWT.RIGHT_TO_LEFT;
		return style;
		
	}

}