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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.zekr.common.ZekrBaseRuntimeException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ApplicationPath;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.engine.search.SearchScopeItem;
import net.sf.zekr.engine.search.SearchUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hit;
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
public class QuranTextSearcher implements Enumeration {
	private final Logger logger = Logger.getLogger(QuranTextSearcher.class);
	private static final int MAX_CLAUSE_COUNT = 10000;

	private File indexDir;
	private Analyzer analyzer;
	private int maxClauseCount;
	private Sort sortResultOrder;
	private IExtendedFormatter highlightFormatter;
	private int maxResultPerPage;
	private int pageNum;
	private List results;
	private int matcheditem;
	private int matchedItemCount;
	private Query query;
	private SearchScope searchScope;
	private String rawQuery;

	/**
	 * Checks first user home directory and then installation directory for indices.
	 */
	public QuranTextSearcher() {
		this(new File(Naming.getQuranIndexDir()).exists() ? Naming.getQuranIndexDir() : ApplicationPath.QURAN_INDEX_DIR,
				null);
	}

	public QuranTextSearcher(File indexDir, SearchScope searchScope) {
		PropertiesConfiguration props = ApplicationConfig.getInstance().getProps();
		if (props.getProperty("index.quran.done") == null || !props.getBoolean("index.quran.done"))
			throw new ZekrBaseRuntimeException("Indexing is not done yet.");
		this.indexDir = indexDir;
		this.searchScope = searchScope;
		this.analyzer = new ArabicAnalyzer();
		this.maxClauseCount = MAX_CLAUSE_COUNT;
		this.sortResultOrder = Sort.RELEVANCE;
		this.highlightFormatter = new ZekrHighlightFormatter();
		this.maxResultPerPage = ApplicationConfig.getInstance().getProps().getInt("options.search.maxResult");
		this.pageNum = 0;
	}

	public QuranTextSearcher(String indexDir, SearchScope searchScope) {
		this(new File(indexDir), searchScope);
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
		this.maxResultPerPage = maxAyaPerPage;
	}

	public int getMaxResultPerPage() {
		return maxResultPerPage;
	}

	public Query getQuery() {
		return query;
	}

	public int getResultCount() {
		if (results == null)
			throw new IllegalSearchStateException("Method search(String) should be called first.");

		return results.size();
	}

	public int getResultPageCount() {
		if (results == null)
			throw new IllegalSearchStateException("Method search(String) should be called first.");

		return (int) Math.ceil((double) results.size() / maxResultPerPage);
	}

	public int getMatchedItemCount() {
		if (results == null)
			throw new IllegalSearchStateException("Method search(String) should be called first.");

		return matchedItemCount;
	}

	public void setSearchScope(SearchScope searchScope) {
		this.searchScope = searchScope;
	}

	public SearchScope getSearchScope() {
		return searchScope;
	}

	public String getRawQuery() {
		if (rawQuery == null)
			throw new IllegalSearchStateException("Method search(String) should be called first.");
		return rawQuery;
	}

	public void search(String query) throws IOException, ParseException {
		this.rawQuery = query;
		String s = SearchUtils.simplifyAdvancedSearchQuery(query);
		results = _search(s);
	}

	/**
	 * Retrieves the specified page of search results.
	 * 
	 * @param page
	 *           page number a zero-based page number
	 * @return a specific page
	 * @throws NoSuchElementException
	 *            if no such page exists
	 * @throws IllegalSearchStateException
	 *            if <code>search(String)</code> is not called yet
	 */
	public List getPage(int page) {
		if (results == null)
			throw new IllegalSearchStateException("Method search(String) should be called first.");
		if (page * maxResultPerPage > results.size())
			throw new NoSuchElementException("No such page: " + page);

		if ((page + 1) * maxResultPerPage <= results.size())
			return results.subList(page * maxResultPerPage, (page + 1) * maxResultPerPage);

		return results.subList(page * maxResultPerPage, results.size());

	}

