/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Jan 31, 2007
 */
package net.sf.zekr.engine.addonmgr.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.engine.addonmgr.AddOnManagerUtils;
import net.sf.zekr.engine.addonmgr.CandidateResource;
import net.sf.zekr.engine.addonmgr.Resource;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.translation.TranslationData;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.QuestionPromptForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Customize multi-translation layout.
 * 
 * @author Mohsen Saboorian
 */
public class AddOnManagerForm extends BaseForm {
   public static final String FORM_ID = "ADDON_MANAGER";

   //private Button okBut;
   //private Button cancelBut;
   //private List sourceList;//, targetList;
   //private Button addBut;
   //private Button remBut;
   //private Button upBut;
   //private Button downBut;

   //private java.util.List<String> targetData = new ArrayList<String>();
   //private boolean okayed = false;

   public AddOnManagerForm(final Shell parent) {
      try {
         this.parent = parent;
         display = parent.getDisplay();
         shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

         shell.setLayout(new FillLayout());
         shell.setText(meaning("TITLE"));
         shell.setImage(new Image(display, resource.getString("icon.menu.add")));

         shell.addListener(EventProtocol.CUSTOM_ZEKR_EVENT, new Listener() {
            public void handleEvent(Event event) {
               if (EventProtocol.TRANSLATION_IMPORTED.equals(event.data)
                     || EventProtocol.RECITATION_IMPORTED.equals(event.data)
                     || EventProtocol.TRANSLATION_REMOVED.equals(event.data)
                     || EventProtocol.RECITATION_REMOVED.equals(event.data)) {
                  shell.close();
               }
            }
         });

         init();
      } catch (RuntimeException re) {
         FormUtils.disposeGracefully(shell);
         throw re;
      }
   }

   /**
	 * 
	 */
   private void init() {
      GridLayout gl = new GridLayout(1, false);
      Composite formBody = new Composite(shell, lang.getSWTDirection());
      formBody.setLayout(gl);

      GridData gd = new GridData(GridData.FILL_BOTH);

      TabFolder resourceTabs = new TabFolder(formBody, lang.getSWTDirection());
      resourceTabs.setLayoutData(gd);

      //gl = new GridLayout(4, false);
      gl = new GridLayout(2, false);

      createResourceTab("TRANSLATION", TranslationData.class, gl, formBody, gd, resourceTabs);
      createResourceTab("RECITATION", AudioData.class, gl, formBody, gd, resourceTabs);

   }

