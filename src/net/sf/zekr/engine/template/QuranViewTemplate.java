/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Dec 28, 2004
 */

package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.runtime.RuntimeUtilities;
import net.sf.zekr.common.util.I18n;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 * @version 0.1
 */
public class QuranViewTemplate implements QuranViewTemplateNaming {
	/**
	 * Transforms a sura view.
	 * 
	 * @param sura sura number (counted from 1)
	 * @param aya aya number (counted from 1)
	 * @return text to be written to a local file
	 */
	public String transform(int sura) {
		ApplicationConfig config = ApplicationConfig.getInsatnce();
		String retStr = null;

		try {
			TemplateEngine engine = TemplateEngine.getInstance();
			QuranText quran = QuranText.getInstance();
			engine.put("AYA_LIST", quran.getSura(sura));
			engine.put("JUZ_LIST", QuranPropertiesUtils.getJuzInsideList(sura));
			engine.put("SAJDA_LIST", QuranPropertiesUtils.getSajdaInsideList(sura));
			engine.put("APP_PATH", RuntimeUtilities.RUNTIME_DIR.replaceAll("\\\\", "/"));
			engine.put("SURA_NUM", new Integer(sura)); // Note: suraNum is counted from 1
			engine.put("SURA_NAME", QuranProperties.getInstance().getSura(sura).getName());
			engine.put("TITLE", "");
			engine.put("LAYOUT", config.getQuranTextLayout());
			retStr = engine.getUpdated(ApplicationPath.SURA_VIEW_TEMPLATE);
		} catch (Exception e) {
			Logger.getLogger(QuranViewTemplate.class).log(e);
		}

		return retStr;
	}
}
