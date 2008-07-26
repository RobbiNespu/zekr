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
 * Exception class, thrown when an error occurs during indexing a document.
 * 
 * @author Mohsen Saboorian
 */
public class IndexingException extends Exception {
	private static final long serialVersionUID = 7212085304024780204L;

	public IndexingException(String msg) {
		super(msg);
	}

	public IndexingException(Throwable th) {
		super(th);
	}
}
