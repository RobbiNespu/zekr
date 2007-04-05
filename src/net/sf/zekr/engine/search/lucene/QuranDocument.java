/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 23, 2007
 */
package net.sf.zekr.engine.search.lucene;

import net.sf.zekr.common.resource.IQuranText;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * @author Mohsen Saboorian
 * @since Zekr 1.0
 */
public class QuranDocument {
	IQuranText quran;

	public QuranDocument(IQuranText quran) {
		this.quran = quran;
	}

	public Document createDocument() {
		Document doc = new Document();

		for (int sura = 1; sura <= 114; sura++) {
			int ayaCount = quran.getSura(sura).length;
			for (int aya = 1; aya <= ayaCount; aya++) {
				doc.add(new Field("contents", quran.get(sura, aya), Field.Store.COMPRESS, Field.Index.TOKENIZED));
			}
		}

		return doc;
	}
}
