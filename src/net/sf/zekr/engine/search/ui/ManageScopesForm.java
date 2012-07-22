/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Sep 29, 2006
 */
package net.sf.zekr.engine.search.ui;

import java.util.List;

import net.sf.zekr.engine.search.SearchScope;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.MessageBoxUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ManageScopesForm extends BaseForm {
   private Composite body;
   private List<SearchScope> searchScopeList;
   private org.eclipse.swt.widgets.List listWidget;
   private Button editBut;
   private Button removeBut;
   private Button newBut;
   private boolean canceled = true;
   protected int selectedIndex = -1;

   public ManageScopesForm(Shell parent, List<SearchScope> searchScopes) {
      this.parent = parent;
      display = parent.getDisplay();
      this.searchScopeList = searchScopes;
      shell = createShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
      FillLayout fl = new FillLayout();
      shell.setLayout(fl);
      shell.setText(meaning("TITLE"));
      shell.setImages(new Image[] { new Image(display, resource.getString("icon.searchScope16")),
            new Image(display, resource.getString("icon.searchScope32")) });

      init();
      shell.pack();
      shell.setSize(300, 300);
   }

   private void init() {
      body = new Composite(shell, lang.getSWTDirection());
      body.setLayout(new GridLayout(1, false));

      GridData gd = new GridData(GridData.FILL_BOTH);
      listWidget = new org.eclipse.swt.widgets.List(body, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

      String[] items = new String[searchScopeList.size()];
      for (int i = 0; i < searchScopeList.size(); i++) {
         items[i] = searchScopeList.get(i).toString();
      }

      listWidget.setItems(items);
      listWidget.setLayoutData(gd);
      listWidget.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            int c = listWidget.getSelectionCount();
            if (c == 0) {
               removeBut.setEnabled(false);
               editBut.setEnabled(false);
            } else if (c > 1) {
               removeBut.setEnabled(true);
               editBut.setEnabled(false);
            } else {
               removeBut.setEnabled(true);
               editBut.setEnabled(true);
            }
         }
      });
      listWidget.addMouseListener(new MouseAdapter() {
         public void mouseDoubleClick(MouseEvent e) {
            int height = listWidget.getItemHeight() * listWidget.getItemCount() - 1;
            if (e.y <= height && listWidget.getSelectionCount() == 1)
               edit();
         }
      });
      listWidget.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent e) {
            if (e.character == SWT.DEL) {
               remove();
            }
         }
      });

      gd = new GridData();
      gd.horizontalAlignment = SWT.LEAD;

      RowLayout rl = new RowLayout(SWT.HORIZONTAL);

      Composite manageButComposite = new Composite(body, SWT.NONE);
      manageButComposite.setLayout(rl);
      manageButComposite.setLayoutData(gd);

      RowData rd = new RowData();
      rd.width = 40;
      newBut = new Button(manageButComposite, SWT.PUSH);
      newBut.setToolTipText(lang.getMeaning("NEW"));
      newBut.setImage(new Image(display, resource.getString("icon.add")));
      newBut.setLayoutData(rd);
      newBut.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         };

         public void widgetSelected(SelectionEvent e) {
            SearchScopeForm ssf = new SearchScopeForm(shell);
            if (ssf.open()) {
               SearchScope ss = ssf.getSearchScope();
               searchScopeList.add(ss);
               listWidget.add(ss.toString());
            }
         };
      });

      rd = new RowData();
      rd.width = 40;
      removeBut = new Button(manageButComposite, SWT.PUSH);
      removeBut.setToolTipText(lang.getMeaning("REMOVE"));
      removeBut.setImage(new Image(display, resource.getString("icon.remove")));
      removeBut.setLayoutData(rd);
      removeBut.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         };

         public void widgetSelected(SelectionEvent e) {
            remove();
         };
      });

      rd = new RowData();
      rd.width = 40;
      editBut = new Button(manageButComposite, SWT.PUSH);
      editBut.setToolTipText(lang.getMeaning("EDIT"));
      editBut.setImage(new Image(display, resource.getString("icon.searchScope.edit16")));
      editBut.setLayoutData(rd);
      editBut.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         };

         public void widgetSelected(SelectionEvent e) {
            edit();
         };
      });

      if (searchScopeList.size() > 0) {
         listWidget.select(0);
      } else {
         removeBut.setEnabled(false);
         editBut.setEnabled(false);
      }

      gd = new GridData(GridData.FILL_HORIZONTAL);
      new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(gd);

      gd = new GridData();
      gd.horizontalAlignment = SWT.TRAIL;

      rl = new RowLayout(SWT.HORIZONTAL);

      Composite butComposite = new Composite(body, SWT.NONE);
      butComposite.setLayout(rl);
      butComposite.setLayoutData(gd);

      Button okBut = new Button(butComposite, SWT.PUSH);
      Button cancelBut = new Button(butComposite, SWT.PUSH);
      okBut.setText(FormUtils.addAmpersand(lang.getMeaning("OK")));
      okBut.pack();
      okBut.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         };

         public void widgetSelected(SelectionEvent e) {
            canceled = false;
            selectedIndex = listWidget.getSelectionIndex();
            shell.close();
         };
      });
      shell.setDefaultButton(okBut);

      cancelBut.setText(FormUtils.addAmpersand(lang.getMeaning("CANCEL")));
      cancelBut.pack();
      cancelBut.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         };

         public void widgetSelected(SelectionEvent e) {
            canceled = true;
            shell.close();
         };
      });
      RowData rdOk = new RowData();
      RowData rdCancel = new RowData();
      // set the OK and CANCEL buttons to the same length		
      int buttonLength = FormUtils.buttonLength(80, okBut, cancelBut);
      rdOk.width = buttonLength;
      rdCancel.width = buttonLength;
      okBut.setLayoutData(rdOk);
      cancelBut.setLayoutData(rdCancel);

   }

   private void remove() {
      if (listWidget.getSelectionCount() <= 0)
         return;
      if (MessageBoxUtils.showYesNoConfirmation(lang.getMeaning("YES_NO"), lang.getMeaning("REMOVE"))) {
         int[] indices = listWidget.getSelectionIndices();
         for (int i = indices.length - 1; i >= 0; i--) {
            searchScopeList.remove(indices[i]);
         }
         listWidget.remove(indices);

         if (listWidget.getSelectionCount() == 0) {
            removeBut.setEnabled(false);
            editBut.setEnabled(false);
         }
      }
      shell.forceActive();
   }

   private void edit() {
      int index = listWidget.getSelectionIndex();
      SearchScope selectedSearchScope = (SearchScope) searchScopeList.get(index);
      SearchScopeForm ssf = new SearchScopeForm(shell, selectedSearchScope);
      if (ssf.open()) {
         SearchScope ss = ssf.getSearchScope();
         searchScopeList.set(index, ss);
         listWidget.setItem(index, ss.toString());
      }
   }

   public List<SearchScope> getSearchScopeList() {
      return searchScopeList;
   }

   /**
    * This method should be called after OK button pressed.
    * 
    * @return selected item index, or -1 if no item selected.
    */
   public int getSelectedIndex() {
      return selectedIndex;
   }

   /**
    * @return <code>true</code> if ok pressed, <code>false</code> otherwise.
    */
   public boolean open() {
      shell.setLocation(FormUtils.getCenter(parent, shell));
      super.show();
      loopEver();
      return !canceled;
   }

   public String getFormId() {
      return "MANAGE_SCOPES_FORM";
   };
}
