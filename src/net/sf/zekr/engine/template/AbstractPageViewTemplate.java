package net.sf.zekr.engine.template;

import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
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

	public AbstractPageViewTemplate(IQuranText quran, IUserView userView) {
		this(quran, null, userView);
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
			engine.put("PAGE_MODE", config.getPagingMode());
			engine.put("TITLE", langEngine.getMeaning("PAGE") + ": " + quranPage.getPageNum());
			engine.put("SURA_NUM", new Integer(userView.getLocation().getSura()));
			engine.put("AYA_NUM", new Integer(userView.getLocation().getAya()));
			engine.put("AYA_NUM_IN_PAGE", new Integer(1 + QuranPropertiesUtils.diff(userView.getLocation(), quranPage
					.getFrom())));
			engine.put("PAGE_NUM", new Integer(userView.getPage()));
			engine.put("AYA_COUNT", new Integer(quranPage.getTo().getAbsoluteAya() - quranPage.getFrom().getAbsoluteAya()
					+ 1));
			engine.put("AYA_VIEW", resource.getString("theme.pageItem"));
			retStr = engine.getUpdated(td.getPath() + "/" + resource.getString("theme.page"));
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).log(e);
		}
		return retStr;
	}
}
