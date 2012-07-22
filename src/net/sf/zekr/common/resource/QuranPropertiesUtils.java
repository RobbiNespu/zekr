/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 21, 2005
 */
package net.sf.zekr.common.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.revelation.RevelationData;
import net.sf.zekr.engine.search.Range;

// TODO: all the caching items on this class should be gradually moved 
// to a normal class which is instantiated and hold in ApplicationConfig.
// Only a single instance of this class is created then.

/**
 * This class tightly depends on the class <code>QuranProperties</code>. Almost all public methods on this class cache the static
 * results once called, and if called more, read it from the cache.<br>
 * <br>
 * This cache is not designed to be thread-safe. It is highly unsafe!
 * 
 * @author Mohsen Saboorian
 */
@SuppressWarnings("unchecked")
public class QuranPropertiesUtils {
   /** Number of Quran ayas based on Uthmani Mushaf */
   public static int QURAN_AYA_COUNT = 6236;

   /** Number of Quran suras */
   public static int QURAN_SURA_COUNT = 114;

   private static String[][] suraAyas = new String[114][];
   private static String[] suraNames = new String[114];
   private static String[] indexedSuraNames = new String[114];
   private static List<JuzProperties>[] juzInside = new ArrayList[114];
   private static List<SajdaProperties>[] sajdaInside = new ArrayList[114];
   private static int[] aggrAyaCount = new int[114];
   private static int[] revelOrder = new int[QURAN_AYA_COUNT];
   private static IQuranLocation[] absoluteLocation = new IQuranLocation[QURAN_AYA_COUNT];
   private static IQuranLocation[][] locationCache = new IQuranLocation[QURAN_SURA_COUNT][];

   public static final int getSajdaType(String sajda) {
      return QuranPropertiesNaming.RECOMMENDED_SAJDA.equalsIgnoreCase(sajda) ? SajdaProperties.MINOR : SajdaProperties.MAJOR;
   }

   public static final boolean isMadani(String descent) {
      return QuranPropertiesNaming.MADANI.equalsIgnoreCase(descent);
   }

   public static List<SajdaProperties> getSajdaList() {
      return QuranProperties.getInstance().getSajdaList();
   }

   /**
    * @param suraNum sura number (counted from 1)
    * @return a String array of aya numbers in a sura. e.g. ["1", "2", "3", ...]
    */
   public static final String[] getSuraAyas(int suraNum) {
      int ayas;
      if (suraAyas[0] == null) { // not loaded yet
         QuranProperties props = QuranProperties.getInstance();
         for (int i = 0; i < 114; i++) {
            SuraProperties sura = props.getSura(i + 1);
            ayas = sura.getAyaCount();
            suraAyas[i] = new String[ayas];
            for (int j = 0; j < ayas; j++) {
               suraAyas[i][j] = String.valueOf(j + 1);
            }
         }
      }
      return suraAyas[suraNum - 1];
   }

   /**
    * @return zero relative array of sura names
    */
   public static final String[] getSuraNames() {
      if (suraNames[0] == null) { // not loaded yet
         QuranProperties props = QuranProperties.getInstance();
         for (SuraProperties sura : props.getSuraList()) {
            suraNames[sura.getIndex() - 1] = sura.getName();
         }
      }
      return suraNames;
   }

   /**
    * @param suraNum (1-based)
    * @return the <code>SuraProperties</code> object corresponding to <code>suraNum</code>
    */
   public static final SuraProperties getSura(int suraNum) {
      QuranProperties props = QuranProperties.getInstance();
      return props.getSura(suraNum);
   }

   public static final void resetIndexedSuraNames() {
      indexedSuraNames[0] = null;
   }

   public static final String[] getIndexedSuraNames() {
      if (indexedSuraNames[0] == null) { // not loaded yet
         QuranProperties props = QuranProperties.getInstance();
         for (SuraProperties sura : props.getSuraList()) {
            indexedSuraNames[sura.getIndex() - 1] = getIndexedSuraName(sura);
         }
      }
      return indexedSuraNames;
   }

   public static final String getIndexedSuraName(int suraNum, String suraName) {
      return suraNum + ". " + suraName;
   }

   public static final String getIndexedSuraName(SuraProperties sura) {
      return getIndexedSuraName(sura.getIndex(), sura.toText());
   }

