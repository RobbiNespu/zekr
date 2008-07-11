/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 5, 2007
 */
package net.sf.zekr.engine.server;

import net.sf.zekr.ZekrBaseTest;
import net.sf.zekr.common.config.ApplicationConfig;

import org.apache.commons.io.FilenameUtils;

public class DefaultHttpServerTest extends ZekrBaseTest {
	HttpServer server = new DefaultHttpServer(ApplicationConfig.getInstance().getProps());

	public DefaultHttpServerTest() throws Exception {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetRealPath() throws Exception {
		String url = "[base]/path/to/somewhere";
		String path = (String) server.pathLookup.get(HttpServer.BASE_RESOURCE);
		path = FilenameUtils.normalize(path + "/path/to/somewhere");
		assertEquals(path, server.toRealPath(url));

		url = "[workspace]/path/to/somewhere";
		path = (String) server.pathLookup.get(HttpServer.WORKSPACE_RESOURCE);
		path = FilenameUtils.normalize(path + "/path/to/somewhere");
		assertEquals(path, server.toRealPath(url));

		url = "[cache]/path/to/somewhere";
		path = (String) server.pathLookup.get(HttpServer.CACHED_RESOURCE);
		path = FilenameUtils.normalize(path + "/path/to/somewhere");
		assertEquals(path, server.toRealPath(url));
	}

	public void testToUrl() throws Exception {
		String path = (String) server.pathLookup.get(HttpServer.BASE_RESOURCE);
		path = FilenameUtils.normalize(path + "/path/to/somewhere");
		assertEquals(server.getUrl() + HttpServer.BASE_RESOURCE + "/path/to/somewhere", server.toUrl(path));

		path = (String) server.pathLookup.get(HttpServer.WORKSPACE_RESOURCE);
		path = FilenameUtils.normalize(path + "/path/to/somewhere");
		assertEquals(server.getUrl() + HttpServer.WORKSPACE_RESOURCE + "/path/to/somewhere", server.toUrl(path));

		path = (String) server.pathLookup.get(HttpServer.CACHED_RESOURCE);
		path = FilenameUtils.normalize(path + "/path/to/somewhere");
		assertEquals(server.getUrl() + HttpServer.CACHED_RESOURCE + "/path/to/somewhere", server.toUrl(path));
	}
}
