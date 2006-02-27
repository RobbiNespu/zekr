/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 23, 2006
 */
package net.sf.zekr.engine.theme;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.language.LanguageEngine;

public abstract class BaseViewTemplate {
	protected TemplateEngine engine = TemplateEngine.getInstance();
	protected ApplicationConfig config = ApplicationConfig.getInstance();
	protected ResourceManager resource = ResourceManager.getInstance();
	protected LanguageEngine langEngine = config.getLanguageEngine();
}
