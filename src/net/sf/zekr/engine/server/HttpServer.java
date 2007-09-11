/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 31, 2007
 */
package net.sf.zekr.engine.server;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.common.util.UriUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * An abstract HTTP server model. An HTTP server implementation can be accessed through <code>getInstance()</code>.
 * 
 * @author Mohsen Saboorian
 */
public abstract class HttpServer implements Runnable, HttpResourceNaming {
	/** a map of some constant names (defined in {@link HttpResourceNaming}) to thier original normalized path */
	public Map pathLookup = Collections.synchronizedMap(new LinkedHashMap());

	protected HttpServer() {
		pathLookup.put(CACHED_RESOURCE, FilenameUtils.normalize(Naming.getViewCacheDir()));
		pathLookup.put(WORKSPACE_RESOURCE, FilenameUtils.normalize(Naming.getWorkspace()));
		pathLookup.put(BASE_RESOURCE, FilenameUtils.normalize(GlobalConfig.RUNTIME_DIR));
	}

	public static HttpServer getServer() {
		return DefaultHttpServer.getServer();
	}

	/**
	 * @return port on which HTTP server is listening for input connections.
	 */
	abstract public int getPort();

	/**
	 * @return HTTP server address. Examples are <tt>"192.168.0.1"</tt> and <tt>"127.0.0.1"</tt>.
	 * @throws HttpServerRuntimeException
	 */
	abstract public String getAddress() throws HttpServerRuntimeException;

	/**
	 * @return HTTP server URL with a trailing slash. Examples are: <tt>"http://localhost:8920/"</tt> and
	 *         <tt>"http://zekr:80/"</tt>.
	 * @throws HttpServerRuntimeException
	 */
	public String getUrl() throws HttpServerRuntimeException {
		try {
			return UriUtils.toHttpUrl(getAddress(), getPort());
		} catch (Exception e) {
			throw new HttpServerRuntimeException(e);
		}
	}

	public String toUrl(String localPath) {
		String normPath = FilenameUtils.normalize(localPath);
		for (Iterator iter = pathLookup.entrySet().iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			String value = entry.getValue().toString();
			if (normPath.startsWith(value))
				return getUrl() + entry.getKey() + "/"
						+ FilenameUtils.separatorsToUnix(normPath.substring(value.length() + 1));
		}
		return "";
	}

	public String toRealPath(String url) {
		String baseDir;
		String path;
		if (url.startsWith(CACHED_RESOURCE)) {
			baseDir = (String) pathLookup.get(CACHED_RESOURCE);
			path = baseDir + "/" + url.substring(CACHED_RESOURCE.length());
		} else if (url.startsWith(WORKSPACE_RESOURCE)) {
			baseDir = Naming.getWorkspace();
			path = baseDir + "/" + url.substring(WORKSPACE_RESOURCE.length());
		} else if (url.startsWith(BASE_RESOURCE)) {
			baseDir = (String) pathLookup.get(BASE_RESOURCE);
			path = baseDir + "/" + url.substring(BASE_RESOURCE.length());
		} else if (url.startsWith(WORKSPACE_OR_BASE_RESOURCE)) {
			path = toRealPath(StringUtils.replace(url, WORKSPACE_OR_BASE_RESOURCE, WORKSPACE_RESOURCE));
			if (!new File(path).exists())
				path = toRealPath(StringUtils.replace(url, WORKSPACE_OR_BASE_RESOURCE, BASE_RESOURCE));
		} else {
			path = url;
		}
		return new File(path).getAbsolutePath();
	}
}
