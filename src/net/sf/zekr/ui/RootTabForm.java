/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Aug 1, 2008
 */
package net.sf.zekr.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.filter.QuranFilterUtils;
import net.sf.zekr.common.runtime.HtmlGenerationException;
import net.sf.zekr.common.runtime.HtmlRepository;
import net.sf.zekr.engine.language.LanguageEngine;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.SearchException;
import net.sf.zekr.engine.search.SearchResultModel;
import net.sf.zekr.engine.search.comparator.SearchResultComparatorFactory;
import net.sf.zekr.engine.search.root.QuranRootSearch;
import net.sf.zekr.engine.search.tanzil.DefaultSearchScorer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This class provides a tab folder for {@link QuranForm}.
 * 
 * @author Mohsen Saboorian
 */
public class RootTabForm {
   private final ApplicationConfig config = ApplicationConfig.getInstance();
   private final LanguageEngine lang = config.getLanguageEngine();
   private final ResourceManager resource = ResourceManager.getInstance();
   private final static Logger logger = Logger.getLogger(RootTabForm.class);

   private TabFolder tabFolder;
   Combo searchCombo;
   private org.eclipse.swt.widgets.List rootList;
   private Composite tabBody;
   private QuranForm quranForm;
   Combo searchOrderCombo;
   Button sortOrderButton;
   private Composite searchPaginationComp;
   private Spinner searchPaginationSpinner;
   private Display display;
   private SearchResultNavigator rootSearchNav;
   private SearchResultModel srm;
   private Button searchArrowBut;
   private Menu searchMenu;
   private Button cancelButton;
   private Composite searchComp;

   public RootTabForm(QuranForm quranForm, TabFolder tabFolder) {
      this.quranForm = quranForm;
      display = quranForm.display;
      this.tabFolder = tabFolder;
   }

