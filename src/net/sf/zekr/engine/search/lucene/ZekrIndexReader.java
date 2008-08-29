/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 17, 2008
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.engine.translation.TranslationData;

import org.apache.lucene.index.IndexReader;

/**
 * This is a wrapper class for Lucene {@link IndexReader}. It holds the ID for the enclosing translation, if
 * {@link IQuranText} is a {@link TranslationData}.
 * 
 * @author Mohsen Saboorian
 */
public class ZekrIndexReader {
	public IndexReader indexReader;
	public IQuranText quranText;
	public String id;

	public ZekrIndexReader(IQuranText quranText, String id, IndexReader indexReader) {
		this.indexReader = indexReader;
		this.id = id;
		this.quranText = quranText;
	}
}
