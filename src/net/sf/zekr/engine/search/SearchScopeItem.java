/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Oct 29, 2005
 */
package net.sf.zekr.engine.search;

import java.util.StringTokenizer;

import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.SuraProperties;

import org.apache.commons.lang.StringUtils;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class SearchScopeItem {
   private final int suraFrom, ayaFrom;
   private final int suraTo, ayaTo;
   private final boolean exclusive;

   private static final char DELIM = ',';

   public static SearchScopeItem parse(String ssi) {
      if (StringUtils.isBlank(ssi)) {
         return null;
      }

      String[] s = StringUtils.split(ssi, "-");
      if (s.length < 4) {
         return null;
      } else {
         boolean exclusive = false;
         if (s.length >= 5) {
            exclusive = Boolean.parseBoolean(s[4]);
         }
         return new SearchScopeItem(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]),
               Integer.parseInt(s[3]), exclusive);
      }
   }

   public String format() {
      return String.format("%s-%s-%s-%s-%s", suraFrom, ayaFrom, suraTo, ayaTo, exclusive);
   }

   /**
    * Creates a new scope item from 1-1 to 1-1, inclusive.
    */
   public SearchScopeItem() {
      this(1, 1, 1, 1, false);
   }

   /**
    * All parameters are behaved as 1-based. Note that no range check (for aya count, or sura number) is
    * performed.
    * 
    * @throws IllegalSearchScopeItemException if from-to range is not considered.
    */
   public SearchScopeItem(int suraFrom, int ayaFrom, int suraTo, int ayaTo, boolean exclusive)
         throws IllegalSearchScopeItemException {
      if ((suraFrom > suraTo) || (suraFrom == suraTo && ayaFrom > ayaTo))
         throw new IllegalSearchScopeItemException();
      this.suraFrom = suraFrom;
      this.ayaFrom = ayaFrom;
      this.suraTo = suraTo;
      this.ayaTo = ayaTo;
      this.exclusive = exclusive;
   }

   public int getAyaFrom() {
      return ayaFrom;
   }

   public int getAyaTo() {
      return ayaTo;
   }

   public int getSuraFrom() {
      return suraFrom;
   }

   public int getSuraTo() {
      return suraTo;
   }

   public boolean isExclusive() {
      return exclusive;
   }

   /**
    * @return <code>true</code> if obj is of type <code>SearchScopeItem</code> and all its properties are
    *         equal to <code>this</code> properties.
    */
   public boolean equals(Object obj) {
      if (obj instanceof SearchScopeItem) {
         return equals((SearchScopeItem) obj);
      } else {
         return super.equals(obj);
      }
   }

   /**
    * Tests whether an aya of a sura is included in this scope (works only if this item is inclusive).
    * 
    * @param sura sura number
    * @param aya aya number
    * @return <code>true</code> if this search scope item explicitly includes the aya, <code>false</code>
    *         otherwise.
    */
   public final boolean includes(int sura, int aya) {
      if (!exclusive) {
         if (sura < suraFrom || sura > suraTo || (sura == suraFrom && aya < ayaFrom) || (sura == suraTo && aya > ayaTo))
            return false;
      }
      return !exclusive;
   }

   /**
    * Tests whether an aya of a sura is excluded from this scope (works only if this item is exclusive).
    * 
    * @param sura sura number
    * @param aya aya number
    * @return <code>true</code> if this search scope item explicitly excludes the aya, <code>false</code>
    *         otherwise.
    */
   public final boolean excludes(int sura, int aya) {
      if (exclusive) {
         if (sura < suraFrom || sura > suraTo || (sura == suraFrom && aya < ayaFrom) || (sura == suraTo && aya > ayaTo))
            return false;
      }
      return exclusive;
   }

   private final boolean equals(SearchScopeItem item) {
      return item.suraFrom == suraFrom && item.ayaFrom == ayaFrom && item.suraTo == suraTo && item.ayaTo == ayaTo
            && item.exclusive == exclusive;
   }

   public String toString() {
      StringBuffer buf = new StringBuffer();
      SuraProperties sura1 = QuranPropertiesUtils.getSura(suraFrom);
      SuraProperties sura2 = QuranPropertiesUtils.getSura(suraTo);
      String sign = exclusive ? "(-)" : "(+)";
      buf.append(sign).append("[").append(sura1.getName()).append(" ").append(ayaFrom).append("] - [")
            .append(sura2.getName()).append(" ").append(ayaTo).append("]");
      return buf.toString();
   }

   public String serialize() {
      return (exclusive ? "-" : "") + suraFrom + DELIM + ayaFrom + DELIM + suraTo + DELIM + ayaTo;
   }

   /**
    * Creates a SearchScopeItem from the given string <code>scopeItemStr</code>. A string representation of a
    * search scope item is of the form <tt>[-]sura_from,aya_from,sura_to,aya_to</tt>. "<tt>-</tt>" is used to
    * specify that this search scope is <b>exclusive</b>.
    * 
    * @param scopeItemStr input scope item in string format
    * @return a new <code>SearchScopeItem</code> instance
    * @throws IllegalSearchScopeItemException if scope is not in well format
    */
   public static SearchScopeItem deserialize(String scopeItemStr) {
      StringTokenizer t = new StringTokenizer(scopeItemStr, DELIM + " \n\r");
      int sf, af, st, at;
      try {
         sf = Integer.parseInt(t.nextToken());
         af = Integer.parseInt(t.nextToken());
         st = Integer.parseInt(t.nextToken());
         at = Integer.parseInt(t.nextToken());
      } catch (NumberFormatException e) {
         throw new IllegalSearchScopeItemException();
      }
      return new SearchScopeItem(Math.abs(sf), af, st, at, sf < 0);
   }
}
