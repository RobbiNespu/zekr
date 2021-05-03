/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 23, 2006
 */
package net.sf.zekr.engine.template;

import java.util.HashMap;
import java.util.Map;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.common.util.UriUtils;
import net.sf.zekr.common.util.VelocityUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.server.HttpServer;
import net.sf.zekr.engine.server.HttpServerRuntimeException;
import net.sf.zekr.engine.theme.ThemeData;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public abstract class BaseViewTemplate implements ITransformer {
	protected final Logger logger = Logger.getLogger(BaseViewTemplate.class);

	protected TemplateEngine engine = TemplateEngine.getInstance();
	protected static ApplicationConfig config = ApplicationConfig.getInstance();
	protected static ResourceManager resource = ResourceManager.getInstance();
	protected LanguageEngine langEngine = config.getLanguageEngine();

	private Map<String, Object> props = new HashMap<String, Object>();

	/**
	 * This method will generate the result string of a view.
	 * 
	 * @return the String representation of a view
	 * @throws TemplateTransformationException
	 */
	public String transform() throws TemplateTransformationException {
		engine.putAll(props);
		return doTransform();
	}

	public abstract String doTransform() throws TemplateTransformationException;

	public void setProperty(String key, Object value) {
		props.put(key, value);
	}

	/**
	 * Will put some initial properties into context (and some other):
	 * <ul>
	 * <li><tt>DIRECTION</tt>: "rtl" or "ltr" based on the current language pack</li>
	 * <li><tt>TRANS_DIRECTION</tt>: "rtl" or "ltr" based on the current translation</li>
	 * <li><tt>TRANS_LANG</tt>: 2-char language code, e.g. "fa" for Farsi, "en" for English, ...</li>
	 * <li><tt>APP_PATH</tt>: an absolute URI ending with slash</li>
	 * <li><tt>CSS_DIR</tt>: an absolute URI ending with slash</li>
	 * <li><tt>UI_DIR</tt>: relative UI directory path</li>
	 * </ul>
	 * It will also put all processed properties related to the current theme (<code>ThemeData.processedProps</code>).
	 */
	protected BaseViewTemplate() {
		ThemeData td = config.getTheme().getCurrent();
		engine.put("DICT", langEngine);
		engine.put("DIRECTION", langEngine.getDirection());
		if (config.getTranslation().getDefault() != null) {
			engine.put("TRANS_DIRECTION", config.getTranslation().getDefault().direction);
			engine.put("TRANS_LANG", config.getTranslation().getDefault().locale.getLanguage());
		}

		String serverUrl = UriUtils.toUri(GlobalConfig.RUNTIME_DIR);
		try {
			if (config.isHttpServerEnabled()) {
				serverUrl = config.getHttpServer().getUrl();
			}
		} catch (HttpServerRuntimeException e) {
			logger.error(e);
			serverUrl = "http://127.0.0.1:" + config.getProps().getInt("server.http.port") + "/";
		}
		String appPath = serverUrl;
		String cssDir = config.isHttpServerEnabled() ? HttpServer.CACHED_RESOURCE + "/" : UriUtils
				.toUri(Naming.getViewCacheDir());

		engine.put("VIEW_LAYOUT", config.getViewLayout());
		engine.put("APP_PATH", appPath);
		engine.put("APP_VERSION", GlobalConfig.ZEKR_VERSION);
		engine.put("UI_DIR", ApplicationPath.UI_DIR);
		engine.put("CSS_DIR", cssDir);
		engine.put("THEME_DIR", td.getPath());
		engine.put("UTILS", new VelocityUtils());
		engine.put("I18N", new I18N(langEngine.getLocale()));
		engine.put("RES", resource);

		engine.putAll(td.processedProps);
	}
}
