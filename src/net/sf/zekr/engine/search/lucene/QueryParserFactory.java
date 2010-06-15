/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jun 13, 2010
 */
package net.sf.zekr.engine.search.lucene;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sf.zekr.common.config.ApplicationConfig;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

/**
 * @author Mohsen Saboorian
 */
public class QueryParserFactory {
	public static ApplicationConfig conf = ApplicationConfig.getInstance();

	@SuppressWarnings("unchecked")
	public static QueryParser create(Version version, String field, Analyzer analyzer) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException,
			NoSuchMethodException, ClassNotFoundException {
		String clazz = conf.getProps().getString("view.search.advanced.queryParser",
				"org.apache.lucene.queryParser.QueryParser");
		Constructor<? extends QueryParser> ctor = (Constructor<? extends QueryParser>) Class.forName(clazz)
				.getConstructor(Version.class, String.class, Analyzer.class);
		return ctor.newInstance(version, field, analyzer);
	}
}
