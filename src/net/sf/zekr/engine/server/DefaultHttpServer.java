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
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;

/**
 * This class intends to implement a simple HTTP server based on NanoHTTPD. It loops infinitely until it is interrupted.
 * 
 * @author Mohsen Saboorian
 */
public class DefaultHttpServer extends HttpServer {
	private static final Logger logger = Logger.getLogger(DefaultHttpServer.class);
	private static final ResourceManager res = ResourceManager.getInstance();
	private NanoHttpd httpFacade = null;

	public void run() {
		try {
			httpFacade = new NanoHttpd(getServerPort()) {
				private ApplicationConfig config = ApplicationConfig.getInstance();

				public Response serve(String uri, String method, Properties header, Properties parms) {
					if (!fromThisMachine(header)) {
						return new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "Remote server access denied.");
					}
					if (Boolean.TRUE.equals(config.getProps().getString("server.http.log")))
						logger.debug("serving URI: " + uri);
					String baseDir = ".";
					if (uri.startsWith("/" + HttpResourceNaming.CACHED_RESOURCE)) {
						baseDir = Naming.getCacheDir();
						uri = uri.substring(1 + HttpResourceNaming.CACHED_RESOURCE.length());
					} else {
						baseDir = GlobalConfig.RUNTIME_DIR;
					}
					// Response resp = new Response(HTTP_OK, (String) NanoHttpd.MIME_TYPES.get("html"), "salam!");
					return serveFile(uri, header, new File(baseDir), false);
				}

				private boolean fromThisMachine(Properties header) {
					String deny = config.getProps().getString("server.http.denyRemoteAccess");
					if (new Boolean(deny).booleanValue())
						return false;
					String remoteAddress = (String) header.get(HEADER_REQUEST_ADDRESS);
					String localAddr = "", localName = "";
					try {
						localAddr = getServerAddress();
						localName = getServerName();
					} catch (UnknownHostException e) {
						logger.warn(e);
					}
					if (!localAddr.equals(remoteAddress) && !localName.equals(remoteAddress)) {
						logger.info("Unauthorized request to server from " + remoteAddress);
						return false;
					}
					return true;
				}
			};
		} catch (IOException ioe) {
			logger.error("HTTP server cannot be started due to the next error.");
			logger.implicitLog(ioe);
			return;
		}
		while (true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				logger.info("HTTP Server terminated.");
			}
		}
	}

	private static int getServerPort() {
		int port = ApplicationConfig.getInstance().getHttpServerPort();
		return port == -1 ? 8920 : port;
	}

	private static String getServerName() throws UnknownHostException {
		return InetAddress.getLocalHost().getCanonicalHostName();
	}

	private static String getServerAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	public String getCanonicalAddress() throws HttpServerException {
		try {
			return getServerName();
		} catch (UnknownHostException e) {
			throw new HttpServerException(e);
		}
	}

	public int getPort() throws HttpServerException {
		return getServerPort();
	}
}
