/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 28, 2008
 */
package net.sf.zekr.common.resource.filter;

import java.util.regex.Pattern;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.engine.search.ArabicCharacters;
import net.sf.zekr.engine.search.tanzil.RegexUtils;

import org.apache.commons.lang.StringUtils;

public class QuranWriterFilter implements IQuranFilter, ArabicCharacters {
   private static final String REPLACE_HIGHLIGHT_NOSPACE = "<span class=\"waqfSign\">$1</span>";
   private static final String REPLACE_HIGHLIGHT = "<span class=\"waqfSign\">&nbsp;$1</span>";
   private static final String WAQF_REGEX = RegexUtils.regTrans(" ([$HIGH_SALA-$HIGH_SEEN])");

   private static final Pattern highlightRegex = Pattern.compile(WAQF_REGEX);

   public QuranWriterFilter() {
   }

   public String filter(QuranFilterContext qfc) {
      String str = qfc.text;

      boolean suppressFilter = ApplicationConfig.getInstance().getProps().getBoolean("text.filter.suppress", false);
      if (suppressFilter) {
         return str;
      }

      if (qfc.ayaNum == 1 && qfc.suraNum != 1 && qfc.suraNum != 9) {
         int sp = -1;
         for (int i = 0; i < 4; i++) { // ignore 4 whitespaces.
            sp = str.indexOf(' ', sp + 1);
         }
         str = str.substring(sp + 1);
      }

      // remove SAJDA and HIZB sign
      str = StringUtils.remove(str, " " + SAJDA_PLACE);
      str = StringUtils.remove(str, RUB_EL_HIZB + " ");

      // good for Uthmani text
      if ((qfc.params & UTHMANI_TEXT) == UTHMANI_TEXT) {
         str = RegexUtils.pregReplace(str, "($SHADDA|$FATHA)($SUPERSCRIPT_ALEF)", "$1$TATWEEL$2");
         str = RegexUtils.pregReplace(str, "([$HAMZA$DAL-$ZAIN$WAW][$SHADDA$FATHA]*)$TATWEEL($SUPERSCRIPT_ALEF)",
               "$1$ZWNJ$2");
      }
      str = RegexUtils.pregReplace(str, "($SHADDA)([$KASRA$KASRATAN])", "$2$1");

      str = StringUtils.replace(str, String.valueOf(ALEF) + MADDA, String.valueOf(ALEF_MADDA));

      if ((qfc.params & SHOW_WAQF_SIGN) != SHOW_WAQF_SIGN) {
         // remove all waqf signs
         str = highlightRegex.matcher(str).replaceAll("");
      }
      
      if ((qfc.params & HIGHLIGHT_WAQF_SIGN) == HIGHLIGHT_WAQF_SIGN) {
         // highlight waqf sign
         if (ApplicationConfig.getInstance().getProps().getBoolean("text.filter.noSpaceBeforeWaqf", false)) {
            str = highlightRegex.matcher(str).replaceAll(REPLACE_HIGHLIGHT_NOSPACE);
         } else {
            str = highlightRegex.matcher(str).replaceAll(REPLACE_HIGHLIGHT);
         }
      }
      return str;
   }
}
