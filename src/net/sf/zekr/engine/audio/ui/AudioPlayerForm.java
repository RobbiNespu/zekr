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
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.SWT;
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
	private static final int WINDOW_VISIBILITY_THRESHOLD = 60;
	public static final String FORM_ID = "AUDIO_PLAYER_FORM";
	public static final int MAX_SEEK_VALUE = 1000;
	public static final int MAX_VOLUME_VALUE = 100;

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
	private Button contBut;
	private Image multiAyaImage;
	private Image singleAyaImage;
	private Image pauseImage;
	private Image playImage;
	private Image prevAyaImage;
	private Image nextAyaImage;
	private Image stopImage;
	private Link playerLabel;
	private Canvas playerCanvas;
	private IUserView uvc;
	private Point shellLocation;
	private Combo intervalCombo;
	private Combo repeatCombo;
	private PropertiesConfiguration props;
	private Menu recitationPopupMenu;
	private Menu playScopePopupMenu;
	private String[] playScopeItems;

	public AudioPlayerForm(QuranForm quranForm, Shell parent) {
		int l = lang.getSWTDirection();
		isRtl = l == SWT.RIGHT_TO_LEFT && GlobalConfig.hasBidiSupport;

		playerController = config.getPlayerController();
		volume = playerController.getVolume();
		uvc = config.getUserViewController();

		this.quranForm = quranForm;
		this.parent = parent;
		display = parent.getDisplay();
		props = config.getProps();

		init();
	}

	@SuppressWarnings("rawtypes")
	private void init() {
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

		fl = new FillLayout();
		body = new Composite(shell, SWT.NONE);
		RowLayout rl = getNewRowLayout(SWT.VERTICAL);
		rl.spacing = 1;
		body.setLayout(rl);

		RowData rd = new RowData();
		topRow = new Composite(body, SWT.NONE);
		topRow.setLayoutData(rd);

		new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);

		rd = new RowData();
		middleRow = new Composite(body, SWT.NONE);
		middleRow.setLayoutData(rd);

		new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);

		bottomRow = new Composite(body, SWT.NONE);

		cacheImages();

		createTopRow();
		createMiddleRow();
		createBottomRow();

		shell.pack();
		if (shellLocation != null) {
			shell.setLocation(shellLocation);
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

		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		playerLabel = new Link(topRow, SWT.FLAT);
		playerLabel.setLayoutData(gd);

		recitationPopupMenu = new Menu(shell, SWT.POP_UP);
		Menu origMenu = quranForm.getMenu().getRecitationListMenu();
		MenuItem[] items = origMenu.getItems();
		for (int i = 0; i < items.length; i++) {
			MenuItem mi = new MenuItem(recitationPopupMenu, items[i].getStyle());
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
			mi.setSelection(items[i].getSelection());
			Listener[] listeners = items[i].getListeners(SWT.Selection);
			for (int j = 0; j < listeners.length; j++) {
				mi.addListener(SWT.Selection, listeners[j]);
			}
		}

		playerLabel.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				if ("recitation".equals(event.text)) {
					Point loc = display.map(playerLabel, null, 0, 0);
					Point size = playerLabel.getSize();
					recitationPopupMenu.setLocation(loc.x, loc.y + size.y);
					recitationPopupMenu.setVisible(true);
				}
			}

			public void widgetSelected(SelectionEvent event) {
				widgetDefaultSelected(event);
			}
		});
		updatePlayerLabel();
	}

	public void updatePlayerLabel() {
		if (shell.isDisposed() || config.getAudio().getCurrent() == null) {
			return;
		}
		AudioData audioData = config.getAudio().getCurrent();
		String status = getPlayerStatus();
		IQuranLocation l = uvc.getLocation();
		String text = String.format("%s (%s):%s | <a href=\"recitation\">%s: %s</a> | %s", l.getSuraName(), l.getSura(),
				l.getAya(), meaning("RECITER"), audioData.getLocalizedName(), status);
		String tooltip = String.format("%s (%s):%s | %s: %s | %s", l.getSuraName(), l.getSura(), l.getAya(),
				meaning("RECITER"), audioData.getLocalizedName(), status);
		playerLabel.setText(text);
		playerLabel.setToolTipText(tooltip);
	}

	public String getPlayerStatus() {
		String playStatus;
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

		gl = new GridLayout(6, false);
		gl.marginWidth = 4;
		gl.horizontalSpacing = 2;
		gl.verticalSpacing = gl.marginHeight = 0;
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
		gd.horizontalIndent = 8;
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
		gd.widthHint = 20;
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

		gd = new GridData(SWT.END, SWT.FILL, true, true);
		contBut = new Button(bottomComposite, SWT.FLAT | SWT.TOGGLE);
		contBut.setLayoutData(gd);
		setContinuityImage(playerController.isMultiAya(), meaning(playScopeItems[selection]));
		contBut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point loc = display.map(contBut, null, 0, 0);
				Point size = contBut.getSize();
				playScopePopupMenu.setLocation(loc.x, loc.y + size.y);
				contBut.setSelection(false);
				playScopePopupMenu.setVisible(true);
			}
		});
	}

	protected void setContinuityImage(boolean continious, String text) {
		intervalCombo.setEnabled(continious);
		repeatCombo.setEnabled(continious);
		contBut.setToolTipText(String.format("%s: %s", meaning("PLAY_SCOPE"), text));
		if (continious) {
			contBut.setImage(multiAyaImage);
		} else {
			contBut.setImage(singleAyaImage);
		}
	}

	private void createMiddleRow() {
		RowLayout rl;
		GridLayout gl;
		GridData gd;

		gl = new GridLayout(3, false);
		gl.verticalSpacing = 10;
		middleRow.setLayout(gl);

		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd.horizontalSpan = 3;
		gd.heightHint = 13;
		gd.verticalIndent = 5;
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
		checkIfSeekIsSupported();

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
		rl.marginLeft = 15;

		gd = new GridData();
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
		gd.horizontalIndent = 30;
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
					muteAudio();
				}
			}
		});

		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
		gd.heightHint = 15;
		gd.widthHint = 100;
		volumeProgressBar = new ProgressBar(volumeComposite, SWT.SMOOTH | SWT.HORIZONTAL);
		volumeProgressBar.setSelection(volume);
		volumeProgressBar.setMaximum(MAX_VOLUME_VALUE);
		volumeProgressBar.setLayoutData(gd);
		volumeProgressBar.setToolTipText(meaning("VOLUME"));
		volumeProgressBar.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if ((e.stateMask & SWT.BUTTON1) != 0) {
					ProgressBar progress = (ProgressBar) e.getSource();
					handleVolume(e, progress);
				}
			}
		});
		volumeProgressBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ProgressBar progress = (ProgressBar) e.getSource();
				handleVolume(e, progress);
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

	public void checkIfSeekIsSupported() {
		// underlying player engine only supports seeking and progress for files
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

	private void muteAudio() {
		volume = 0;
		volumeProgressBar.setSelection(0);
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

	public void close() {
		shell.close();
	}

	public String getFormId() {
		return FORM_ID;
	}

	public void updateRecitationPopupMenu() {
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
}
