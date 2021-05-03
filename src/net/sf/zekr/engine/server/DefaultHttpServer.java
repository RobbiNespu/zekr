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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;

/**
 * This class intends to implement a simple HTTP server based on NanoHTTPD. It loops infinitely until it is
 * interrupted.
 * 
 * @author Mohsen Saboorian
 */
public class DefaultHttpServer extends HttpServer {
	private final Logger logger = Logger.getLogger(this.getClass());
	private final ResourceManager res = ResourceManager.getInstance();
	private NanoHttpd httpFacade = null;
	private PropertiesConfiguration props;

	DefaultHttpServer(PropertiesConfiguration props) {
		this.props = props;
	}

	public void run() {
		try {
			logger.info("Starting HTTP server...");
			final boolean denyRemoteAccess = props.getBoolean("server.http.denyRemoteAccess", true);
			httpFacade = new NanoHttpd(getServerPort(), denyRemoteAccess) {
				public Response serve(String uri, String method, Properties header, Properties parms) {
					if (!hasAccessPermission(uri)) {
						return new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "Access denied.");
					}
					if (Boolean.valueOf(props.getString("server.http.log")).booleanValue())
						logger.debug("serving URI: " + uri);
					String path = toRealPath(uri.substring(1));
					if (!new File(path).exists())
						return new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "File not found.");
					String baseDir = FilenameUtils.getFullPath(path);
					String fileName = FilenameUtils.getName(path);
					return serveFile(fileName, header, new File(baseDir), false);
				}

				private boolean hasAccessPermission(String uri) {
					if (denyRemoteAccess) {
						return true;
					} else {
						return isAllowedUri(uri);
					}
				}

				private boolean isAllowedUri(String uri) {
					return (uri.indexOf("..") == -1) && (uri.indexOf(":/") == -1) && (uri.indexOf(":\\") == -1);
				}
			};
			logger.info("HTTP server is listening on: " + getUrl());
		} catch (IOException ioe) {
			logger.error("HTTP server cannot be started due to the next error.");
			logger.implicitLog(ioe);
			return;
		}
//		while (true) {
//			try {
//				// do nothing, there is a separate waiting thread for each request.
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				logger.info("HTTP Server terminated.");
//			}
//		}
	}

	private int getServerPort() {
		return props.getInt("server.http.port", 8920);
	}

	private String getServerName() throws HttpServerRuntimeException {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			throw new HttpServerRuntimeException(e);
		}
	}

	private String getServerAddress() throws HttpServerRuntimeException {
		// return InetAddress.getLocalHost().getHostAddress();
		return props.getString("server.http.address", "127.0.0.1");
	}

	public String getAddress() throws HttpServerRuntimeException {
		return getServerAddress();
	}

	public int getPort() {
		return getServerPort();
	}
}
