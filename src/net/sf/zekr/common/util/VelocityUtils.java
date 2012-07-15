/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 3, 2007
 */

package net.sf.zekr.common.util;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranPage;
import net.sf.zekr.engine.page.IPagingData;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * This class contains some helper methods to be used in Velocity templates. This is a replacement for velocity-tools-generic
 * library.
 * 
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class VelocityUtils {
   public double mul(Object num1, Object num2) {
      Number n1 = toDouble(num1);
      Number n2 = toDouble(num2);

      double value = n1.doubleValue() * n2.doubleValue();
      return value;
   }

   public int intAdd(Object num1, Object num2) {
      Number n1 = toInteger(num1);
      Number n2 = toInteger(num2);

      return n1.intValue() + n2.intValue();
   }

   public double div1(Object num1, Object num2) {
      Number n1 = toDouble(num1);
      Number n2 = toDouble(num2);

      double value = n1.doubleValue() / n2.doubleValue();
      return ((int) (value * 10)) / 10.0;
   }

   public Number toDouble(Object num) {
      return Double.valueOf(num.toString());
   }

   public Integer toInteger(Object num) {
      return new Integer(toDouble(num).intValue());
   }

   public Integer round(Object num) {
      Number n = toDouble(num);
      return new Integer((int) Math.rint(n.doubleValue()));
   }

   @SuppressWarnings("rawtypes")
   public int arraySize(Object arr) {
      if (arr instanceof Collection) {
         return ((Collection) arr).size();
      }
      return arr == null ? -1 : ((Object[]) arr).length;
   }

   //	public String items2JsArray(IPlaylistProvider pp, int page) {
   //		int ayaCount = QuranPropertiesUtils.getSura(page).getAyaCount();
   //		StringBuffer buf = new StringBuffer("[");
   //		if (ayaCount > 0) // always true :-)
   //			buf.append(pp.getItem(page, 1));
   //		for (int aya = 2; aya <= ayaCount; aya++) {
   //			buf.append(", " + pp.getItem(page, aya));
   //		}
   //		buf.append("]");
   //		return buf.toString();
   //	}

   public String items2JsArray(int pageNum) {
      IPagingData pagingData = ApplicationConfig.getInstance().getQuranPaging().getDefault();
      IQuranPage quranPage = pagingData.getQuranPage(pageNum);
      IQuranLocation fromLoc = quranPage.getFrom();
      IQuranLocation toLoc = quranPage.getTo();
      StringBuffer buf = new StringBuffer("[");
      while (fromLoc != null && fromLoc.compareTo(toLoc) <= 0) {
         buf.append('\'').append(fromLoc.toString()).append('\'').append(", ");
         fromLoc = fromLoc.getNext();
      }
      if (buf.length() > 1) {
         buf.replace(buf.length() - 2, buf.length(), "]");
      }
      return buf.toString();
   }

   @SuppressWarnings("rawtypes")
   public Object getItem(Object arr, int index) {
      if (arr instanceof List) {
         return ((List) arr).get(index);
      }
      return ((Object[]) arr)[index];
   }

   public String getRepeatOptions(int repeatTime) {
      StringBuffer buf = new StringBuffer();
      PropertiesConfiguration props = ApplicationConfig.getInstance().getProps();
      int max = props.getInt("audio.maxRepeatTime", 10);
      for (int i = 1; i <= max; i++) {
         buf.append("<option" + (i == repeatTime ? " selected=\"selected\"" : "") + ">&nbsp;").append(i)
               .append("&nbsp;</option>");
      }
      return buf.toString();
   }

   private static Pattern COMMENTARY = Pattern.compile("\\[\\[([^\\[\\]]+)\\]\\]");

   public String processAya(String str) {
      if (StringUtils.isEmpty(str)) {
         return "";
      }

      str = StringUtils.replace(str, "\\\\", "<br/>");

      Matcher m;
      int i = 1;
      do {
         m = COMMENTARY.matcher(str);
         str = m.replaceFirst("<span class=\"commentHandle\">(" + i++
               + ")</span> <span class=\"commentText\" style=\"display: none\">$1</span>");
      } while (m.find());

      return str;

   }

   /*public static void main(String[] args) {
      System.out.println(processAya("adslkj sdlfjgk sdlfk [[qwew [[_+_+]] qe]] sdfdfvsdfbdfb dfvd [[sdvcsdc]] sdfsdv."));
   }*/
}
