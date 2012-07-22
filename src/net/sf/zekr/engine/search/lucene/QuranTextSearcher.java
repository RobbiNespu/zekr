/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 28, 2007
 */
package net.sf.zekr.engine.search.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.engine.search.AbstractSearcher;
import net.sf.zekr.engine.search.SearchException;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.SearchScopeItem;
import net.sf.zekr.engine.search.SearchUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.util.DocIdBitSet;
import org.apache.lucene.util.Version;

/**
 * @author Mohsen Saboorian
 */
public class QuranTextSearcher extends AbstractSearcher {
   public static final int MAX_CLAUSE_COUNT = 10000;
   public static final int MAX_SEARCH_RESULT = 10000;

   private File indexDir;
   private Analyzer analyzer;
   private int maxClauseCount;
   private int maxSearchResult = MAX_SEARCH_RESULT;
   private Sort sortResultOrder;
   private IExtendedFormatter highlightFormatter;
   private int maxResultPerPage;
   private int pageNum;
   private List<SearchResultItem> results;
   private int matchedItemCount;
   private Query query;
   private String rawQuery;
   private ZekrIndexReader zekrIndexReader;
   private Set<String> highlightedTermList;

   public QuranTextSearcher(LuceneIndexManager luceneIndexManager, SearchScope searchScope) throws IndexingException {
      this(luceneIndexManager.getQuranIndex(), searchScope, LuceneAnalyzerFactory
            .getAnalyzer(ZekrLuceneAnalyzer.QURAN_LANG_CODE));
   }

   public QuranTextSearcher(LuceneIndexManager luceneIndexManager, SearchScope searchScope, IQuranText quranText)
         throws IndexingException {
      this(luceneIndexManager.getIndex(quranText), searchScope, LuceneAnalyzerFactory.getAnalyzer(quranText));
   }

   public QuranTextSearcher(ZekrIndexReader indexReader, SearchScope searchScope, Analyzer analyzer) {
      this.searchScope = searchScope;
      zekrIndexReader = indexReader;
      this.searchScope = searchScope;
      this.analyzer = analyzer;
      maxClauseCount = MAX_CLAUSE_COUNT;
      sortResultOrder = Sort.RELEVANCE;
      highlightFormatter = new ZekrHighlightFormatter();
      try {
         maxSearchResult = config.getProps().getInt("search.maxResult", MAX_SEARCH_RESULT);
      } catch (Exception e) {
         // silently ignore, as the variable is already initialized
      }
      pageNum = 0;
   }

   public void setSortResultOrder(Sort sortResultOrder) {
      this.sortResultOrder = sortResultOrder;
   }

   public Sort getSortResultOrder() {
      return sortResultOrder;
   }

   public void setAnalyzer(Analyzer analyzer) {
      this.analyzer = analyzer;
   }

   public Analyzer getAnalyzer() {
      return analyzer;
   }

   public void setMaxClauseCount(int maxClauseCount) {
      this.maxClauseCount = maxClauseCount;
   }

   public int getMaxClauseCount() {
      return maxClauseCount;
   }

   public void setHighlightFormatter(IExtendedFormatter highlightFormatter) {
      this.highlightFormatter = highlightFormatter;
   }

   public Formatter getHighlightFormatter() {
      return highlightFormatter;
   }

   public void setMaxResultPerPage(int maxAyaPerPage) {
      maxResultPerPage = maxAyaPerPage;
   }

   public int getMaxResultPerPage() {
      return maxResultPerPage;
   }

   public Query getQuery() {
      return query;
   }

   public boolean isIndexReaderOpen() {
      return zekrIndexReader != null;
   }

   public void setSearchScope(SearchScope searchScope) {
      this.searchScope = searchScope;
   }

   protected SearchResultModel doSearch(String query) throws SearchException {
      rawQuery = query;
      String s = SearchUtils.simplifyAdvancedSearchQuery(query);
      results = internalSearch(s);
      if (sortResultOrder.equals(Sort.RELEVANCE)) {
         ascending = !ascending; // Lucene sorts relevance descending, while natural order ascending!
      }
      // String clause = StringUtils.join(highlightedTermList, " ");
      String clause = getQuery().toString(QuranTextIndexer.CONTENTS_FIELD);
      return new SearchResultModel(zekrIndexReader.quranText, results, clause, rawQuery, matchedItemCount,
            searchResultComparator, ascending);
   }

