/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Mar 11, 2009
 */
package net.sf.zekr.engine.audio.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.engine.audio.AudioData;
import net.sf.zekr.engine.audio.PlayStatus;
import net.sf.zekr.engine.audio.PlayableObject;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.QuranForm;
import net.sf.zekr.ui.helper.EventProtocol;
import net.sf.zekr.ui.helper.EventUtils;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Mohsen Saboorian
 */
public class AudioPlayerForm extends BaseForm {
   public enum DockMode {
      FLOAT, TASKPANE, TOP, BOTTOM
   }

   private static final int WINDOW_VISIBILITY_THRESHOLD = 60;
   public static final String FORM_ID = "AUDIO_PLAYER_FORM";
   public static final int MAX_SEEK_VALUE = 1000;
   public static final int MAX_VOLUME_VALUE = 100;

   DockMode dockMode = DockMode.BOTTOM;

   private PlayerController playerController;
   private Button playPauseItem;
   private Button stopItem;
   private QuranForm quranForm;

   private Button prevItem;
   private Button nextItem;
   private Image volumeImage0;
   private Image volumeImage1;
   private Image volumeImage2;
   private Image volumeImage3;

   private boolean isRtl;
   private int volume;

   private Canvas volumeCanvas;
   private ProgressBar volumeProgressBar;
   private ProgressBar seekProgressBar;
   private Composite middleRow;
   private Composite topRow;
   private Composite body;
   private Composite bottomRow;
   private Button playScopeBut;
   private Image multiAyaImage, singleAyaImage;
   private Image pauseImage, playImage, stopImage;
   private Image prevAyaImage, nextAyaImage;
   private Image addImage, removeImage;
   private List<Link> playerLabelList = new ArrayList<Link>();
   private Canvas playerCanvas;
   private IUserView uvc;
   private Combo intervalCombo;
   private Combo repeatCombo;
   private PropertiesConfiguration props;
   private Menu recitationPopupMenu;
   private Menu playScopePopupMenu;
   private String[] playScopeItems;
   private Runnable onClose;
   private List<Composite> topRowList = new ArrayList<Composite>();
   private final int topRowHorizontalSpacing = 3;

   public AudioPlayerForm(QuranForm quranForm, Shell parent, Runnable onClose) {
      int l = lang.getSWTDirection();
      isRtl = l == SWT.RIGHT_TO_LEFT && GlobalConfig.hasBidiSupport;

      playerController = config.getPlayerController();
      volume = playerController.getVolume();
      uvc = config.getUserViewController();

      this.quranForm = quranForm;
      this.parent = parent;
      display = parent.getDisplay();
      props = config.getProps();

      this.onClose = onClose;

      try {
         dockMode = DockMode.valueOf(props.getString("audio.controller.dock", "bottom").toUpperCase());
      } catch (Exception e) {
      }

      init();
   }

