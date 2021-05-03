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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.util.FileUtils;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.update.ui.UpdateForm;
import net.sf.zekr.engine.xml.XmlReader;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.ProgressForm;
import net.sf.zekr.ui.QuranForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TaskItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Zekr update manager. This class is capable of checking for new updates available on the remote site of Zekr.
 * 
 * @author Mohsen Saboorian
 */
public class UpdateManager {
   public enum State {
      IDLE, CHECKING
   }

   final private Logger logger = Logger.getLogger(this.getClass());
   final private ApplicationConfig config = ApplicationConfig.getInstance();
   final LanguageEngine lang = LanguageEngine.getInstance();
   final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
   final PropertiesConfiguration props = config.getProps();

   private Display display;
   private QuranForm quranForm;

   public boolean updateCheckFinished = false;
   public boolean updateCheckFailed = false;
   protected Exception failureCause;
   private UpdateInfo updateInfo;
   private State state = State.IDLE;

   public UpdateManager(QuranForm quranForm) {
      this.quranForm = quranForm;
      display = quranForm.getDisplay();
   }

   /**
    * Checks whether this is time for checking for a new update.
    * 
    * @return <code>true</code> if this is the time for checking, <code>false</code> otherwise.
    */
   public boolean isCheckNeeded() {
      if (state != State.IDLE) {
         // a checking is already started
         return false;
      }
      try {
         Date lastUpdate = dateFormat.parse(config.getProps().getString("update.lastCheck", "01-01-2008"));
         long interval = props.getInt("update.checkInterval", 14);
         Date today = new Date();
         long diffInMillis = today.getTime() - lastUpdate.getTime();
         long days = diffInMillis / 86400000; // 24 * 60 * 60 * 1000
         if (days >= interval) {
            return true;
         }
      } catch (ParseException e) {
         logger.implicitLog(e);
      }
      return false;
   }

   public boolean check(final boolean manual) {
      if (quranForm == null || quranForm.getShell() == null || quranForm.getShell().isDisposed()) {
         logger.warn("Cannot check for update as main shell is disposed.");
      }
      display.asyncExec(new Runnable() {
         public void run() {
            try {
               state = State.CHECKING;
               logger.debug("Start update checking in a separate thread.");

               TaskItem tbi = FormUtils.getTaskBarItem(display, quranForm.getShell());
               if (tbi != null) {
                  tbi.setProgressState(SWT.INDETERMINATE);
               }

               if (manual) {
                  display.asyncExec(new ProgressThread(display, checkThread));
               } else {
                  try {
                     Thread.sleep(2000); // wait some seconds to ensure that application is fully started up
                  } catch (InterruptedException e) {
                     // damp exception
                  }
               }

               checkThread.setDaemon(true);
               checkThread.start();

               while (checkThread.isAlive()) {
                  if (!display.readAndDispatch()) {
                     display.sleep();
                  }
               }

               // set new update time regardless of the success or failure of check4update process
               props.setProperty("update.lastCheck", dateFormat.format(new Date()));

               if (tbi != null) {
                  tbi.setProgressState(SWT.DEFAULT);
               }

               // update checking should either fail (updateCheckFailed = true), or finish (updateCheckFinished)!
               if (manual && updateCheckFailed) {
                  MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + failureCause);
               } else if (updateCheckFinished) {
                  String msg;
                  if (Long.parseLong(updateInfo.build) > Long.parseLong(GlobalConfig.ZEKR_BUILD_NUMBER)) {
                     if (updateInfo.status.equals(UpdateInfo.BETA_RELEASE)) {
                        msg = meaning("NEW_BETA_AVAILABLE");
                     } else if (updateInfo.status.equals(UpdateInfo.FINAL_RELEASE)) {
                        msg = meaning("NEW_FINAL_AVAILABLE");
                     } else { // if (updateInfo.status.equals(UpdateInfo.DEV_RELEASE))
                        msg = meaning("NEW_DEV_AVAILABLE");
                     }
                     updateInfo.message = msg + ": " + updateInfo.fullName;
                     UpdateForm uf = new UpdateForm(updateInfo, quranForm.getShell());
                     Shell ufs = uf.getShell();
                     FormUtils.limitSize(ufs, 500, 380);
                     ufs.setLocation(FormUtils.getCenter(quranForm.getShell(), ufs));
                     uf.show();
                  } else {
                     if (manual) {
                        MessageBoxUtils.show(meaning("NO_UPDATE"), meaning("TITLE"), SWT.NONE);
                     }
                  }
               }
            } catch (Exception e) {
               logger.error("Error occurred while checking for update.");
               logger.implicitLog(e);
            } finally {
               state = State.IDLE;
            }
         }
      });

      return updateCheckFinished;
   }
   private class ProgressThread extends Thread {
      private Thread updateThread;
      private Display display;

      public ProgressThread(Display display, Thread updateThread) {
         this.display = display;
         this.updateThread = updateThread;
      }

      public void run() {
         ProgressForm pf = new ProgressForm(MessageBoxUtils.getShell(display), meaning("PLEASE_WAIT"), meaning("CHECKING")
               + "..." + "\n\n" + meaning("ZEKR_IS_CHECKING"));
         pf.show();
         while (!pf.getShell().isDisposed()) {
            if (updateCheckFinished || updateCheckFailed) {
               EventUtils.sendEvent(EventProtocol.END_WAITING);
            }
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
            String uri = GlobalConfig.UPDATE_SITE + "/update-info.xml";
            logger.info("Checking for any update on the remote site: " + uri);
            InputStream is;
            is = FileUtils.getContent(uri);

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
               if (cn.getLength() > 0) {
                  updateInfo.info = cn.item(0).getNodeValue();
               }
            }
            updateCheckFinished = true;
         } catch (Exception e) {
            updateCheckFailed = true;
            failureCause = e;
            logger.error("Zekr failed to check for updates.");
            logger.implicitLog(e);
         }
      }
   };

   private String meaning(String key) {
      return lang.getMeaningById("UPDATE", key);
   }
}
