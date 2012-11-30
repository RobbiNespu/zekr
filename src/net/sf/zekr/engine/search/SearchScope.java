/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 18, 2006
 */
package net.sf.zekr.engine.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.zekr.common.resource.IQuranLocation;

import org.apache.commons.collections.CollectionUtils;

public class SearchScope {
   List<SearchScopeItem> list = new ArrayList<SearchScopeItem>();

   public void add(SearchScopeItem item) {
      int i = list.indexOf(item);
      if (i == -1)
         list.add(item);
      else {
         list.remove(i);
         list.add(item);
      }
   }

   public boolean contains(SearchScopeItem item) {
      return list.contains(item);
   }

   public List<SearchScopeItem> getScopeItems() {
      return list;
   }

   public String toString() {
      return list.toString();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj instanceof SearchScope) {
         SearchScope ss = (SearchScope) obj;
         return ss.list.equals(this.list);
      }
      return false;
   }

   /**
    * Tests whether an aya of a sura conforms to the search scope constraints.
    * 
    * @param quranLocation the sura-aya pair.
    * @return <code>true</code> if this search scope includes the aya (constraints are applied consecutively),
    *         <code>false</code> otherwise.
    */
   public boolean includes(IQuranLocation quranLocation) {
      return includes(quranLocation.getSura(), quranLocation.getAya());
   }

   /**
    * Tests whether an aya of a sura conforms to the search scope constraints.
    * 
    * @param sura sura number
    * @param aya aya number
    * @return <code>true</code> if this search scope includes the aya (constraints are applied consecutively),
    *         <code>false</code> otherwise.
    */
   public boolean includes(int sura, int aya) {
      if (list.size() == 0)
         return false;
      for (Iterator<SearchScopeItem> iter = list.iterator(); iter.hasNext();) {
         SearchScopeItem ssi = iter.next();
         if (ssi.excludes(sura, aya))
            return false;
      }
      for (Iterator<SearchScopeItem> iter = list.iterator(); iter.hasNext();) {
         SearchScopeItem ssi = iter.next();
         if (ssi.includes(sura, aya))
            return true;
      }
      return false;
   }

   public static SearchScope parse(List<String> searchScopeItemList) {
      SearchScope ss = new SearchScope();
      if (CollectionUtils.isEmpty(searchScopeItemList)) {
         return ss;
      }
      for (String ssiStr : searchScopeItemList) {
         SearchScopeItem ssi = SearchScopeItem.parse(ssiStr);
         ss.add(ssi);
      }

      return ss;
   }

   public List<String> format() {
      if (CollectionUtils.isEmpty(list)) {
         return Collections.emptyList();
      }
      List<String> ssiStringList = new ArrayList<String>();
      for (SearchScopeItem ssi : list) {
         ssiStringList.add(ssi.format());
      }
      return ssiStringList;
   }
}
