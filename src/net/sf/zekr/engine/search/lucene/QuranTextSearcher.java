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

import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchResultItem;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.SearchScopeItem;
import net.sf.zekr.engine.search.SearchUtils;
import net.sf.zekr.engine.search.comparator.AbstractSearchResultComparator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

/**
 * @author Mohsen Saboorian
 */
public class QuranTextSearcher {
	protected Logger logger = Logger.getLogger(this.getClass());
	private static final int MAX_CLAUSE_COUNT = 10000;

	private File indexDir;
	private Analyzer analyzer;
	private int maxClauseCount;
	private Sort sortResultOrder;
	private IExtendedFormatter highlightFormatter;
	private int maxResultPerPage;
	private int pageNum;
	private List results;
	private int matchedItemCount;
	private Query query;
	private SearchScope searchScope;
	private String rawQuery;
	private boolean ascending;
	private AbstractSearchResultComparator searchResultComparator;
	private IndexReader indexReader;

	/**
	 * Checks first user home directory and then installation directory for indices.
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public QuranTextSearcher() throws CorruptIndexException, IOException {
		this(new File(Naming.getQuranIndexDir()).exists() ? Naming.getQuranIndexDir() : ApplicationPath.QURAN_INDEX_DIR,
				null);
	}

	public QuranTextSearcher(File indexDir, SearchScope searchScope) throws CorruptIndexException, IOException {
		this(IndexReader.open(indexDir), searchScope);
	}

	public QuranTextSearcher(String indexDir, SearchScope searchScope) throws CorruptIndexException, IOException {
		this(new File(indexDir), searchScope);
	}

	public QuranTextSearcher(LuceneIndexManager luceneIndexManager, SearchScope searchScope) throws IndexingException {
		this(luceneIndexManager.getQuranIndex(), searchScope);
	}

	public QuranTextSearcher(IndexReader indexReader, SearchScope searchScope) {
		this.searchScope = searchScope;
		this.indexReader = indexReader;
		this.searchScope = searchScope;
		this.analyzer = new ArabicAnalyzer();
		this.maxClauseCount = MAX_CLAUSE_COUNT;
		this.sortResultOrder = Sort.RELEVANCE;
		this.highlightFormatter = new ZekrHighlightFormatter();
		this.pageNum = 0;
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

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setHighlightFormatter(IExtendedFormatter highlightFormatter) {
		this.highlightFormatter = highlightFormatter;
	}

	public Formatter getHighlightFormatter() {
		return highlightFormatter;
	}

	public void setMaxResultPerPage(int maxAyaPerPage) {
		this.maxResultPerPage = maxAyaPerPage;
	}

	public int getMaxResultPerPage() {
		return maxResultPerPage;
	}

	public Query getQuery() {
		return query;
	}

	public boolean isIndexReaderOpen() {
		return indexReader != null;
	}

	public void setSearchScope(SearchScope searchScope) {
		this.searchScope = searchScope;
	}

	public SearchScope getSearchScope() {
		return searchScope;
	}

	public AdvancedSearchResult search(String query) throws IOException, ParseException {
		this.rawQuery = query;
		String s = SearchUtils.simplifyAdvancedSearchQuery(query);
		results = internalSearch(s);
		if (sortResultOrder.equals(Sort.RELEVANCE))
			ascending = !ascending; // Lucene sorts relevance descending, while natural order ascending!
		return new AdvancedSearchResult(results, getQuery().toString(QuranTextIndexer.CONTENTS_FIELD), rawQuery,
				matchedItemCount, searchResultComparator, ascending);
	}

	/**
	 * Main search method, for internal use.
	 * 
	 * @param q query string
	 * @return a list of highlighted string objects.
	 * @throws IOException
	 * @throws ParseException
	 */
	private List internalSearch(String q) throws IOException, ParseException {
		IndexSearcher is = new IndexSearcher(indexReader);
		QueryParser parser = new QueryParser(QuranTextIndexer.CONTENTS_FIELD, analyzer);

		// allow search terms like "*foo" with leading star
		parser.setAllowLeadingWildcard(true);
		// parser.setFuzzyPrefixLength(10);

		logger.debug("Parse query.");
		query = parser.parse(q);
		BooleanQuery.setMaxClauseCount(maxClauseCount);
		logger.debug("Rewrite query.");
		query = query.rewrite(indexReader); // required to expand search terms
		logger.debug("Searching for: " + query.toString());
		Hits hits;
		if (searchScope != null && searchScope.getScopeItems().size() > 0) {
			String scopeQuery = makeSearchScope();
			logger.debug("Scope is: " + scopeQuery);
			hits = is.search(query, new QuranRangeFilter(searchScope), sortResultOrder);
		} else
			hits = is.search(query, sortResultOrder);

		logger.debug("Highlight search result.");
		Highlighter highlighter = new Highlighter(highlightFormatter, new QueryScorer(query));

		List res = new ArrayList(hits.length());
		for (int i = 0; i < hits.length(); i++) {
			Document doc = hits.doc(i);
			final String contents = doc.get(QuranTextIndexer.CONTENTS_FIELD);
			final IQuranLocation location = new QuranLocation(doc.get(QuranTextIndexer.LOCATION_FIELD));
			TokenStream tokenStream = analyzer.tokenStream(QuranTextIndexer.CONTENTS_FIELD, new StringReader(contents));
			// String resultStr = highlighter.getBestFragment(tokenStream, contents);
			String resultStr = highlighter.getBestFragments(tokenStream, contents, 100, "...");
			SearchResultItem sri = new SearchResultItem(resultStr, location);
			res.add(sri);
			// res.add(new AdvancedSearchResultItem(location, resultStr));
		}
		matchedItemCount = highlightFormatter.getHighlightCount();
		return res;
	}