   @SuppressWarnings("rawtypes")
   private void init() {
      Point shellLocation = null;
      if (dockMode == DockMode.FLOAT) {
         shell = createShell(parent, SWT.CLOSE /*| SWT.ON_TOP */| SWT.TOOL
               | (isRtl ? SWT.RIGHT_TO_LEFT : SWT.LEFT_TO_RIGHT));
         List shellLocationList = config.getProps().getList("audio.controller.location");
         if (shellLocationList.size() > 1) {
            shellLocation = new Point(Integer.parseInt(shellLocationList.get(0).toString()),
                  Integer.parseInt(shellLocationList.get(1).toString()));
         }
         shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
               Point location = shell.getLocation();
               Rectangle bounds = shell.getBounds();
               // make sure player form is visible
               Point screen = FormUtils.getScreenSize(display);
               if (bounds.width + bounds.x < WINDOW_VISIBILITY_THRESHOLD) {
                  location.x = WINDOW_VISIBILITY_THRESHOLD - bounds.width;
               } else if (screen.x - location.x < WINDOW_VISIBILITY_THRESHOLD) {
                  location.x = screen.x - WINDOW_VISIBILITY_THRESHOLD;
               }
               if (bounds.height + bounds.y < WINDOW_VISIBILITY_THRESHOLD) {
                  location.y = WINDOW_VISIBILITY_THRESHOLD - bounds.height;
               } else if (screen.y - location.y < WINDOW_VISIBILITY_THRESHOLD) {
                  location.y = screen.y - WINDOW_VISIBILITY_THRESHOLD;
               }

               config.getProps().setProperty("audio.controller.location", new Object[] { location.x, location.y });
               // config.getProps().setProperty("audio.controller.show", "false");
            }
         });
         // config.getProps().setProperty("audio.controller.show", "true");

         FillLayout fl = new FillLayout();
         shell.setLayout(fl);
         shell.setText(meaning("TITLE"));
         shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
               onClose.run();
            }
         });

         body = new Composite(shell, SWT.NONE);
      } else {
         shell = quranForm.getShell();
         if (dockMode == DockMode.TASKPANE) {
            body = new Group(quranForm.getWorkPane(), SWT.NONE);
            ((Group) body).setText(meaning("TITLE"));
         } else {
            // GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

            /*ScrolledComposite sc = new ScrolledComposite(dockMode == DockMode.BOTTOM ? quranForm.getBottomComposite()
                  : quranForm.getTopComposite(), SWT.V_SCROLL | SWT.H_SCROLL);*/
            // s.setLayoutData(gd);
            /*sc.setExpandHorizontal(true);
            sc.setExpandVertical(true);*/

            Composite b = new Composite(dockMode == DockMode.BOTTOM ? quranForm.getBottomComposite()
                  : quranForm.getTopComposite(), SWT.NONE);
            // sc.setContent(b);

            GridLayout gl = new GridLayout(1, false);
            if (dockMode == DockMode.BOTTOM) {
               gl.marginTop = 3;
            } else {
               gl.marginTop = 0;
            }

            gl.marginBottom = 0;
            gl.horizontalSpacing = gl.verticalSpacing = 0;
            gl.marginHeight = gl.marginWidth = 0;
            b.setLayout(gl);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            b.setLayoutData(gd);

            if (dockMode == DockMode.TOP) {
               body = new Composite(b, SWT.NONE);
            }

            gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            // gd.minimumHeight = 5;
            Label l = new Label(b, SWT.SEPARATOR | SWT.HORIZONTAL);
            l.setLayoutData(gd);

            if (dockMode == DockMode.BOTTOM) {
               body = new Composite(b, SWT.NONE);
            }
            // ((Group) body).setText(meaning("TITLE"));
         }
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         body.setLayoutData(gd);

         body.addListener(SWT.Hide, new Listener() {
            public void handleEvent(Event e) {
               // quranForm.getWorkPane().layout(true);
               onClose.run();
            }
         });
         body.addListener(SWT.Show, new Listener() {
            public void handleEvent(Event e) {
               // quranForm.getWorkPane().layout(true);
            }
         });
      }

      RowLayout rl = getNewRowLayout((dockMode == DockMode.BOTTOM || dockMode == DockMode.TOP) ? SWT.HORIZONTAL
            : SWT.VERTICAL);
      rl.spacing = (dockMode == DockMode.BOTTOM || dockMode == DockMode.TOP) ? 10 : 1;
      rl.marginLeft = rl.marginRight = 0;
      rl.fill = dockMode == DockMode.FLOAT | dockMode == DockMode.TASKPANE;
      rl.wrap = false;
      body.setLayout(rl);

      RowData rd = new RowData();
      topRow = new Composite(body, SWT.NONE);
      topRow.setLayoutData(rd);

      if (dockMode != DockMode.BOTTOM && dockMode != DockMode.TOP) {
         new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
      }

      rd = new RowData();
      middleRow = new Composite(body, SWT.NONE);
      middleRow.setLayoutData(rd);

      if (dockMode != DockMode.BOTTOM && dockMode != DockMode.TOP) {
         new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
      }

      bottomRow = new Composite(body, SWT.NONE);

      cacheImages();

      createTopRow();
      createMiddleRow();
      createBottomRow();

      // parent.pack();
      //	shell.pack();
      // body.pack();

      if (dockMode == DockMode.FLOAT) {
         if (shellLocation != null) {
            shell.setLocation(shellLocation);
         }
         shell.pack();
      } else if (dockMode == DockMode.BOTTOM) {
         quranForm.getBottomComposite().layout(true, true);
      } else if (dockMode == DockMode.TOP) {
         quranForm.getTopComposite().layout(true, true);
      } else if (dockMode == DockMode.TASKPANE) {
         quranForm.getWorkPane().layout(true, true);
      }
   }

   private void cacheImages() {
      volumeImage0 = new Image(display, resource.getString("icon.player.vol0"));
      volumeImage1 = new Image(display, resource.getString("icon.player.vol1"));
      volumeImage2 = new Image(display, resource.getString("icon.player.vol2"));
      volumeImage3 = new Image(display, resource.getString("icon.player.vol3"));

      singleAyaImage = new Image(display, resource.getString("icon.player.singleAya"));
      multiAyaImage = new Image(display, resource.getString("icon.player.multiAya"));

      prevAyaImage = new Image(display, resource.getString("icon.player.prevAya"));
      nextAyaImage = new Image(display, resource.getString("icon.player.nextAya"));

      /*addImage = new Image(display, resource.getString("icon.player.add"));
      removeImage = new Image(display, resource.getString("icon.player.remove"));*/

      File playImageFile = new File(isRtl ? resource.getString("icon.player.playRtl")
            : resource.getString("icon.player.play"));
      File pauseImageFile = new File(resource.getString("icon.player.pause"));
      File stopImageFile = new File(resource.getString("icon.player.stop"));
      playImage = new Image(display, playImageFile.getAbsolutePath());
      pauseImage = new Image(display, pauseImageFile.getAbsolutePath());
      stopImage = new Image(display, stopImageFile.getAbsolutePath());
   }

   private void createTopRow() {
      GridLayout gl = new GridLayout(1, false);
      gl.marginHeight = 2;
      topRow.setLayout(gl);
      gl.verticalSpacing = gl.horizontalSpacing = 3;
      gl.marginWidth = 2;
      gl.marginHeight = 2;

      List<AudioData> selectedList = config.getAudio().getCurrentList();
      for (int i = 0; i < selectedList.size(); i++) {
         AudioData audioData = selectedList.get(i);
         createTopRowWidgets(i, audioData);
      }
   }

   private void createTopRowWidgets(final int reciterIndex, AudioData audioData) {
      final Composite c = new Composite(topRow, SWT.NONE);
      topRowList.add(c);
      GridLayout gl = new GridLayout(2, false);
      gl.horizontalSpacing = topRowHorizontalSpacing;
      gl.verticalSpacing = 0;
      gl.marginHeight = gl.marginWidth = 0;
      c.setLayout(gl);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      c.setLayoutData(gd);

      gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
      gd.heightHint = gd.widthHint = 19;
      Button addButton = new Button(c, SWT.PUSH | SWT.FLAT);
      addButton.setLayoutData(gd);
      /*if (addButton.getFont() != null && addButton.getFont().getFontData() != null) {
         FormUtils.boldFont(display, addButton);
      }*/
      if (reciterIndex == 0) {
         addButton.setText("+");
      } else {
         addButton.setText("-");
      }

      gd = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
      final Link playerLabel = new Link(c, SWT.FLAT);
      playerLabel.setLayoutData(gd);
      playerLabelList.add(playerLabel);

      addButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (reciterIndex == 0) {
               config.setSelectedAudio(config.getAudio().getCurrent().getId(), topRowList.size());
               createTopRowWidgets(topRowList.size(), null);
            } else {
               int idx = topRowList.indexOf(c);
               topRowList.remove(idx).dispose();
               playerLabelList.remove(idx);
               config.setSelectedAudio(null, idx);
            }
            if (dockMode == DockMode.FLOAT) {
               shell.pack();
            } else if (dockMode == DockMode.TASKPANE) {
               // I couldn't finally find a clean way for handling work pane scroller height
               EventUtils.sendEvent(shell, RECREATE_VIEW);
               /*
               body.layout(true, true);
               quranForm.getWorkPane().layout(true, true);
               quranForm.getShell().layout(true, true);
               quranForm.getWorkPaneScroller().setMinHeight(
                     quranForm.getWorkPane().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
               */
            } else {
               quranForm.getContentComposite().layout(true, true);
               updatePlayerLabel(true);
            }
         }
      });

      final Menu m = new Menu(shell, SWT.POP_UP);
      if (reciterIndex == 0) {
         recitationPopupMenu = m;
      }
      Menu origMenu = quranForm.getMenu().getRecitationListMenu();
      MenuItem[] items = origMenu.getItems();
      for (int i = 0; i < items.length; i++) {
         MenuItem mi = new MenuItem(m, items[i].getStyle());
         String text = items[i].getText();
         if (text != null) {
            mi.setText(text);
         }
         Object data = items[i].getData();
         if (data != null) {
            mi.setData(data);
         }
         Image img = items[i].getImage();
         if (img != null) {
            mi.setImage(img);
         }
         if (audioData == null) {
            mi.setSelection(items[i].getSelection());
         } else if (audioData.id.equals(mi.getData())) {
            mi.setSelection(true);
         }
         final Listener[] listeners = items[i].getListeners(SWT.Selection);
         for (int j = 0; j < listeners.length; j++) {
            final Listener l = listeners[j];
            mi.addListener(SWT.Selection, new Listener() {
               @Override
               public void handleEvent(Event e) {
                  e.index = topRowList.indexOf(c);
                  l.handleEvent(e);
               }
            });
         }
      }

      playerLabel.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent event) {
            if ("recitation".equals(event.text)) {
               Point loc = display.map(playerLabel, null, 0, 0);
               Point size = playerLabel.getSize();
               m.setLocation(loc.x, loc.y + size.y);
               m.setVisible(true);
            }
         }

         public void widgetSelected(SelectionEvent event) {
            widgetDefaultSelected(event);
         }
      });

      updatePlayerLabel(reciterIndex, true);
   }

   protected void pack() {
      if (dockMode == DockMode.FLOAT) {
         shell.pack();
      } else {
         body.redraw();
      }
   }

   public void updatePlayerLabel(boolean reciterChanged) {
      for (int i = 0; i < playerLabelList.size(); i++) {
         updatePlayerLabel(i, reciterChanged);
      }
   }

   public void updatePlayerLabel(int audioIndex, boolean reciterChanged) {
      List<AudioData> audioList = config.getAudio().getCurrentList();
      if (isDisposed() || CollectionUtils.isEmpty(audioList)) {
         return;
      }

      assert audioIndex >= audioList.size() : "Row index should be less than " + audioList.size();

      AudioData audioData = audioList.get(audioIndex);

      String status = getPlayerStatus(audioIndex);
      IQuranLocation l = uvc.getLocation();
      String text = String.format("<a href=\"recitation\">%s</a> %6$s %s (%s):%s | %s", audioData.getLocalizedName(),
            l.getSuraName(), l.getSura(), l.getAya(), status, dockMode != DockMode.TASKPANE ? "|" : "\n");
      String tooltip = String.format("%s (%s):%s | %s: %s | %s", l.getSuraName(), l.getSura(), l.getAya(),
            meaning("RECITER"), audioData.getLocalizedName(), status);
      playerLabelList.get(audioIndex).setText(text + (reciterChanged ? "             " : "")); // keep for differences between width of play/stop/pause texts
      playerLabelList.get(audioIndex).setToolTipText(tooltip);

      int width = playerLabelList.get(audioIndex).computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
      if (reciterChanged && topRowList.get(audioIndex).getSize().x <= width + topRowHorizontalSpacing) {
         if (dockMode == DockMode.BOTTOM || dockMode == DockMode.TOP) {
            quranForm.getBottomComposite().layout(true, true);
         } else if (dockMode == DockMode.FLOAT) {
            shell.pack();
         }
      } else {
         if (topRowList.get(audioIndex).getSize().x <= width + topRowHorizontalSpacing) {
            if (dockMode == DockMode.BOTTOM || dockMode == DockMode.TOP) {
               quranForm.getBottomComposite().layout(true, true);
            } else if (dockMode == DockMode.FLOAT) {
               // ((GridData) playerLabelList.get(audioIndex).getLayoutData()).widthHint = size.x + 40;
               shell.pack();
            }
         }
      }

      /*if (topRow.getSize().x <= size.x) {
         if (dockMode == DockMode.BOTTOM || dockMode == DockMode.TOP) {
            quranForm.getBottomComposite().layout(true, true);
         } else if (dockMode == DockMode.FLOAT) {
            ((GridData) playerLabelList.get(audioIndex).getLayoutData()).widthHint = size.x + 40;
            // topRow.layout(true, true);
            shell.pack();
         }
      }*/

      /*if (dockMode == DockMode.FLOAT) {
         shell.pack();
      } else {
         topRow.pack();
      }*/
   }

   public String getPlayerStatus(int audioIndex) {
      String playStatus;
      if (playerController.getCurrentAudioIndex() != audioIndex) {
         return meaning("STOPPED");
      }

      int code = playerController.getStatus();

      if (code == PlayerController.PAUSED) {
         playStatus = meaning("PAUSED");
      } else if (code == PlayerController.PLAYING) {
         playStatus = meaning("PLAYING");
      } else /*if (code == BasicPlayerEvent.STOPPED)*/{
         playStatus = meaning("STOPPED");
      }
      return playStatus;
   }

   private void createBottomRow() {
      GridData gd;
      GridLayout gl;

      bottomRow.setLayout(new FillLayout());

      gl = new GridLayout(dockMode == DockMode.FLOAT ? 6 : 4, false);
      gl.marginWidth = 3;
      gl.horizontalSpacing = 2;
      gl.verticalSpacing = gl.marginHeight = 4;
      Composite bottomComposite = new Composite(bottomRow, SWT.NONE);
      bottomComposite.setLayout(gl);

      Label repeatLabel = new Label(bottomComposite, SWT.NONE);
      repeatLabel.setText(meaning("REPEAT") + ":");

      repeatCombo = new Combo(bottomComposite, SWT.READ_ONLY);
      int max = props.getInt("audio.maxRepeatTime", 10);
      String[] items;
      if (max <= 1) {
         max = 10;
      }
      items = new String[max];
      items[0] = meaning("NO_REPEAT");
      for (int i = 1; i < max; i++) {
         items[i] = meaning("TIMES", String.valueOf(i + 1));
      }
      repeatCombo.setItems(items);
      repeatCombo.select(playerController.getRepeatTime() - 1);
      repeatCombo.setVisibleItemCount(10);
      repeatCombo.setEnabled(playerController.isMultiAya());
      repeatCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            playerController.setRepeatTime(repeatCombo.getSelectionIndex() + 1);
         }
      });
      gd = new GridData();
      gd.horizontalSpan = dockMode == DockMode.FLOAT ? 1 : 3;
      repeatCombo.setLayoutData(gd);

      gd = new GridData();
      gd.horizontalIndent = 10;
      Label waitLabel = new Label(bottomComposite, SWT.NONE);
      waitLabel.setLayoutData(gd);
      waitLabel.setText(meaning("INTERVAL") + ":");

      intervalCombo = new Combo(bottomComposite, SWT.READ_ONLY);
      intervalCombo.setItems(new String[] { meaning("NO_INTERVAL"), "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5",
            "4.0", "4.5", "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5", "9.0", "9.5", "10.0" });
      intervalCombo.select(playerController.getInterval() / 500);
      intervalCombo.setEnabled(playerController.isMultiAya());
      intervalCombo.setVisibleItemCount(10);
      intervalCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            playerController.setInterval(intervalCombo.getSelectionIndex() * 500);
         }
      });

      gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
      gd.widthHint = 10;
      Label secondsLabel = new Label(bottomComposite, SWT.NONE);
      secondsLabel.setLayoutData(gd);
      secondsLabel.setText(meaning("SECOND_ABBR"));
      secondsLabel.setToolTipText(meaning("SECONDS"));

      String[] playScopeKeys = new String[] { PlayerController.PS_CONTINUOUS, PlayerController.PS_JUZ,
            PlayerController.PS_HIZB_QUARTER, PlayerController.PS_SURA, PlayerController.PS_PAGE,
            PlayerController.PS_AYA };
      playScopeItems = new String[] { "CONTINUOUS", "JUZ", "HIZB_QUARTER", "SURA", "PAGE", "AYA" };
      playScopePopupMenu = new Menu(shell, SWT.POP_UP);
      String playScope = props.getString("audio.playScope", "continuous");
      int selection = 0;
      for (int i = 0; i < playScopeKeys.length; i++) {
         MenuItem mi = new MenuItem(playScopePopupMenu, SWT.RADIO);
         if (playScopeKeys[i].equals(playScope)) {
            mi.setSelection(true);
            selection = i;
         }
         mi.setText(meaning(playScopeItems[i]));
         mi.setData(playScopeKeys[i]);
         mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               MenuItem item = (MenuItem) e.getSource();
               if (item.getSelection()) {
                  String data = (String) item.getData();
                  playerController.setPlayScope(data);
                  setContinuityImage(playerController.isMultiAya(), item.getText());
               }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
               widgetSelected(e);
            }
         });
      }

      gd = new GridData(SWT.END, SWT.CENTER, true, true);
      playScopeBut = new Button(bottomComposite, SWT.FLAT | SWT.TOGGLE);
      playScopeBut.setLayoutData(gd);
      setContinuityImage(playerController.isMultiAya(), meaning(playScopeItems[selection]));
      playScopeBut.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Point loc = display.map(playScopeBut, null, 0, 0);
            Point size = playScopeBut.getSize();
            playScopePopupMenu.setLocation(loc.x, loc.y + size.y);
            playScopeBut.setSelection(false);
            playScopePopupMenu.setVisible(true);
         }
      });
   }

   protected void setContinuityImage(boolean continious, String text) {
      intervalCombo.setEnabled(continious);
      repeatCombo.setEnabled(continious);
      playScopeBut.setToolTipText(String.format("%s: %s", meaning("PLAY_SCOPE"), text));
      if (continious) {
         playScopeBut.setImage(multiAyaImage);
      } else {
         playScopeBut.setImage(singleAyaImage);
      }
   }

   private void createMiddleRow() {
      RowLayout rl;
      GridLayout gl;
      GridData gd;

      gl = new GridLayout(3, false);
      gl.verticalSpacing = 10;
      if (dockMode == DockMode.BOTTOM || dockMode == DockMode.TOP) {
         if (isRtl) {
            // gl.marginLeft = 40;
         } else {
            // gl.marginRight = 40;
         }
      }
      middleRow.setLayout(gl);

      gd = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
      gd.horizontalSpan = 3;
      gd.heightHint = 13;
      gd.verticalIndent = 4;
      seekProgressBar = new ProgressBar(middleRow, SWT.SMOOTH | SWT.HORIZONTAL);
      seekProgressBar.setMaximum(MAX_SEEK_VALUE);
      seekProgressBar.setSelection(0);
      seekProgressBar.setLayoutData(gd);
      seekProgressBar.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDown(MouseEvent e) {
            ProgressBar progressBar = (ProgressBar) e.getSource();
            handleSeeker(e, progressBar);
            seek(progressBar.getSelection());
         }
      });

      checkIfSeekIsSupported(0);

      gd = new GridData();
      gd.widthHint = 36;
      gd.heightHint = 36;
      // gd.verticalIndent = 5;
      playPauseItem = new Button(middleRow, SWT.PUSH | SWT.FLAT);
      playPauseItem.setLayoutData(gd);
      playerTogglePlayPause(playerController.getStatus() == PlayerController.PLAYING);

      playPauseItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            PlayStatus ps = (PlayStatus) ((Widget) e.getSource()).getData();
            quranForm.playerUiController.playerTogglePlayPause(ps == PlayStatus.PAUSE, true);
         }
      });

      rl = getNewRowLayout(SWT.HORIZONTAL);
      rl.spacing = 2;
      rl.marginLeft = 5;

      gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
      Composite nextPrevComposite = new Composite(middleRow, SWT.NONE);
      nextPrevComposite.setLayoutData(gd);
      nextPrevComposite.setLayout(rl);

      prevItem = new Button(nextPrevComposite, SWT.PUSH | SWT.FLAT);
      prevItem.setData("prev");
      prevItem.setImage(isRtl ? nextAyaImage : prevAyaImage);
      prevItem.setToolTipText(meaning("PREV_AYA"));

      stopItem = new Button(nextPrevComposite, SWT.PUSH | SWT.FLAT);
      stopItem.setImage(stopImage);
      stopItem.setToolTipText(meaning("STOP"));
      stopItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            quranForm.playerUiController.playerStop(true);
         }
      });

      SelectionListener navSelectionListener = new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            quranForm.playerUiController.navigate((String) ((Widget) e.getSource()).getData());
         }
      };

      nextItem = new Button(nextPrevComposite, SWT.PUSH | SWT.FLAT);
      nextItem.setData("next");
      nextItem.setImage(isRtl ? prevAyaImage : nextAyaImage);
      nextItem.setToolTipText(meaning("NEXT_AYA"));

      nextItem.addSelectionListener(navSelectionListener);
      prevItem.addSelectionListener(navSelectionListener);

      gl = new GridLayout(2, false);
      Composite volumeComposite = new Composite(middleRow, SWT.NONE);
      gl.horizontalSpacing = gl.verticalSpacing = gl.marginHeight = gl.marginWidth = 0;
      volumeComposite.setLayout(gl);

      gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
      gd.heightHint = 16;
      gd.widthHint = 20;
      gd.horizontalIndent = 5;
      volumeCanvas = new Canvas(volumeComposite, SWT.NONE);
      volumeCanvas.setLayoutData(gd);

      volumeCanvas.addPaintListener(new PaintListener() {
         public void paintControl(PaintEvent e) {
            repaintAudioIcon(e);
         }

      });
      volumeCanvas.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDown(MouseEvent e) {
            if (e.button == 1) {
               toggleMute();
            }
         }
      });

      gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
      gd.heightHint = 12;
      gd.widthHint = 50;

      volumeProgressBar = new ProgressBar(volumeComposite, SWT.SMOOTH | SWT.HORIZONTAL);
      volumeProgressBar.setSelection(volume);
      volumeProgressBar.setMaximum(MAX_VOLUME_VALUE);
      volumeProgressBar.setLayoutData(gd);
      volumeProgressBar.setToolTipText(meaning("VOLUME"));
      volumeProgressBar.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            if (e.stateMask == SWT.CTRL && e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_UP
                  || e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT || e.keyCode == SWT.HOME
                  || e.keyCode == SWT.END || e.keyCode == SWT.PAGE_DOWN || e.keyCode == SWT.PAGE_UP) {
               ProgressBar progressBar = (ProgressBar) e.getSource();
               volume = progressBar.getSelection();
               volumeCanvas.redraw();
               playerController.setVolume(volume);
            }
         }
      });
      volumeProgressBar.addMouseMoveListener(new MouseMoveListener() {
         public void mouseMove(MouseEvent e) {
            if ((e.stateMask & SWT.BUTTON1) != 0) {
               ProgressBar progressBar = (ProgressBar) e.getSource();
               handleVolume(e, progressBar);
            }
         }
      });
      volumeProgressBar.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDown(MouseEvent e) {
            ProgressBar progressBar = (ProgressBar) e.getSource();
            handleVolume(e, progressBar);
         }

         @Override
         public void mouseUp(MouseEvent e) {
            ProgressBar progressBar = (ProgressBar) e.getSource();
            volume = progressBar.getSelection();
            volumeCanvas.redraw();
            playerController.setVolume(volume);
         }
      });
   }

   public void checkIfSeekIsSupported(int reciterIndex) {
      // underlying player engine only supports seeking and progress for files, not online content
      PlayableObject cpo = playerController.getCurrentPlayableObject();
      if (cpo != null) {
         seekProgressBar.setEnabled(cpo.getFile() != null);
      }
   }

   public void seek(int selection) {
      quranForm.playerUiController.seek(((float) selection) / MAX_SEEK_VALUE);
   }

   private void repaintAudioIcon(PaintEvent e) {
      Image img;
      if (volume <= 0) {
         img = volumeImage0;
      } else if (volume < 33) {
         img = volumeImage1;
      } else if (volume < 66) {
         img = volumeImage2;
      } else {
         img = volumeImage3;
      }
      e.gc.drawImage(img, 0, 0);
   }

   private void toggleMute() {
      if (volume == 0) {
         volume = 15;
      } else {
         volume = 0;
      }
      volumeProgressBar.setSelection(volume);
      playerController.setVolume(volume);
      volumeCanvas.redraw();
   }

   public void updateVolume() {
      volume = playerController.getVolume();
      volumeProgressBar.setSelection(volume);
      volumeCanvas.redraw();
   }

   private void handleVolume(MouseEvent e, ProgressBar progressBar) {
      handleProgressBar(e, progressBar, 7, MAX_VOLUME_VALUE);
   }

   private void handleSeeker(MouseEvent e, ProgressBar progressBar) {
      handleProgressBar(e, progressBar, 4, MAX_SEEK_VALUE);
   }

   private void handleProgressBar(MouseEvent e, ProgressBar progressBar, int threshold, int maxProgress) {
      int width = progressBar.getSize().x;
      int progress = 0;
      int x = e.x < 0 ? 0 : e.x > width ? width : e.x;
      if (x < threshold) {
         progress = 0;
      } else if (width - x < threshold) {
         progress = maxProgress;
      } else {
         float f = (float) x / width;
         progress = (int) (f * maxProgress);
      }
      progressBar.setSelection(progress);
   }

   private RowLayout getNewRowLayout(int direction) {
      RowLayout rl = new RowLayout(direction);
      rl.fill = true;
      rl.marginBottom = rl.marginTop = rl.marginLeft = rl.marginRight = 0;
      rl.spacing = 0;
      return rl;
   }

   public void playerTogglePlayPause(boolean play) {
      if (play) {
         playPauseItem.setImage(pauseImage);
         playPauseItem.setData(PlayStatus.PLAY);
         playPauseItem.setToolTipText(meaning("PAUSE"));
      } else {
         playPauseItem.setImage(playImage);
         playPauseItem.setData(PlayStatus.PAUSE);
         playPauseItem.setToolTipText(meaning("PLAY"));
      }
   }

   public void progress(float progressPercent) {
      seekProgressBar.setSelection(Math.round(progressPercent));
   }

   /**
    * @return progress value between 0 and {@link #MAX_SEEK_VALUE}.
    */
   public int getProgress() {
      return seekProgressBar.getSelection();
   }

   public String getFormId() {
      return FORM_ID;
   }

   public void updateRecitationPopupMenu(int reciterIndex) {
      MenuItem[] mis = recitationPopupMenu.getItems();
      for (MenuItem menuItem : mis) {
         if (config.getAudio().getCurrent().id.equals(menuItem.getData())) {
            menuItem.setSelection(true);
         } else {
            menuItem.setSelection(false);
         }
      }
   }

   public void stop() {
      seekProgressBar.setSelection(0);
   }

   public DockMode getDockMode() {
      return dockMode;
   }

   public void toggle(boolean show) {
      if (dockMode == DockMode.FLOAT) {
         if (show)
            show();
         else
            shell.close();
      } else {
         if (show) {
            if (dockMode == DockMode.TASKPANE) {
               if (!shell.isVisible()) {
                  quranForm.getWorkPane().pack(true);
               }
            } else {
               // quranForm.getBottomComposite().pack();
               // quranForm.getContentComposite().pack();
            }
            if (shell.isVisible()) {
               EventUtils.sendEvent(parent, EventProtocol.RECREATE_VIEW);
            }
         } else {
            body.dispose();
            if (dockMode == DockMode.TASKPANE) {
               // quranForm.getWorkPane().pack(true);
            } else {
               // quranForm.getBottomComposite().pack();
               // quranForm.getContentComposite().pack();
            }
            if (shell.isVisible()) {
               EventUtils.sendEvent(parent, EventProtocol.RECREATE_VIEW);
            }
         }

         /*body.setVisible(show);
         body.layout(true);*/
      }
   }

   public boolean isClosed() {
      return (dockMode == DockMode.FLOAT && isDisposed()) || (body.isDisposed());
   }

   @Override
   public boolean isDisposed() {
      return body == null || body.isDisposed();
   }

   public void setDockMode(DockMode dockMode) {
      this.dockMode = dockMode;

   }

   public void dispose() {
      if (dockMode == DockMode.FLOAT && !isDisposed()) {
         shell.dispose();
      } else {
         body.dispose();
      }
   }

   public int getPlayerCount() {
      return topRowList.size();
   }
}
