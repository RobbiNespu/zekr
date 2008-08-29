/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 25, 2007
 */
package net.sf.zekr.engine.search.lucene;

/**
 * Generic Indexer interface
 */
public interface IIndexer {
	/**
	 * Performs all the indexing process.
	 * 
	 * @throws IndexingException if any exception occurred during indexing process
	 * @throws InterruptedException if indexing thread interrupted
	 */
	void doIndex() throws IndexingException, InterruptedException;
}
