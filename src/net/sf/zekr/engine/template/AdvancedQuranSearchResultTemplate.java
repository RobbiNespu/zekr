/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 25, 2007
 */
package net.sf.zekr.engine.template;

import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.filter.IQuranFilter;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.theme.ThemeData;
import net.sf.zekr.engine.translation.TranslationData;

/**
 * @author Mohsen Saboorian
 */
public class AdvancedQuranSearchResultTemplate extends BaseViewTemplate {
   protected SearchResultModel searchResult;
   protected I18N i18n = new I18N(langEngine.getLocale());
   protected String keyword;
   int pageNo;

   /**
    * @param qts QuranTextSearcher instance
    * @param pageNo counted from 1
    */
   public AdvancedQuranSearchResultTemplate(SearchResultModel searchResult, int pageNo) {
      this.searchResult = searchResult;
      this.pageNo = pageNo;

      engine.put("THISISSEARCH", Boolean.TRUE);
      engine.put("ICON_TRANSLATE", resource.getString("icon.translate"));
   }

   public String doTransform() throws TemplateTransformationException {
      try {
         String ret = null;
         /*@fmt:off*/
         engine.put("COUNT", langEngine.getDynamicMeaning("SEARCH_RESULT_COUNT",
                              new String[] { i18n.localize(String.valueOf(searchResult.getTotalMatch())),
                                             i18n.localize(String.valueOf(searchResult.getResultCount())) }));
         /*@fmt:on*/
         engine.put("AYA_LIST", searchResult.getPage(pageNo));
         engine.put("PAGE_START_NUM", new Integer(pageNo * searchResult.getMaxResultPerPage()));
         engine.put(
               "PAGE_NUM_MSG",
               langEngine.getDynamicMeaning("SEARCH_PAGE", new String[] { i18n.localize(String.valueOf(pageNo + 1)),
                     i18n.localize(String.valueOf(searchResult.getResultPageCount())) }));
         engine.put("CLAUSE", searchResult.getClause());
         // String k = SearchUtils.arabicSimplify(searchResult.getRawQuery());
         // engine.put("TITLE", langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { k }));
         engine.put("TITLE",
               langEngine.getDynamicMeaning("SEARCH_RESULT_TITLE", new String[] { searchResult.getRawQuery() }));

         IQuranText iqt = searchResult.getQuranText();
         if (iqt instanceof TranslationData) {
            TranslationData td = (TranslationData) iqt;
            engine.put("TDATA", new FilteredQuranText(IQuranText.SIMPLE_MODE, IQuranFilter.HIGHLIGHT_WAQF_SIGN));
            engine.put("TRANSLATE", langEngine.getMeaning("QURAN"));
            engine.put("TRANSLATION", "true");
            engine.put("TRANS_DIRECTION", td.direction);
            if ("rtl".equalsIgnoreCase(td.direction)) {
               engine.put("ICON_PLAY", resource.getString("theme.icon.playRtl"));
            } else {
               engine.put("ICON_PLAY", resource.getString("theme.icon.play"));
            }
         } else { // is Quran
            engine.put("ICON_PLAY", resource.getString("theme.icon.playRtl"));
            engine.put("TRANSLATE", langEngine.getMeaning("TRANSLATION"));
            engine.put("TDATA", config.getTranslation().getDefault());
         }

         ThemeData theme = config.getTheme().getCurrent();
         ret = engine.getUpdated(theme.getPath() + "/" + resource.getString("theme.search.result"));
         return ret;
      } catch (Exception e) {
         throw new TemplateTransformationException(e);
      }
   }
}
