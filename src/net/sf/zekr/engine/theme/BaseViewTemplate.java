/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 23, 2006
 */
package net.sf.zekr.engine.theme;

import java.io.File;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.language.LanguageEngine;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public abstract class BaseViewTemplate {
	protected TemplateEngine engine = TemplateEngine.getInstance();
	protected ApplicationConfig config = ApplicationConfig.getInstance();
	protected ResourceManager resource = ResourceManager.getInstance();
	protected LanguageEngine langEngine = config.getLanguageEngine();

	/**
	 * Will put some initial properties into context:
	 * <ul>
	 * <li>DIRECTION: rtl or ltr based on current language pack</li>
	 * <li>APP_PATH: a URI eding with slash</li>
	 * <li>CSS_DIR: a URI ending with slash</li>
	 * <li>UI_DIR: relative ui directory path</li>
	 * </ul>
	 */
	protected BaseViewTemplate() {
		ThemeData td = config.getTheme().getCurrent();
		engine.put("DIRECTION", langEngine.getDirection());
		engine.put("TRANS_DIRECTION", config.getTranslation().getDefault().direction);
		engine.put("APP_PATH", new File(GlobalConfig.RUNTIME_DIR).toURI());
		engine.putAll(td.processedProps);
		engine.put("THEME_DIR", td.getPath());
		engine.put("UI_DIR", ApplicationPath.UI_DIR);
		engine.put("CSS_DIR", new File(Naming.CACHE_DIR).toURI());
	}
}