	private String makeSearchScope() {
		if (searchScope == null)
			return "";

		StringBuffer inclScopeStr = new StringBuffer();
		StringBuffer exclScopeStr = new StringBuffer();
		List scopeItems = searchScope.getScopeItems();

		for (int i = 0; i < scopeItems.size(); i++) {
			SearchScopeItem ssi = (SearchScopeItem) scopeItems.get(i);
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
		if (inclScopeStr.length() > 0)
			ret.append("((").append(inclScopeStr).append(")");
		if (exclScopeStr.length() > 0)
			ret.append(" (").append(exclScopeStr).append("))");
		else
			ret.append(")");
		return ret.toString();
	}

	public void setSearchResultComparator(AbstractSearchResultComparator searchResultComparator) {
		this.searchResultComparator = searchResultComparator;
	}

	public AbstractSearchResultComparator getSearchResultComparator() {
		return searchResultComparator;
	}

}

/**
 * Constrains search results to only match those which also match a provided search scope.
 * 
 * @author Mohsen Saboorian
 */
class QuranRangeFilter extends Filter {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4081394885101418256L;
	private SearchScope searchScope;

	/**
	 * Constructs a filter which only matches documents matching <code>searchScope</code>.
	 * 
	 * @param searchScope
	 */
	public QuranRangeFilter(SearchScope searchScope) {
		this.searchScope = searchScope;
	}

	public BitSet bits(IndexReader reader) throws IOException {
		BitSet bits = new BitSet(reader.maxDoc());
		for (int i = 0; i < reader.maxDoc(); i++) {
			Document doc = reader.document(i);
			IQuranLocation loc = new QuranLocation(doc.getField("location").stringValue());
			if (searchScope.includes(loc))
				bits.set(i);
		}
		return bits;
	}

	/**
	 * @param path directory to test against existence of lucene indices.
	 * @return <code>true</code> if some some Lucene indices exists at the specified path, <code>false</code>
	 *         otherwise
	 */
	public boolean indexExists(String path) {
		return IndexReader.indexExists(path);
	}
}
