/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jul 10, 2009
 */
package net.sf.zekr.engine.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * All Internet accesses should be through this class, in order to apply proxy settings to all accesses.
 * 
 * @author Mohsen Saboorian
 */
public class NetworkController {
	private ProxySelector proxySelector;
	private PropertiesConfiguration props;
	private String defaultProxy;
	private String proxyType;
	private String proxyServer;
	private int proxyPort;

	public static final String SYSTEM_PROXY = "system";
	public static final String MANUAL_PROXY = "manual";

	public NetworkController(PropertiesConfiguration props) {
		this.props = props;

		if (!props.getBoolean("network.proxy.internal.disableJvmUseSystemProxy", false)) {
			System.setProperty("java.net.useSystemProxies", "true");
		}

		proxySelector = ProxySelector.getDefault();
		defaultProxy = props.getString("network.proxy", "system");
		proxyType = props.getString("network.proxy.type", "HTTP");
		proxyServer = props.getString("network.proxy.server", "127.0.0.1");
		String port = props.getString("network.proxy.port");
		if (StringUtils.isNotBlank(port)) {
			proxyPort = Integer.parseInt(port);
		} else {
			proxyPort = 80;
		}
	}

	public Proxy getProxy(String uri) throws URISyntaxException {
		Proxy proxy;
		if (SYSTEM_PROXY.equalsIgnoreCase(defaultProxy)) {
			List<Proxy> proxyList = proxySelector.select(new URI(uri));
			proxy = (Proxy) proxyList.get(0);
			if (proxy.address() == null) {
				proxy = Proxy.NO_PROXY;
			}
		} else if (MANUAL_PROXY.equalsIgnoreCase(defaultProxy)) {
			SocketAddress sa = InetSocketAddress.createUnresolved(proxyServer, proxyPort);
			Proxy.Type type = Proxy.Type.HTTP.name().equalsIgnoreCase(proxyType) ? Proxy.Type.HTTP : Proxy.Type.SOCKS
					.name().equalsIgnoreCase(proxyType) ? Proxy.Type.SOCKS : Proxy.Type.DIRECT;
			proxy = new Proxy(type, sa);
		} else {
			proxy = Proxy.NO_PROXY;
		}
		return proxy;
	}

	/**
	 * Open a connection to a URI using application proxy or {@link Proxy#NO_PROXY no proxy}.
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public InputStream openSteam(String uri) throws URISyntaxException, IOException {
		URL url = new URL(uri);
		URLConnection conn = url.openConnection(getProxy(uri));
		return conn.getInputStream();
	}

	public InputStream openSteam(String uri, int timeout) throws URISyntaxException, IOException {
		URL url = new URL(uri);
		URLConnection conn = url.openConnection(getProxy(uri));
		conn.setReadTimeout(timeout);
		conn.setUseCaches(true);
		return conn.getInputStream();
	}
}
