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

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.engine.log.Logger;

import org.apache.commons.io.FilenameUtils;

/**
 * This class intends to implement a simple HTTP server based on NanoHTTPD. It loops infinitely until it is interrupted.
 * 
 * @author Mohsen Saboorian
 */
public class DefaultHttpServer extends HttpServer {
	private static final Logger logger = Logger.getLogger(DefaultHttpServer.class);
	private static final ResourceManager res = ResourceManager.getInstance();
	private NanoHttpd httpFacade = null;

	private static HttpServer thisInstance = new DefaultHttpServer();

	private DefaultHttpServer() {
	}

	public static HttpServer getServer() {
		return thisInstance;
	};

	public void run() {
		try {
			logger.info("Starting HTTP server...");
			httpFacade = new NanoHttpd(getServerPort()) {
				private ApplicationConfig config = ApplicationConfig.getInstance();

				public Response serve(String uri, String method, Properties header, Properties parms) {
					if (!fromThisMachine(header) || !isAllowedUri(uri)) {
						return new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "Access denied.");
					}
					if (Boolean.valueOf(config.getProps().getString("server.http.log")).booleanValue())
						logger.debug("serving URI: " + uri);
					String path = toRealPath(uri.substring(1));
					if (!new File(path).exists())
						return new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "File not found.");
					String baseDir = FilenameUtils.getFullPath(path);
					String fileName = FilenameUtils.getName(path);
					return serveFile(fileName, header, new File(baseDir), false);
				}

				private boolean isAllowedUri(String uri) {
					return (uri.indexOf("..") == -1) && (uri.indexOf(":/") == -1) && (uri.indexOf(":\\") == -1);
				}

				private boolean fromThisMachine(Properties header) {
					String deny = config.getProps().getString("server.http.denyRemoteAccess");
					if (!new Boolean(deny).booleanValue())
						return true;
					String remoteAddress = (String) header.get(HEADER_REQUEST_ADDRESS);
					String localAddr = "", localName = "";
					localAddr = getServerAddress();
					localName = getServerName();
					if (!localAddr.equals(remoteAddress) && !localName.equals(remoteAddress)) {
						logger.info("Unauthorized request to server from " + remoteAddress);
						return false;
					}
					return true;
				}
			};
			logger.info("HTTP server is listening on: " + getUrl());
		} catch (IOException ioe) {
			logger.error("HTTP server cannot be started due to the next error.");
			logger.implicitLog(ioe);
			return;
		}
		while (true) {
			try {
				// do nothing, there is a saparate waiting thread for each request.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.info("HTTP Server terminated.");
			}
		}
	}

	private static int getServerPort() {
		int port = ApplicationConfig.getInstance().getHttpServerPort();
		return port == -1 ? 8920 : port;
	}

	private static String getServerName() throws HttpServerRuntimeException {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			throw new HttpServerRuntimeException(e);
		}
	}

	static String getServerAddress() throws HttpServerRuntimeException {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new HttpServerRuntimeException(e);
		}
	}

	public String getCanonicalAddress() throws HttpServerRuntimeException {
		return getServerName();
	}

	public int getPort() {
		return getServerPort();
	}
}