   public static final List<String> getLocalizedSuraNameList() {
      QuranProperties props = QuranProperties.getInstance();
      List<SuraProperties> suraList = props.getSuraList();
      List<String> suraNameList = new ArrayList<String>();
      for (int i = 0; i < suraList.size(); i++) {
         suraNameList.add(suraList.get(i).toText());
      }
      return suraNameList;
   }

   public static final String getIndexedSuraName(int suraNum) {
      return getIndexedSuraName(getSura(suraNum));
   }

   /**
    * @param juzNum 1-base juz number
    * @return juz properties object
    */
   public static final JuzProperties getJuz(int juzNum) {
      QuranProperties props = QuranProperties.getInstance();
      return props.getJuz(juzNum);
   }

   public static final SajdaProperties getSajda(int sajdaNum) {
      QuranProperties props = QuranProperties.getInstance();
      return props.getSajda(sajdaNum);
   }

   /**
    * Find and return the sajda for this location.
    * 
    * @param location
    * @return corresponding SajdaProperties (if any), or <code>null</code>, if there is no sajda for this location.
    */
   public static final SajdaProperties getSajda(IQuranLocation location) {
      List<SajdaProperties> sajdaList = getSajdaInsideList(location.getSura());
      for (int i = 0; i < sajdaList.size(); i++) {
         SajdaProperties sajda = sajdaList.get(i);
         if (sajda.getAyaNumber() == location.getAya()) {
            return sajda;
         }
      }
      return null;
   }

   /**
    * If there is any juz start within the sura, it will be returned. <br>
    * This method is the same as <code>getJuzInside()</code>, but with a different return type.
    * 
    * @param suraNum sura number (counted from 1)
    * @return a <code>List</code> of <code>JuzProperties</code>
    */
   public static final List<JuzProperties> getJuzInsideSura(int suraNum) {
      if (juzInside[0] == null) { // not loaded yet
         QuranProperties props = QuranProperties.getInstance();
         for (SuraProperties sura : props.getSuraList()) {
            List<JuzProperties> juzNum = new ArrayList<JuzProperties>();
            List<JuzProperties> list = props.getJuzList();

            for (JuzProperties juz : list) {
               if (juz.getSuraNumber() == sura.getIndex()) {
                  juzNum.add(juz);
               }
            }
            juzInside[sura.getIndex() - 1] = juzNum;
         }
      }
      return juzInside[suraNum - 1];
   }

   /**
    * If there is any juz start within the sura, it will be returned.
    * 
    * @param suraNum sura number (counted from 1)
    * @return <code>int</code> array of juz numbers
    */
   public static final int[] getJuzInside(int suraNum) {
      return getJuz(getJuzInsideSura(suraNum));
   }

   private static final int[] getJuz(List<JuzProperties> list) {
      int[] juzArray = new int[list.size()];
      JuzProperties juz;
      for (int i = 0; i < list.size(); i++) {
         juz = list.get(i);
         juzArray[i] = juz.getIndex();
      }
      return juzArray;
   }

   public static final JuzProperties getFirstJuzOf(int suraNum) {
      QuranProperties props = QuranProperties.getInstance();
      List<JuzProperties> list = props.getJuzList();

      Iterator<JuzProperties> iter = list.iterator();
      JuzProperties juz1 = iter.next();
      while (iter.hasNext()) {
         JuzProperties juz2 = iter.next();
         if (suraNum >= juz1.getSuraNumber() && suraNum <= juz2.getSuraNumber()) {
            if (juz2.getSuraNumber() == suraNum && juz2.getAyaNumber() == 1) {
               return juz2;
            }
            return juz1;
         }
         juz1 = juz2;
      }
      return juz1; // 30th juz
   }

   public static final JuzProperties getJuzOf(IQuranLocation iql) {
      return getJuzOf(iql.getSura(), iql.getAya());
   }

   public static final JuzProperties getJuzOf(int suraNum, int ayaNum) {
      List<JuzProperties> juzList = getSuraJuzAsList(suraNum);
      JuzProperties juz = juzList.get(0);
      for (int i = 1; i < juzList.size(); i++) {
         JuzProperties j = juzList.get(i);
         if (suraNum > j.getSuraNumber() || suraNum == j.getSuraNumber() && ayaNum >= j.getAyaNumber()) {
            juz = j;
         } else {
            break;
         }
      }
      return juz;
   }