	/**
	 * Main search method, for internal use.
	 * 
	 * @param q
	 *           query string
	 * @return a list of highlighted string objects.
	 * @throws IOException
	 * @throws ParseException
	 */
	private List _search(String q) throws IOException, ParseException {
		logger.debug("Open index reader.");
		IndexReader reader = IndexReader.open(indexDir);
		IndexSearcher is = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(QuranTextIndexer.CONTENTS_FIELD, analyzer);

		// allow search terms like "*foo" with leading star
		parser.setAllowLeadingWildcard(true);
		// parser.setFuzzyPrefixLength(10);

		logger.debug("Parse query.");
		query = parser.parse(q);
		BooleanQuery.setMaxClauseCount(maxClauseCount);
		logger.debug("Rewrite query.");
		query = query.rewrite(reader); // required to expand search terms
		logger.debug("Searching for: " + query.toString());
		Hits _hits;
		if (searchScope != null && searchScope.getScopeItems().size() > 0) {
			String scopeQuery = makeSearchScope();
			logger.debug("Scope is: " + scopeQuery);
			_hits = is.search(query, new QuranRangeFilter(searchScope), sortResultOrder);
		} else
			_hits = is.search(query, sortResultOrder);

		logger.debug("Highlight search result.");
		Highlighter highlighter = new Highlighter(highlightFormatter, new QueryScorer(query));

		List res = new ArrayList(_hits.length());
		for (int i = 0; i < _hits.length(); i++) {
			Document doc = _hits.doc(i);
			final String contents = doc.get(QuranTextIndexer.CONTENTS_FIELD);
			final QuranLocation location = new QuranLocation(doc.get(QuranTextIndexer.LOCATION_FIELD));
			TokenStream tokenStream = analyzer.tokenStream(QuranTextIndexer.CONTENTS_FIELD, new StringReader(contents));
			// String resultStr = highlighter.getBestFragment(tokenStream, contents);
			String resultStr = highlighter.getBestFragments(tokenStream, contents, 100, "...");

			res.add(new AdvancedSearchResultItem(location, resultStr));
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

	/* java.util.Enumeration implementation */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		return (pageNum * maxResultPerPage <= results.size());
	}

	/**
	 * @return next search result page as a <code>java.util.List</code> of highlighted
	 *         <code>AdvancedQuranSearchResultItem</code>s.
	 */
	public Object nextElement() {
		List nextPageResult = null;
		nextPageResult = getPage(pageNum + 1);

		pageNum++;
		return nextPageResult;
	}

	public static void main(String[] args) throws Exception {
		String q = "location:[001-001 TO 001-006]"; // "اسل*"
		IndexReader reader = IndexReader.open(new File("c:/temp/sss"));
		IndexSearcher is = new IndexSearcher(reader);
		ArabicAnalyzer myanalyzer = new ArabicAnalyzer();
		QueryParser parser = new QueryParser(QuranTextIndexer.CONTENTS_FIELD, myanalyzer);
		Query query = parser.parse(q);
		BooleanQuery.setMaxClauseCount(MAX_CLAUSE_COUNT);
		query = query.rewrite(reader); // required to expand search terms
		System.out.println("Searching for: " + query.toString());
		Hits hits = is.search(query, Sort.RELEVANCE);
		// TopFieldDocs tops = is.search(query, null, 10, Sort.RELEVANCE);
		// is.search(query, null, new HitCollector() {
		// public void collect(int doc, float score) {
		// System.out.println(doc + " - " + score);
		// }
		// });
		System.out.println(hits.length());
		for (Iterator iter = hits.iterator(); iter.hasNext();) {
			Hit hit = (Hit) iter.next();
			System.out.println(hit.getDocument().toString());
		}

		// for (int i = 0; i < tops.scoreDocs.length; i++) {
		// ScoreDoc scoreDoc = tops.scoreDocs[i];
		// System.out.println(is.doc(scoreDoc.doc));
		// }

		Highlighter highlighter = new Highlighter(new ZekrHighlightFormatter(), new QueryScorer(query));
		for (int i = 0; i < hits.length(); i++) {
			String text = hits.doc(i).get(QuranTextIndexer.CONTENTS_FIELD);
			TokenStream tokenStream = myanalyzer.tokenStream(QuranTextIndexer.CONTENTS_FIELD, new StringReader(text));
			// Get 3 best fragments and seperate with a "..."
			String results = highlighter.getBestFragment(tokenStream, text);
			System.out.println(results);
		}
	}
}

/**
 * Constrains search results to only match those which also match a provided search scope.
 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Filter#bits(org.apache.lucene.index.IndexReader)
	 */
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
	 * @param path
	 *           directory to test against existence of lucene indices.
	 * @return <code>true</code> if some some Lucene indices exists at the specified path, <code>false</code>
	 *         otherwise
	 */
	public boolean indexExists(String path) {
		return IndexReader.indexExists(path);
	}
}