   private Composite createResourceTab(String resourceName, final Class resourceType, GridLayout gl,
         Composite formBody, GridData gd, TabFolder resourceTabs) {

      final List sourceList;//, targetList;
      final Button addBut;
      final Button remBut;
      final Map<String, Resource> sourceData = new HashMap<String, Resource>();
      final java.util.List<Resource> listOfResources = AddOnManagerUtils.getLoadedResources(resourceType);

      TabItem resourceTab = new TabItem(resourceTabs, SWT.NONE);
      resourceTab.setText(meaning(resourceName));

      Composite body = new Group(resourceTabs, SWT.NONE);
      resourceTab.setControl(body);

      body.setLayoutData(gd);
      body.setLayout(gl);

      gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 4;
      Label sourceLabel = new Label(body, SWT.NONE);
      sourceLabel.setText(meaning("AVAILABLE") + ":");
      sourceLabel.setLayoutData(gd);

      gd = new GridData(GridData.FILL_BOTH);
      gd.widthHint = 200;
      sourceList = new List(body, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
      sourceList.setLayoutData(gd);

      String[] sourceItems = new String[listOfResources.size()];

      int i = 0;
      for (Iterator<Resource> iter = listOfResources.iterator(); iter.hasNext(); i++) {
         Resource resource = iter.next();
         sourceItems[i] = resource.getDescription();
         sourceData.put(resource.getDescription(), resource);
      }
      sourceList.setItems(sourceItems);

      // isRTL is only applicable for Windows
      int d = lang.getSWTDirection();
      int direction = (GlobalConfig.isWindows ? d : SWT.LEFT_TO_RIGHT);

      gd = new GridData(SWT.CENTER);
      RowLayout rl = new RowLayout(SWT.VERTICAL);
      Composite addRemComp = new Composite(body, direction);
      addRemComp.setLayout(rl);
      addRemComp.setLayoutData(gd);

      addBut = new Button(addRemComp, SWT.PUSH);
      addBut.setText(meaning("ADD_CUSTOM"));
      addBut.pack(); // we pack to set the length
      addBut.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            add(sourceData, sourceList, addBut, resourceType);
         }
      });

      remBut = new Button(addRemComp, SWT.PUSH);
      remBut.setText(meaning("REMOVE_CUSTOM"));
      remBut.pack(); // the same as for addBut
      remBut.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            rem(sourceData, sourceList, remBut);
         }
      });

      // let's set both buttons to the same length
      // (after pack-ing we read the width
      // and set the max width to both buttons)
      RowData rdAddBut = new RowData();
      RowData rdRemBut = new RowData();
      // give both buttons the same length
      int buttonLength = FormUtils.buttonLength(80, addBut, remBut);
      rdAddBut.width = buttonLength;
      rdRemBut.width = buttonLength;
      addBut.setLayoutData(rdAddBut);
      remBut.setLayoutData(rdRemBut);

      addBut.setEnabled(true);
      remBut.setEnabled(false);

      sourceList.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            int cnt = sourceList.getSelectionCount();
            if (cnt > 0) {
               remBut.setEnabled(true);
            } else {
               remBut.setEnabled(false);
            }
         }
      });
      sourceList.addMouseListener(new MouseAdapter() {
         public void mouseDoubleClick(MouseEvent e) {
            if (sourceList.getSelectionCount() > 0) {
               rem(sourceData, sourceList, addBut);
            }
         }
      });

      return body;
   }

   private void add(Map<String, Resource> sourceData, List sourceList, Button addBut, Class resourceType) {
      final java.util.List<File> resourceFilesToInstall;
      //String recitExt = GlobalConfig.isMac ? "*.zip" : "*.recit.zip";
      //String recitExt ="*.zip";//here we could create a common extension for the resources.
      try {

         String fileExtensions = AddOnManagerUtils.getValidInstallationFileExtensions(resourceType);
         resourceFilesToInstall = MessageBoxUtils.importFileDialog(shell, new String[] { "New Resource Packs ("
               + fileExtensions + ")" }, new String[] { fileExtensions }, false);
         if (resourceFilesToInstall.size() <= 0) {
            return;
         }
      } catch (IOException e) {
         MessageBoxUtils.showActionFailureError(e);
         logger.implicitLog(e);
         return;
      }

      final File file2Import = resourceFilesToInstall.get(0);

      CandidateResource candidateResource = AddOnManagerUtils.getNewCandidateResource(resourceType, file2Import);

      QuestionPromptForm qpf = new QuestionPromptForm(shell, new String[] {
            lang.getMeaningById("IMPORT_QUESTION", "ME_ONLY"), lang.getMeaningById("IMPORT_QUESTION", "ALL_USERS") },
            lang.getMeaningById("IMPORT_QUESTION", "IMPORT_FOR"), lang.getMeaning("QUESTION"), true,
            new ResourceInstallationQuestionListener(display, candidateResource, sourceData, sourceList));

      qpf.show();

   }

   private void rem(Map<String, Resource> sourceData, List sourceList, Button remBut) {
      int[] indices = sourceList.getSelectionIndices();
      Resource resourceToDelete = sourceData.get(sourceList.getItem(indices[0]));

      QuestionPromptForm qpf = new QuestionPromptForm(shell, new String[] {
            lang.getMeaningById("REMOVAL_QUESTION", "YES"), lang.getMeaningById("REMOVAL_QUESTION", "NO") },
            lang.getMeaningById("REMOVAL_QUESTION", "CONFIRM_REMOVAL"), lang.getMeaning("QUESTION"), true,
            new ResourceUnInstallationQuestionListener(display, resourceToDelete, sourceData, sourceList));

      qpf.show();

      remBut.setEnabled(false);
   }

   public void show() {
      shell.pack();
      if (shell.getSize().y < 250)
         shell.setSize(shell.getSize().x, 250);
      if (shell.getSize().y > 400)
         shell.setSize(shell.getSize().x, 600);
      shell.setLocation(FormUtils.getCenter(parent, shell));
      shell.open();
   }

   public void refresh() {
      shell.redraw();
   }

   public String getFormId() {
      return FORM_ID;
   }
}