   /**
    * @return a number between 0 to 7, meaning the hizb quarter in a juz. For example 0 means the first quranter of Hizb 1, 4
    *         means the first quarter of Hizb 2, and 7 means the third quarter of Hizb 2.
    */
   public static final int getHizbQuadIndex(JuzProperties juz, int suraNum, int ayaNum) {
      if (juz == null) {
         juz = getJuzOf(suraNum, ayaNum);
      }
      IQuranLocation hizbQuads[] = juz.getHizbQuarters();
      int quadIndex = 0;
      for (int i = 1; i < hizbQuads.length; i++) {
         IQuranLocation ql = hizbQuads[i];
         if (suraNum > hizbQuads[i].getSura() || suraNum == ql.getSura() && ayaNum >= ql.getAya()) {
            quadIndex = i;
         } else {
            break;
         }
      }
      return quadIndex;
   }

   public static final int getHizbQuadIndex(int suraNum, int ayaNum) {
      return getHizbQuadIndex(null, suraNum, ayaNum);
   }

   /**
    * @see #getHizbQuadIndex(int, int)
    * @param quranLoc
    * @return
    */
   public static int getHizbQuadIndex(IQuranLocation quranLoc) {
      return getHizbQuadIndex(null, quranLoc.getSura(), quranLoc.getAya());
   }

   /**
    * @param juz current juz to lookup in
    * @param quranLoc current quran location
    * @return
    */
   public static int getHizbQuadIndex(JuzProperties juz, IQuranLocation quranLoc) {
      return getHizbQuadIndex(juz, quranLoc.getSura(), quranLoc.getAya());
   }

   /**
    * @param suraNum sura number (counted from 1)
    * @return <code>List</code> of <code>SajdaProperties</code> inside <code>suraNum</code>
    */
   public static final List<SajdaProperties> getSajdaInsideList(int suraNum) {
      if (sajdaInside[0] == null) { // not loaded yet
         QuranProperties props = QuranProperties.getInstance();
         for (SuraProperties sura : props.getSuraList()) {
            List<SajdaProperties> sajdaList = new ArrayList<SajdaProperties>();
            List<SajdaProperties> list = props.getSajdaList();

            for (SajdaProperties sajda : list) {
               if (sajda.getSuraNumber() == sura.getIndex()) {
                  sajdaList.add(sajda);
               }
            }
            sajdaInside[sura.getIndex() - 1] = sajdaList;
         }
      }
      return sajdaInside[suraNum - 1];
   }

   /**
    * @param suraNum sura number (counted from 1)
    * @return Sura properties as a <code>Map</code>
    */
   public static final Map<String, String> getSuraPropsMap(int suraNum) {
      LanguageEngine dict = ApplicationConfig.getInstance().getLanguageEngine();
      RevelationData rd = ApplicationConfig.getInstance().getRevelation().getDefault();
      QuranPropertiesUtils.getSuraNames(); // load!
      SuraProperties sura = QuranProperties.getInstance().getSura(suraNum);
      Map<String, String> map = new LinkedHashMap<String, String>();
      map.put(dict.getMeaning("NAME"), getSura(suraNum).toText());
      map.put(dict.getMeaning("NUMBER"), String.valueOf(sura.getIndex()));
      map.put(dict.getMeaning("AYA_COUNT"), String.valueOf(sura.getAyaCount()));
      map.put(dict.getMeaning("DESCENT"), getSuraDescent(sura.isMadani()));
      map.put(dict.getMeaning("JUZ"), getSuraJuz(suraNum));
      if (rd != null) {
         map.put(dict.getMeaning("REVEL_ORDER"), String.valueOf(rd.getOrder(suraNum)));
      }
      return map;
   }

   public static final String propsToClipboadrFormat(Map<String, String> suraMap) {
      StringBuffer ret = new StringBuffer();
      for (Entry<String, String> entry : suraMap.entrySet()) {
         ret.append(entry.getKey() + ": " + entry.getValue() + GlobalConfig.LINE_SEPARATOR);
      }
      return ret.length() > 0 ? ret.substring(0, ret.length() - GlobalConfig.LINE_SEPARATOR.length()) : ret.toString();
   }

