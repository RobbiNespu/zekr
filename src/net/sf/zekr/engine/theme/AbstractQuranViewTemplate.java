package net.sf.zekr.engine.theme;

import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.runtime.RuntimeUtilities;
import net.sf.zekr.common.util.IQuranText;
import net.sf.zekr.common.util.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;

public abstract class AbstractQuranViewTemplate extends BaseViewTemplate {
	IQuranText text;
	public AbstractQuranViewTemplate(IQuranText text) {
		this.text = text;
	}

	/**
	 * Transforms a sura view.
	 * 
	 * @param sura sura number (counted from 1)
	 * @return text to be written to a local file
	 */
	public String transform(int sura) {
		String retStr = null;
		try {
			engine.put("AYA_LIST", text.getSura(sura));
			engine.put("JUZ_LIST", QuranPropertiesUtils.getJuzInsideList(sura));
			engine.put("SAJDA_LIST", QuranPropertiesUtils.getSajdaInsideList(sura));
			engine.put("APP_PATH", RuntimeUtilities.RUNTIME_DIR.replaceAll("\\\\", "/"));
			engine.put("SURA_NUM", new Integer(sura)); // Note: suraNum is counted from 1
			engine.put("SURA_NAME", QuranProperties.getInstance().getSura(sura).getName());
			engine.put("TITLE", "");
			engine.put("LAYOUT", config.getQuranTextLayout());
			engine.put("THEME_DIR", resource.getString("theme.baseDir"));
			retStr = engine.getUpdated(resource.getString("theme.default.sura"));
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(e);
		}

		return retStr;
	}

}
