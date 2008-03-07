/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 14, 2008
 */
package net.sf.zekr.engine.update;

import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.util.FileUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.ProgressForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UpdateManager {
	final private Logger logger = Logger.getLogger(this.getClass());
	final private ApplicationConfig config = ApplicationConfig.getInstance();
	final LanguageEngine lang = LanguageEngine.getInstance();
	final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	final PropertiesConfiguration props = config.getProps();

	private Shell shell;
	Display display;

	public boolean updateCheckFinished = false;
	public boolean updateCheckFailed = false;
	protected Exception failureCause;
	private UpdateInfo updateInfo;

	public UpdateManager(Shell shell) {
		this.shell = shell;
		this.display = shell.getDisplay();
	}

	/**
	 * Checks whether this is time for checking for a new update.
	 * 
	 * @return <code>true</code> if this is the time for checking, <code>false</code> otherwise.
	 */
	public boolean isCheckNeeded() {
		try {
			Date lastUpdate = dateFormat.parse(config.getProps().getString("update.lastCheck", "2008-01-01"));
			long interval = props.getInt("update.checkInterval", 14);
			Date today = new Date();
			long diffInMillis = today.getTime() - lastUpdate.getTime();
			long days = diffInMillis / 86400000; // 24 * 60 * 60 * 1000
			if (days >= interval)
				return true;
		} catch (ParseException e) {
			logger.implicitLog(e);
		}
		return false;
	}

	public boolean check() {
		display.asyncExec(new ProgressThread(checkThread));

		logger.debug("Start update checking in a separate thread.");
		checkThread.setDaemon(true);
		checkThread.start();

		while (checkThread.isAlive()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		props.setProperty("update.lastCheck", dateFormat.format(new Date()));

		// update checking should either fail (updateCheckFailed = true), or finish (updateCheckFinished)!
		if (updateCheckFailed) {
			MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + ":\n" + failureCause);
		} else if (updateCheckFinished) {
			String msg = "";
			if (updateInfo.build.equals(GlobalConfig.ZEKR_BUILD_NUMBER)) {
				msg = meaning("NO_UPDATE");
			} else if (Long.parseLong(updateInfo.build) > Long.parseLong(GlobalConfig.ZEKR_BUILD_NUMBER)) {
				if (updateInfo.status.equals(UpdateInfo.DEV_RELEASE)) {
					msg = meaning("NEW_DEV_AVAILABLE");
				} else if (updateInfo.status.equals(UpdateInfo.BETA_RELEASE)) {
					msg = meaning("NEW_BETA_AVAILABLE");
				} else if (updateInfo.status.equals(UpdateInfo.FINAL_RELEASE)) {
					msg = meaning("NEW_FINAL_AVAILABLE");
				}
			}
			updateInfo.message = msg + ": " + updateInfo.fullName;
			UpdateForm uf = new UpdateForm(updateInfo, shell);
			Shell ufs = uf.getShell();
			FormUtils.limitSize(ufs, 500, 380);
			ufs.setLocation(FormUtils.getCenter(shell, ufs));
			uf.show();
		}

		return updateCheckFinished;
	}
	private class ProgressThread extends Thread {
		private Thread updateThread;

		public ProgressThread(Thread updateThread) {
			this.updateThread = updateThread;
		}

		public void run() {
			ProgressForm pf = new ProgressForm(MessageBoxUtils.getShell(), meaning("PLEASE_WAIT"), meaning("CHECKING")
					+ "..." + "\n\n" + meaning("ZEKR_IS_CHECKING"));
			pf.show();
			while (!pf.getShell().isDisposed()) {
				if (updateCheckFinished || updateCheckFailed)
					EventUtils.sendEvent(EventProtocol.END_WAITING);
				if (!pf.getDisplay().readAndDispatch()) {
					pf.getDisplay().sleep();
				}
			}
			if (pf.getState() == ProgressForm.CALCELED && !updateThread.isInterrupted()) {
				logger.debug("Update checking cancelled by user.");
				updateThread.interrupt();
			}
		}

		String meaning(String key) {
			return lang.getMeaningById("PROGRESS", key);
		}
	}

	private Thread checkThread = new Thread() {
		public void run() {
			try {
				String url = config.getProps().getString("update.address") + "/update-info.xml";
				logger.info("Checking for any update on the remote site: " + url);
				InputStream is = FileUtils.getContent(new URI(url).toURL());

				logger.debug("Parse update info XML.");
				XmlReader xr = new XmlReader(is);
				is.close();

				Element root = xr.getDocumentElement();
				updateInfo = new UpdateInfo();
				updateInfo.fullName = root.getAttribute("fullName").trim();
				updateInfo.version = root.getAttribute("version").trim();
				updateInfo.status = root.getAttribute("status").trim();
				updateInfo.build = root.getAttribute("build").trim();
				updateInfo.downloadUrl = root.getAttribute("downloadUrl").trim();
				updateInfo.noteUrl = root.getAttribute("noteUrl").trim();
				try {
					updateInfo.releaseDate = dateFormat.parse(root.getAttribute("date").trim());
				} catch (ParseException e) {
					logger.debug("Unable to parse date: " + root.getAttribute("date"));
					// do nothing!
				}
				NodeList infoList = root.getElementsByTagName("info");
				if (infoList.getLength() > 0) {
					NodeList cn = infoList.item(0).getChildNodes();
					if (cn.getLength() > 0)
						updateInfo.info = cn.item(0).getNodeValue();
				}
				updateCheckFinished = true;
			} catch (Exception e) {
				updateCheckFailed = true;
				failureCause = e;
				logger.implicitLog(e);
				logger.error("Failed to check for update!");
			}
		}
	};

	private String meaning(String key) {
		return lang.getMeaningById("UPDATE", key);
	}
}