   /**
    * @param isMadani
    * @return localized <code>String</code> of descent type of a sura (Makki or Madani)
    */
   public static final String getSuraDescent(boolean isMadani) {
      LanguageEngine dict = ApplicationConfig.getInstance().getLanguageEngine();
      return isMadani ? dict.getMeaning("MADANI") : dict.getMeaning("MAKKI");
   }

   public static final String getSuraJuz(int suraNum) {
      int[] juzList = getJuzInside(suraNum);
      int firstJuz = getFirstJuzOf(suraNum).getIndex();
      if (juzList.length == 0) {
         juzList = new int[1];
         juzList[0] = firstJuz;
      } else if (juzList[0] != firstJuz) {
         juzList = CollectionUtils.concat(new int[] { firstJuz }, juzList);
      }
      return CollectionUtils.getLocalizedList(juzList);
   }

   public static final List<JuzProperties> getSuraJuzAsList(int suraNum) {
      List<JuzProperties> retList = new ArrayList<JuzProperties>(getJuzInsideSura(suraNum));
      JuzProperties firstJuz = getFirstJuzOf(suraNum);
      if (retList.size() == 0) {
         retList.add(firstJuz);
      } else if (retList.get(0) != firstJuz) {
         retList.add(0, firstJuz);
      }
      return retList;
   }

   /**
    * @param juz juz number to find its suras
    * @return a <code>{@link Range}</code> object whose from and to are both inclusive.
    */
   public static final Range getSuraInsideJuz(int juz) {
      QuranProperties props = QuranProperties.getInstance();
      int startSura = props.getJuz(juz).getSuraNumber();
      int fromSura, toSura;
      fromSura = startSura;
      if (juz >= 30) {
         toSura = 114;
      } else {
         JuzProperties jp = props.getJuz(juz + 1);
         if (jp.getAyaNumber() > 1) {
            toSura = jp.getSuraNumber();
         } else {
            toSura = jp.getSuraNumber() - 1;
         }
      }
      return new Range(fromSura, toSura);
   }

   /**
    * Returns aggregative sum of aya count from sura 1 to sura i<br>
    * (for sura: 1 to suraNum parameter) &#8721;(<i>aya count</i>)
    * 
    * @param suraNum sura number (counted from 1)
    * @return the sum of aya count from sura 1 to suraNum - 1.
    */
   public static final int getAggregateAyaCount(int suraNum) {
      if (aggrAyaCount[113] == 0) { // not loaded yet
         int k = 0;
         for (int i = 0; i < 114; i++) {
            aggrAyaCount[i] = k;
            k += getSura(i + 1).getAyaCount();
         }
      }
      return aggrAyaCount[suraNum - 1];
   }

   /**
    * @param quranPage
    * @return a list of Quran locations of type {@link IQuranPage}.
    */
   public static final List<IQuranLocation> getPageData(IQuranPage quranPage) {
      List<IQuranLocation> locList = new ArrayList<IQuranLocation>();
      IQuranLocation from = quranPage.getFrom();
      IQuranLocation to = quranPage.getFrom();
      while (from != null && to.compareTo(from) >= 0) {
         locList.add(from);
         from = from.getNext();
      }
      return locList;
   }

   /**
    * @param quranText
    * @param quranPage
    * @return a list of Quran locations of type {@link IQuranPage}.
    */
   public static final List<String> getPageData(IQuranText quranText, IQuranPage quranPage) {
      List<String> ayaList = new ArrayList<String>();
      IQuranLocation from = quranPage.getFrom();
      IQuranLocation to = quranPage.getFrom();
      while (from != null && to.compareTo(from) >= 0) {
         ayaList.add(quranText.get(from));
         from = from.getNext();
      }
      return ayaList;
   }

   public static void updateLocalizedSuraNames() {
      QuranProperties props = QuranProperties.getInstance();
      props.quranPropsReader.updateLocalizedSuraNames();
   }

   public static int getRevelationOrder(IQuranLocation location) {
      return 0;
   }

