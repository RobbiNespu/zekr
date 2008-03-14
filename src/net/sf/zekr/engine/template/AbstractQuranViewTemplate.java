package net.sf.zekr.engine.template;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranProperties;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.ThemeData;

public abstract class AbstractQuranViewTemplate extends BaseViewTemplate {
	protected int suraNum;
	protected int ayaNum;
	protected IQuranText quran;

	public AbstractQuranViewTemplate(IQuranText quran, int suraNum, int ayaNum) {
		super();
		this.suraNum = suraNum;
		this.ayaNum = ayaNum;
		this.quran = quran;
	}

	/**
	 * Transforms a sura view (based on <code>suraNum</code> field set in constructor).
	 * 
	 * @return text to be written to a local file
	 */
	public String doTransform() {
		String retStr = null;
		ThemeData td = config.getTheme().getCurrent();
		try {
			engine.put("BISMILLAH", quran.getBismillah(suraNum));
			engine.put("JUZ_LIST", QuranPropertiesUtils.getJuzInsideSura(suraNum));
			engine.put("ALL_JUZ_LIST", QuranPropertiesUtils.getSuraJuzAsList(suraNum));
			engine.put("SAJDA_LIST", QuranPropertiesUtils.getSajdaInsideList(suraNum));
			engine.put("SURA_NUM", new Integer(suraNum)); // Note: suraNum is counted from 1
			engine.put("AYA_NUM", new Integer(ayaNum)); // Note: ayaNum is counted from 1
			engine.put("SURA_NAME", QuranProperties.getInstance().getSura(suraNum).getName());
			engine.put("GO_MSG", langEngine.getMeaning("GO"));
			engine.put("SURA_MSG", langEngine.getMeaning("SURA"));
			engine.put("AYA_MSG", langEngine.getMeaning("AYA"));
			engine.put("PREV", langEngine.getMeaning("PREVIOUS"));
			engine.put("NEXT", langEngine.getMeaning("NEXT"));
			engine.put("TITLE", langEngine.getMeaning("SURA") + ": "
					+ QuranProperties.getInstance().getSura(suraNum).getName()); // the same as SURA_NAME
			retStr = engine.getUpdated(td.getPath() + "/" + resource.getString("theme.sura"));
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		return retStr;
	}
}
