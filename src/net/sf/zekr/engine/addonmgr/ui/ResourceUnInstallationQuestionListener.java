/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     31/07/2010
 */
package net.sf.zekr.engine.addonmgr.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.zekr.common.ZekrMessageException;
import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.util.CollectionUtils;
import net.sf.zekr.common.util.I18N;
import net.sf.zekr.common.util.IntallationProgressListener;
import net.sf.zekr.engine.addonmgr.AddOnManagerUtils;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.QuestionListener;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * @author Mohsen Saboorian
 */
public class ResourceUnInstallationQuestionListener implements QuestionListener {

   static LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
   private List<String> errorList = new ArrayList<String>();
   private List<String> removedList = new ArrayList<String>();
   private Resource resourceToUnInstall;
   private Display display;
   private boolean cancelled = false;
   private static final Logger logger = Logger.getLogger(ResourceUnInstallationQuestionListener.class);
   Map<String, Resource> sourceData;
   org.eclipse.swt.widgets.List sourceList;

   public ResourceUnInstallationQuestionListener(Display display, Resource installedResource,
         Map<String, Resource> sourceData, org.eclipse.swt.widgets.List sourceList) {
      this.resourceToUnInstall = installedResource;
      this.display = display;
      this.sourceData = sourceData;
      this.sourceList = sourceList;
   }

   /* This method is called when the question is answered
    * @param result is an integer that represent the user answer
    * (non-Javadoc)
    * @see net.sf.zekr.ui.QuestionListener#start(int)
    */
   public void start(int result) {
      if (result == 1) {
         cancel();
         EventUtils.sendEvent(EventProtocol.IMPORT_PROGRESS_DONE);
         return;
      }

      logger.info("Removing resource " + resourceToUnInstall.getId() + " \"" + getInstallationFile().getName()
            + "\" to " + getDestinationDirectory());
      Thread t = new Thread() {
         public void run() {
            try {
               IntallationProgressListener progressListener = new DefaultIntallationProgressListener(display);
               AddOnManagerUtils.unistall(resourceToUnInstall, progressListener);
               EventUtils.sendAsyncEvent(display, EventProtocol.IMPORT_PROGRESS_DONE);

            } catch (ZekrMessageException zme) {
               logger.error("Error removing resource: " + getInstallationFile(), zme);
               errorList.add(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
               display.syncExec(new Runnable() {
                  public void run() {
                     if (errorList.size() > 0) {
                        String str = CollectionUtils.toString(errorList, GlobalConfig.LINE_SEPARATOR);
                        MessageBoxUtils.showError(str);
                     }
                     try {
                        EventUtils.sendEvent(EventProtocol.RESOURCE_REMOVAL_FAILED);
                     } catch (Exception e) {
                        logger.implicitLog(e);
                     }
                  }
               });
            }
         };
      };
      t.setDaemon(true);
      t.start();

   }

   private String getDestinationDirectory() {
      if (resourceToUnInstall.isShared() == null)
         throw new RuntimeException("Unable to resolve installation directory");
      else
         return this.resourceToUnInstall.getInstallationFolder();
   }

   private File getInstallationFile() {
      return resourceToUnInstall.getFile();
   }

   public void done() {
      if (!cancelled && !resourceToUnInstall.isLoaded()) {
         removedList.add(resourceToUnInstall.getDescription());
         String rlm = lang.getSWTDirection() == SWT.RIGHT_TO_LEFT ? I18N.RLM + "" : "";
         MessageBoxUtils.showMessage(rlm + lang.getMeaning("ACTION_PERFORMED") + "\n"
               + lang.getMeaning("RESOURCE_REMOVED") + ":\n    "
               + CollectionUtils.toString(removedList, lang.getMeaning("COMMA") + "\n    "));
         String resourceType = resourceToUnInstall.getClass().getSimpleName().toUpperCase();
         EventUtils.sendAsyncEvent(display, resourceType + "_REMOVED");
         if (AddOnManagerUtils.getLoadedResources(resourceToUnInstall.getType()).isEmpty()) {
            MessageBoxUtils.showMessage(lang.getMeaning("RESTART_APP"));
         }

      }
   }

   public void cancel() {
      cancelled = true;
   }

}