   /**
    * @return all Quran locations as an array. The size of the array is {@link QuranPropertiesUtils#QURAN_AYA_COUNT}.
    */
   public static IQuranLocation[] getLocations() {
      QuranProperties props = QuranProperties.getInstance();
      if (absoluteLocation[0] == null) { // not loaded yet
         absoluteLocation = new IQuranLocation[QURAN_AYA_COUNT];
         int absolute = 0;
         for (int sura = 1; sura <= 114; sura++) {
            int ayaCount = props.getSura(sura).getAyaCount();
            locationCache[sura - 1] = new IQuranLocation[ayaCount];
            IQuranLocation[] suraLoc = locationCache[sura - 1];
            for (int aya = 1; aya <= ayaCount; aya++) {
               absoluteLocation[absolute] = new QuranLocation(sura, aya);
               suraLoc[aya - 1] = absoluteLocation[absolute];
               absolute++;
            }
         }
      }
      return absoluteLocation;
   }

   /**
    * @param absoluteAyaNum a positive number between 1 and 6236.
    * @return an IQuranLocation instance for this aya.
    */
   public static final IQuranLocation getLocation(int absoluteAyaNum) {
      return getLocations()[absoluteAyaNum - 1];
   }

   public static final IQuranLocation getLocation(String location) {
      int i = location.indexOf('-');
      if (i == -1) {
         throw new IllegalArgumentException(location);
      }
      int sura = Integer.parseInt(location.substring(0, i));
      int aya = Integer.parseInt(location.substring(i + 1));
      return getLocation(sura, aya);
   }

   public static final boolean isValid(int sura, int aya) {
      QuranProperties qp = QuranProperties.getInstance();
      return between(sura, 1, QuranPropertiesUtils.QURAN_SURA_COUNT) && between(aya, 1, qp.getSura(sura).getAyaCount());
   }

   public static final boolean isValid(IQuranLocation quranLocation) {
      return isValid(quranLocation.getSura(), quranLocation.getAya());
   }

   private final static boolean between(int num, int from, int to) {
      return num >= from && num <= to;
   }

   /**
    * This method returns a Qur'an location, which is looked up from a cache of a Qur'an location. Since {@link IQuranLocation} is
    * immutable, it can be shared.
    * 
    * @param sura (counted from 1)
    * @param aya (counted from 1)
    * @return a cached Quran location
    */
   public static final IQuranLocation getLocation(int sura, int aya) {
      return locationCache[sura - 1][aya - 1];
   }

   /**
    * @param iql Qur'an location to be looked up
    * @return Qur'an location absolute aya index or -1 if nothing found.
    */
   public static final int getAbsoluteLocation(IQuranLocation iql) {
      for (int i = 0; i < absoluteLocation.length; i++) {
         if (iql.equals(absoluteLocation[i])) {
            return i;
         }
      }
      return -1;
   }

   /**
    * @param loc1 first location
    * @param loc2 second location
    * @return absolute aya number for location 1 - absolute number for location 2
    */
   public static final int diff(IQuranLocation loc1, IQuranLocation loc2) {
      return getAbsoluteLocation(loc1) - getAbsoluteLocation(loc2);
   }

   public static List<SuraProperties> getSuraList() {
      return QuranProperties.getInstance().getSuraList();
   }

   public static Locale getSuraNameModeLocale() {
      ApplicationConfig config = ApplicationConfig.getInstance();
      String suraNameMode = config.getProps().getString("view.sura.name");
      if (QuranPropertiesNaming.SURA_NAME_ARABIC.equals(suraNameMode)) {
         return new Locale("ar");
      } else if (QuranPropertiesNaming.SURA_NAME_T13N.equals(suraNameMode)
            || QuranPropertiesNaming.SURA_NAME_T9N.equals(suraNameMode)) {
         return config.getLanguageEngine().getLocale();
      } else {
         return new Locale("en");
      }
   }

   /**
    * @return an array of localized sura names indexed and sorted based on current revelation order pack (or normal order if no
    *         revelation pack is available).
    */
   public static String[] getIndexedRevelationOrderedSuraNames() {
      ApplicationConfig conf = ApplicationConfig.getInstance();
      QuranProperties props = QuranProperties.getInstance();
      RevelationData rev = conf.getRevelation().getDefault();
      if (rev == null) {
         return getIndexedSuraNames();
      }
      String[] result = new String[114];
      for (int i = 1; i <= 114; i++) {
         int suraNum = rev.getSuraOfOrder(i);
         SuraProperties sura = props.getSura(suraNum);
         result[i - 1] = getIndexedSuraName(i, sura.toText());
      }
      return result;
   }
}