   private void createTabContent() {
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
      GridLayout gl = new GridLayout(2, false);
      gl.horizontalSpacing = 2;
      gl.verticalSpacing = 0;
      gl.marginWidth = gl.marginHeight = 0;
      searchComp = new Composite(tabBody, SWT.RIGHT_TO_LEFT);
      searchComp.setLayout(gl);
      searchComp.setLayoutData(gd);

      gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
      gd.verticalIndent = 6;
      searchCombo = new Combo(searchComp, SWT.DROP_DOWN | SWT.RIGHT_TO_LEFT | SWT.SEARCH);
      searchCombo.setLayoutData(gd);
      searchCombo.setVisibleItemCount(6);
      searchCombo.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (e.keyCode == SWT.ARROW_DOWN) {
               e.doit = false;
            }
         }
      });
      searchCombo.addTraverseListener(new TraverseListener() {
         public void keyTraversed(TraverseEvent e) {
            if (e.detail == SWT.TRAVERSE_RETURN) {
               selectTextInList();
               doFind();
            } else if (e.keyCode == SWT.ARROW_DOWN && e.detail == SWT.TRAVERSE_ARROW_NEXT) {
               rootList.setFocus();
               if (rootList.getItemCount() > 0) {
                  rootList.select(0);
               }
            } else if (e.detail == SWT.TRAVERSE_ESCAPE) {
               resetSearchBox();
            }
         }
      });

      gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
      gd.verticalIndent = 5;
      cancelButton = new Button(searchComp, SWT.PUSH | SWT.FLAT);
      cancelButton.setImage(new Image(display, resource.getString("icon.cancel")));
      cancelButton.setLayoutData(gd);
      cancelButton.setToolTipText(lang.getMeaning("RESET"));
      cancelButton.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            resetSearchBox();
         }
      });

      searchCombo.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            filterList(searchCombo.getText());
         }
      });

      gl = new GridLayout(2, false);
      gl.horizontalSpacing = 0;
      gl.marginWidth = 0;
      gl.verticalSpacing = 0;

      gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
      Composite searchButComp = new Composite(tabBody, SWT.NONE);
      searchButComp.setLayout(gl);
      searchButComp.setLayoutData(gd);

      gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
      Button searchButton = new Button(searchButComp, SWT.PUSH);
      searchButton.setText(lang.getMeaning("SEARCH"));
      searchButton.setLayoutData(gd);
      searchButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            selectTextInList();
            doFind();
         }

      });

      // add small controlling arrow button
      gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
      gd.horizontalIndent = -1;

      searchArrowBut = new Button(searchButComp, SWT.TOGGLE);
      searchMenu = quranForm.searchScopeMenu;
      searchMenu.addMenuListener(new MenuAdapter() {
         public void menuHidden(MenuEvent e) {
            searchArrowBut.setSelection(false);
         }
      });
      searchArrowBut.setImage(new Image(display, resource.getString("icon.down")));
      searchArrowBut.setLayoutData(gd);
      searchArrowBut.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            Point loc = display.map(searchArrowBut, null, 0, 0);
            Point size = searchArrowBut.getSize();
            searchMenu.setLocation(loc.x, loc.y + size.y);
            searchMenu.setVisible(true);
         }
      });

      KeyAdapter ka = new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (e.keyCode == 13) {
               selectTextInList();
               doFind();
            }
         }
      };

      rootList = new org.eclipse.swt.widgets.List(tabBody, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.RIGHT_TO_LEFT);
      gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
      gd.heightHint = 90;
      rootList.setLayoutData(gd);
      final List<String> stringRootList = config.getQuranRoot().getRootList();
      rootList.setItems(stringRootList.toArray(new String[0]));
      rootList.addSelectionListener(new SelectionAdapter() {
         public void widgetDefaultSelected(SelectionEvent e) {
            doFind();
         }
      });
      rootList.addTraverseListener(new TraverseListener() {
         public void keyTraversed(TraverseEvent e) {
            if (e.detail == SWT.TRAVERSE_RETURN) {
               doFind();
            } else if (e.detail == SWT.TRAVERSE_ARROW_PREVIOUS && rootList.getSelectionIndex() <= 0) {
               searchCombo.setFocus();
            }
         }
      });

      gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
      gd.horizontalSpan = 2;
      gl = new GridLayout(3, false);
      gl.marginWidth = 0;
      Composite searchOptionsComp = new Composite(tabBody, SWT.NONE);
      searchOptionsComp.setLayout(gl);
      searchOptionsComp.setLayoutData(gd);

      gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
      Label sortResult = new Label(searchOptionsComp, SWT.NONE);
      sortResult.setLayoutData(gd);
      sortResult.setText(quranForm.meaning("SORT_BY") + ":");

      gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
      searchOrderCombo = new Combo(searchOptionsComp, SWT.READ_ONLY);
      searchOrderCombo.setItems(new String[] { quranForm.meaning("RELEVANCE"), quranForm.meaning("NATURAL_ORDER"),
            lang.getMeaning("REVEL_ORDER"), quranForm.meaning("AYA_LENGTH") });
      searchOrderCombo.setLayoutData(gd);
      searchOrderCombo.select(config.getProps().getInt("view.search.root.sortBy"));

      searchOrderCombo.setData("0", "net.sf.zekr.engine.search.comparator.SimilarityComparator");
      searchOrderCombo.setData("1", null);
      searchOrderCombo.setData("2", "net.sf.zekr.engine.search.comparator.RevelationOrderComparator");
      searchOrderCombo.setData("3", "net.sf.zekr.engine.search.comparator.AyaLengthComparator");
      searchOrderCombo.addKeyListener(ka);

      gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
      sortOrderButton = new Button(searchOptionsComp, SWT.PUSH | SWT.FLAT);
      sortOrderButton.setData(config.getProps().getString("view.search.root.sortOrder", "des"));
      quranForm.addSortOrderButton(sortOrderButton, gd);

      rootSearchNav = new SearchResultNavigator(tabBody, new IPageNavigator() {
         public void gotoPage(int page) {
            findGoto(page);
         }
      });
   }

   private void selectTextInList() {
      if (rootList.getItemCount() == 1) {
         rootList.select(0);
         return;
      }

      String q = simplifyText(searchCombo.getText());
      String[] items = rootList.getItems();
      for (int i = 0; i < items.length; i++) {
         if (simplifyText(items[i]).equals(q)) {
            rootList.select(i);
            break;
         }
      }
   }

   private void findGoto(int pageNo) {
      try {
         quranForm.uvc.setViewMode(IUserView.VM_ROOT_SEARCH);
         if (srm == null) {
            logger.error("Search is not done yet!");
            return;
         }
         if (pageNo > srm.getResultPageCount()) {
            logger.error("No such page in results: " + pageNo);
            MessageBoxUtils.showError("No such page in root search results: " + pageNo);
            return;
         }

         quranForm.doPreFind();
         if (pageNo > 1) {
            rootSearchNav.prevPageBut.setEnabled(true);
         } else {
            rootSearchNav.prevPageBut.setEnabled(false);
         }
         if (pageNo < srm.getResultPageCount()) {
            rootSearchNav.nextPageBut.setEnabled(true);
         } else {
            rootSearchNav.nextPageBut.setEnabled(false);
         }

         pageNo = pageNo == 0 ? 1 : pageNo;
         logger.info("Navigate to page #" + pageNo + " of root search result.");
         Browser searchBrowser = quranForm.viewLayout == QuranForm.TRANS_ONLY ? quranForm.transBrowser
               : quranForm.quranBrowser;
         searchBrowser.setUrl(quranForm.quranUri = HtmlRepository.getAdvancedSearchQuranUri(srm, pageNo - 1));
         quranForm.pageChanged = true;
      } catch (HtmlGenerationException e) {
         logger.log(e);
      }
   }

   private void find() {
      String str;
      str = searchCombo.getText().trim();
      if ("".equals(str)) {
         return; // do nothing
      }

      if (searchCombo.getItemCount() <= 0 || !str.equals(searchCombo.getItem(0))) {
         searchCombo.add(str, 0);
      }
      if (searchCombo.getItemCount() > 40) {
         searchCombo.remove(40, searchCombo.getItemCount() - 1);
      }

      logger.info("Search started: " + str);
      Date date1 = new Date();

      int sortBy = searchOrderCombo.getSelectionIndex();
      QuranRootSearch qrs;
      try {
         qrs = new QuranRootSearch(QuranText.getSimpleTextInstance(), new DefaultSearchScorer());
      } catch (Exception e) {
         logger.implicitLog(e);
         MessageBoxUtils.showError("Basic searcher failed to initialize:\n\t" + e);
         return; // search failed
      }
      if (quranForm.searchScope != qrs.getSearchScope()) { // no need to .equals()
         qrs.setSearchScope(quranForm.searchScope);
      }
      qrs.setSearchResultComparator(SearchResultComparatorFactory.getComparator((String) searchOrderCombo
            .getData(String.valueOf(sortBy))));
      qrs.setAscending(sortOrderButton.getData().equals("asc"));
      try {
         srm = qrs.search(str);
      } catch (SearchException se) {
         MessageBoxUtils.showError(lang.getMeaning("ACTION_FAILED") + "\n" + se.toString());
         rootSearchNav.setVisible(false);
         return; // search failed
      }
      Date date2 = new Date();
      logger.info("Search for " + str + " finished; took " + (date2.getTime() - date1.getTime()) + " ms.");

      int pageCount = srm.getResultPageCount();
      logger.debug("Search result has " + pageCount + " pages.");
      if (pageCount > 1) {
         rootSearchNav.setVisible(true);
      } else {
         rootSearchNav.setVisible(false);
         rootSearchNav.nextPageBut.setEnabled(true);
      }

      rootSearchNav.resetSearch(pageCount);
   }

   private String filterList(String filter) {
      filter = filter.trim();
      filter = simplifyText(filter);
      List<String> list = config.getQuranRoot().getRootList();
      List<String> newList = new ArrayList<String>();
      if (StringUtils.isBlank(filter)) {
         newList = list;
      } else {
         for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            if (simplifyText(item).indexOf(filter) > -1) {
               newList.add(item);
            }
            // if (FilenameUtils.wildcardMatch(item, filter)) {
            // newList.add(item);
            // }
         }
      }
      rootList.setItems(newList.toArray(new String[0]));
      return filter;
   }

   private static final String simplifyText(String filter) {
      return QuranFilterUtils.filterSimilarArabicCharactersForRootSearch(filter);
   }

   public TabItem createTabItem() {
      if (!config.isRootDatabaseEnabled()) {
         return null;
      }

      TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
      tabItem.setText(lang.getMeaning("ROOT"));

      GridLayout gl = new GridLayout(2, false);
      tabBody = new Composite(tabFolder, SWT.NONE);
      tabBody.setLayout(gl);
      tabItem.setControl(tabBody);

      createTabContent();
      return tabItem;
   }

   private void resetSearchBox() {
      searchCombo.setText("");
   }

   private void doFind() {
      int[] selection = rootList.getSelectionIndices();
      if (selection.length > 0) {
         searchCombo.setText(rootList.getItem(selection[0]));
         find();
      }
   }

   public Combo getSearchCombo() {
      return searchCombo;
   }
}
