package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.model.Page;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.theme.ThemeData;

/**
 * Abstract paged view template.
 * 
 * @author Mohsen Saboorian
 */
public class AbstractPageViewTemplate extends BaseViewTemplate {
	protected IQuranPage quranPage;
	protected IQuranText quran, trans;
	protected IUserView userView;

	public AbstractPageViewTemplate(IQuranText quran, IQuranText trans, IUserView userView) {
		super();
		this.quran = quran;
		this.trans = trans;
		this.userView = userView;

		this.quranPage = config.getQuranPaging().getDefault().getQuranPage(userView.getPage());
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
			// Page page = new Page(quranPage);
			// engine.put("PAGE_MODE", config.getPagingMode());
			// engine.put("PAGE", page);
			// engine.put("AYA_LIST", page.getAyaList().toArray());
			// engine.put("QURAN", quran);
			engine.put("TITLE", langEngine.getMeaning("PAGE") + ": " + quranPage.getPageNum());
			engine.put("SURA_NUM", new Integer(userView.getLocation().getSura()));
			engine.put("AYA_NUM", new Integer(userView.getLocation().getAya()));
			engine.put("PAGE_NUM", new Integer(userView.getPage()));
			engine.put("AYA_VIEW", resource.getString("theme.page"));
			retStr = engine.getUpdated(td.getPath() + "/" + resource.getString("theme.sura"));
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		return retStr;
	}

	//	public String doTransform() {
	//		String retStr = null;
	//		ThemeData td = config.getTheme().getCurrent();
	//		try {
	//			engine.put("PAGE_MODE", config.getProps().getString("view.pagingMode"));
	//			engine.put("BISMILLAH", quran.getBismillah(suraNum));
	//			engine.put("JUZ_LIST", QuranPropertiesUtils.getJuzInsideSura(suraNum));
	//			engine.put("ALL_JUZ_LIST", QuranPropertiesUtils.getSuraJuzAsList(suraNum));
	//			engine.put("SAJDA_LIST", QuranPropertiesUtils.getSajdaInsideList(suraNum));
	//			engine.put("SURA_NUM", new Integer(suraNum)); // Note: suraNum is counted from 1
	//			engine.put("AYA_NUM", new Integer(ayaNum)); // Note: ayaNum is counted from 1
	//			engine.put("SURA_NAME", QuranProperties.getInstance().getSura(suraNum).getName());
	//			engine.put("TITLE", langEngine.getMeaning("SURA") + ": "
	//					+ QuranProperties.getInstance().getSura(suraNum).getName()); // the same as SURA_NAME
	//			engine.put("AYA_VIEW", resource.getString("theme.aya"));
	//			retStr = engine.getUpdated(td.getPath() + "/" + resource.getString("theme.sura"));
	//		} catch (Exception e) {
	//			Logger.getLogger(this.getClass()).log(e);
	//		}
	//		return retStr;
	//	}

}