   /**
    * Main search method, for internal use.
    * 
    * @param q query string
    * @return a list of highlighted string objects.
    * @throws SearchException
    */
   private List<SearchResultItem> internalSearch(String q) throws SearchException {
      IndexSearcher is = null;
      try {
         is = new IndexSearcher(zekrIndexReader.indexReader);

         // analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
         // resultTokenStream = new StandardTokenizer(Version.LUCENE_CURRENT, reader);

         QueryParser parser = QueryParserFactory.create(Version.LUCENE_CURRENT, QuranTextIndexer.CONTENTS_FIELD,
               analyzer);

         // allow search terms like "*foo" with leading star
         parser.setAllowLeadingWildcard(true);
         // parser.setFuzzyPrefixLength(10);

         // if this line is not set, highlighter doesn't work in in wildcard queries while query.rewrite() is done.
         // and sorting also doesn't work correctly for wildcard queries.
         parser.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);

         logger.debug("Parse query.");
         query = parser.parse(q);
         BooleanQuery.setMaxClauseCount(maxClauseCount);

         logger.debug("Rewrite query.");
         query = query.rewrite(zekrIndexReader.indexReader); // required to expand search terms

         logger.debug("Searching for: " + query.toString());
         // Hits hits;
         TopFieldDocs tops = null;
         is.setDefaultFieldSortScoring(true, true);
         if (searchScope != null && searchScope.getScopeItems().size() > 0) {
            String scopeQuery = makeSearchScope();
            logger.debug("Scope is: " + scopeQuery);
            // hits = is.search(query, new QuranRangeFilter(searchScope), sortResultOrder);
            tops = is.search(query, new QuranRangeFilter(searchScope), maxSearchResult, sortResultOrder);

         } else {
            // hits = is.search(query, new QueryWrapperFilter(query), 20, sortResultOrder);
            tops = is.search(query, new QueryWrapperFilter(query), maxSearchResult, sortResultOrder);
         }

         logger.debug("Highlight search result.");
         Highlighter highlighter = new Highlighter(highlightFormatter, new QueryScorer(query));
         // highlighter.setFragmentScorer(new QueryTermScorer(query));

         int total = Math.min(maxSearchResult, tops.totalHits);
         List<SearchResultItem> res = new ArrayList<SearchResultItem>(total);
         for (int i = 0; i < total; i++) {
            ScoreDoc[] sd = tops.scoreDocs;
            Document doc = is.doc(sd[i].doc);
            final String contents = doc.get(QuranTextIndexer.CONTENTS_FIELD);
            final IQuranLocation location = new QuranLocation(doc.get(QuranTextIndexer.LOCATION_FIELD));
            TokenStream tokenStream = analyzer.tokenStream(QuranTextIndexer.CONTENTS_FIELD, new StringReader(contents));

            // String resultStr = highlighter.getBestFragment(tokenStream, contents);
            String resultStr = highlighter.getBestFragments(tokenStream, contents, 100, "...");
            SearchResultItem sri = new SearchResultItem(resultStr, location);
            res.add(sri);
         }
         matchedItemCount = highlightFormatter.getHighlightCount();
         // highlightedTermList = highlightFormatter.getHighlightedTermList();
         return res;
      } catch (Exception e) {
         throw new SearchException(e);
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (IOException e) {
            }
         }
      }
   }

   private String makeSearchScope() {
      if (searchScope == null) {
         return "";
      }

      StringBuffer inclScopeStr = new StringBuffer();
      StringBuffer exclScopeStr = new StringBuffer();
      List<SearchScopeItem> scopeItems = searchScope.getScopeItems();

      for (int i = 0; i < scopeItems.size(); i++) {
         SearchScopeItem ssi = scopeItems.get(i);
         int sf = ssi.getSuraFrom();
         int af = ssi.getAyaFrom();
         int st = ssi.getSuraTo();
         int at = ssi.getAyaTo();
         if (ssi.isExclusive()) {
            exclScopeStr.append(" -location:[").append(new QuranLocation(sf, af).toSortableString()).append(" TO ")
                  .append(new QuranLocation(st, at).toSortableString()).append("] ");
         } else {
            inclScopeStr.append(" +location:[").append(new QuranLocation(sf, af).toSortableString()).append(" TO ")
                  .append(new QuranLocation(st, at).toSortableString()).append("] ");
         }
      }

      StringBuffer ret = new StringBuffer();
      if (inclScopeStr.length() > 0) {
         ret.append("((").append(inclScopeStr).append(")");
      }
      if (exclScopeStr.length() > 0) {
         ret.append(" (").append(exclScopeStr).append("))");
      } else {
         ret.append(")");
      }
      return ret.toString();
   }
}

/**
 * Constrains search results to only match those which also match a provided search scope.
 * 
 * @author Mohsen Saboorian
 */
class QuranRangeFilter extends Filter {
   private static final long serialVersionUID = 8665705675500932415L;
   private SearchScope searchScope;

   /**
    * Constructs a filter which only matches documents matching <code>searchScope</code>.
    * 
    * @param searchScope
    */
   public QuranRangeFilter(SearchScope searchScope) {
      this.searchScope = searchScope;
   }

   @Override
   public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
      BitSet bits = new BitSet(reader.maxDoc());
      for (int i = 0; i < reader.maxDoc(); i++) {
         Document doc = reader.document(i);
         IQuranLocation loc = new QuranLocation(doc.getField("location").stringValue());
         if (searchScope.includes(loc)) {
            bits.set(i);
         }
      }
      return new DocIdBitSet(bits);
   }
}
