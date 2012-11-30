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
import net.sf.zekr.engine.addonmgr.CandidateResource;
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
public class ResourceInstallationQuestionListener implements QuestionListener {

   static LanguageEngine lang = ApplicationConfig.getInstance().getLanguageEngine();
   private List<String> errorList = new ArrayList<String>();
   private List<String> addedList = new ArrayList<String>();
   private Resource candidateResource;
   private Display display;
   private boolean cancelled = false;
   private static final Logger logger = Logger.getLogger(ResourceInstallationQuestionListener.class);
   Map<String, Resource> sourceData;
   org.eclipse.swt.widgets.List sourceList;

   public ResourceInstallationQuestionListener(Display display, CandidateResource candidateResource,
         Map<String, Resource> sourceData, org.eclipse.swt.widgets.List sourceList) {
      this.candidateResource = candidateResource;
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
      if (result < 0) {
         return;
      }
      candidateResource.setIsShared(result == 0);

      logger.info("Adding Resource " + candidateResource.getId() + " \"" + getInstallationFile().getName() + "\" to "
            + getDestinationDirectory());

      Thread t = new Thread() {
         public void run() {
            try {
               IntallationProgressListener progressListener = new DefaultIntallationProgressListener(display);
               candidateResource = AddOnManagerUtils.install((CandidateResource) candidateResource, progressListener);
               EventUtils.sendAsyncEvent(display, EventProtocol.IMPORT_PROGRESS_DONE);
               //add the items to the list
               //sourceList.add(candidateResource.getDescription());
               //sourceData.put(candidateResource.getDescription(),candidateResource);
            } catch (ZekrMessageException zme) {
               logger.error("Error importing: " + getInstallationFile(), zme);
               errorList.add(lang.getDynamicMeaning(zme.getMessage(), zme.getParams()));
               display.syncExec(new Runnable() {
                  public void run() {
                     if (errorList.size() > 0) {
                        String str = CollectionUtils.toString(errorList, GlobalConfig.LINE_SEPARATOR);
                        MessageBoxUtils.showError(str);
                     }
                     try {
                        EventUtils.sendEvent(EventProtocol.IMPORT_PROGRESS_FAILED);
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
      if (candidateResource.isShared() == null)
         throw new RuntimeException("Unable to resolve installation directory");
      else
         return this.candidateResource.getInstallationFolder();
   }

   private File getInstallationFile() {
      return candidateResource.getFile();
   }

   public void done() {
      if (!cancelled && candidateResource.isLoaded()) {
         addedList.add(candidateResource.getDescription());
         String rlm = lang.getSWTDirection() == SWT.RIGHT_TO_LEFT ? I18N.RLM + "" : "";
         MessageBoxUtils.showMessage(rlm + lang.getMeaning("ACTION_PERFORMED") + "\n" + lang.getMeaning("RESOURCE_ADDED")
               + ":\n    " + CollectionUtils.toString(addedList, lang.getMeaning("COMMA") + "\n    "));
         String resourceType = candidateResource.getClass().getSimpleName().toUpperCase();
         EventUtils.sendAsyncEvent(display, resourceType + "_IMPORTED");

         if (AddOnManagerUtils.getCurrentResource(candidateResource.getType()) == null) {
            AddOnManagerUtils.setCurrent(candidateResource);
            EventUtils.sendAsyncEvent(display, EventProtocol.NEEDS_RESTART);
         }
      }
   }

   public void cancel() {
      cancelled = true;
   }

}
