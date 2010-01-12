/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Nov 6, 2009
 */
package net.sf.zekr.ui;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sf.zekr.common.config.ApplicationConfig;
import net.sf.zekr.common.config.GlobalConfig;
import net.sf.zekr.common.config.IUserView;
import net.sf.zekr.common.resource.FilteredQuranText;
import net.sf.zekr.common.resource.IQuranLocation;
import net.sf.zekr.common.resource.IQuranText;
import net.sf.zekr.common.resource.JuzProperties;
import net.sf.zekr.common.resource.QuranLocation;
import net.sf.zekr.common.resource.QuranPropertiesUtils;
import net.sf.zekr.common.resource.QuranText;
import net.sf.zekr.common.resource.SajdaProperties;
import net.sf.zekr.common.resource.filter.IQuranFilter;
import net.sf.zekr.common.resource.filter.QuranFilterUtils;
import net.sf.zekr.common.util.HyperlinkUtils;
import net.sf.zekr.common.util.UriUtils;
import net.sf.zekr.engine.audio.PlayerController;
import net.sf.zekr.engine.audio.ui.AudioPlayerForm;
import net.sf.zekr.engine.bookmark.ui.BookmarkReferenceForm;
import net.sf.zekr.engine.bookmark.ui.BookmarkSetForm;
import net.sf.zekr.engine.bookmark.ui.BookmarkUtils;
import net.sf.zekr.engine.bookmark.ui.ManageBookmarkSetsForm;
import net.sf.zekr.engine.log.Logger;
import net.sf.zekr.engine.search.Range;
import net.sf.zekr.engine.translation.ui.CustomTranslationListForm;
import net.sf.zekr.engine.update.UpdateManager;
import net.sf.zekr.ui.helper.FormUtils;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is a controller for all commands may done through a shortkey.
 * 
 * @author Mohsen Saboorian
 */
public class QuranFormController {
	public class ActionItem {
		Runnable runnable;
		private Object param;

		public ActionItem(Runnable runnable) {
			this.runnable = runnable;
		}

		public ActionItem(Runnable runnable, Object param) {
			this.runnable = runnable;
			this.param = param;
		}

		public Listener toListener() {
			return new Listener() {
				public void handleEvent(Event event) {
					runnable.run();
				}
			};
		}

		public SelectionListener toSelectionListener() {
			return new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					runnable.run();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			};
		}
	}

	private Logger logger = Logger.getLogger(QuranFormController.class);
	private QuranForm quranForm;
	private IUserView uvc;
	private ApplicationConfig config;
	private BookmarkSetForm bsf;
	private CustomTranslationListForm crlf;

	public QuranFormController(QuranForm quranForm) {
		this.quranForm = quranForm;
		config = ApplicationConfig.getInstance();
		uvc = config.getUserViewController();
	}

	public ActionItem registerAction(final String name, final Class<?> parameterType, final Object methodParam) {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Method method = QuranFormController.this.getClass().getMethod(name, parameterType);
					method.invoke(QuranFormController.this, methodParam);
				} catch (Exception e) {
					MessageBoxUtils.showError("Error running action: " + name);
					logger.error("Error calling action: " + name, e);
				}
			}
		};
		return new ActionItem(runnable, methodParam);
	}

	public ActionItem registerAction(final String name) {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					Method method = QuranFormController.this.getClass().getMethod(name, new Class<?>[0]);
					method.invoke(QuranFormController.this, new Object[0]);
				} catch (Exception e) {
					MessageBoxUtils.showError("Error running action: " + name);
					logger.error("Error calling action: " + name, e);
				}
			}
		};
		return new ActionItem(runnable);
	}

	public void print() {
		quranForm.getQuranBrowser().execute("window.print()");
	}

	public void quit() {
		quranForm.quit();
	}

	public void savePageAs() {
		try {
			File f = MessageBoxUtils.exportFileDialog(quranForm.getShell(),
					new String[] { "HTML Files", "All Files (*.*)" }, new String[] { "*.html;*.htm", "*.*" });
			if (f == null || f.isDirectory()) {
				return;
			}
			if (!f.getName().toUpperCase().endsWith(".HTM") && !f.getName().toUpperCase().endsWith(".HTML")) {
				f = new File(f.getParent(), f.getName() + ".html");
			}
			logger.info("Save current view to file: " + f);
			FileUtils.copyFile(UriUtils.toFile(quranForm.getCurrentUri()), f);
		} catch (Exception e) {
			MessageBoxUtils.showActionFailureError(e);
		}
	}

	public void about() {
		AboutForm af = new AboutForm(quranForm.getShell());
		af.getShell().setLocation(FormUtils.getCenter(quranForm.getShell(), af.getShell()));
		af.show();
	}

	public void check4Update() {
		UpdateManager manager = new UpdateManager(quranForm);
		manager.check(true);
	}

	public void gotoForm() {
		Shell[] shells = quranForm.display.getShells();
		boolean found = false;
		for (int i = 0; i < shells.length; i++) {
			if ("GOTO_FORM".equals(shells[i].getData("FORM_ID"))) {
				found = true;
			}
		}
		if (!found) {
			new GotoForm(quranForm.getShell(), quranForm).open();
		}
	}

	public void gotoRandomAya() {
		Random rnd = new Random(new Date().getTime());
		int juz = rnd.nextInt(30) + 1;
		Range r = QuranPropertiesUtils.getSuraInsideJuz(juz);
		int sura = rnd.nextInt(r.to - r.from + 1) + r.from;
		int aya = rnd.nextInt(QuranPropertiesUtils.getSura(sura).getAyaCount()) + 1;
		navTo(sura, aya);
	}

	public void navTo(int suraNumber, int ayaNumber) {
		quranForm.navTo(suraNumber, ayaNumber);
	}

	public void navTo(IQuranLocation quranLocation) {
		quranForm.navTo(quranLocation);
	}

	public void gotoNextSura() {
		if (uvc.getLocation().getSura() < QuranPropertiesUtils.QURAN_SURA_COUNT) {
			navTo(uvc.getLocation().getSura() + 1, 1);
		}
	}

	public void gotoPrevSura() {
		if (uvc.getLocation().getSura() > 1) {
			navTo(uvc.getLocation().getSura() - 1, 1);
		}
	}

	public void gotoNextJuz() {
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (jp.getIndex() < 30) {
			jp = QuranPropertiesUtils.getJuz(jp.getIndex() + 1);
			navTo(jp.getSuraNumber(), jp.getAyaNumber());
		}
	}

	public void gotoPrevJuz() {
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (jp.getIndex() > 1) {
			jp = QuranPropertiesUtils.getJuz(jp.getIndex() - 1);
			navTo(jp.getSuraNumber(), jp.getAyaNumber());
		}
	}

	public void gotoPrevSajda() {
		List<SajdaProperties> sajdaList = QuranPropertiesUtils.getSajdaList();
		IQuranLocation prevSajda = null;
		int i = sajdaList.size() - 1;
		for (; i >= 0; i--) {
			SajdaProperties sp = sajdaList.get(i);
			prevSajda = new QuranLocation(sp.getSuraNumber(), sp.getAyaNumber());
			if (prevSajda.compareTo(uvc.getLocation()) < 0) {
				break;
			}
		}
		if (i > 0) {
			navTo(prevSajda);
		}
	}

	public void gotoNextSajda() {
		List<SajdaProperties> sajdaList = QuranPropertiesUtils.getSajdaList();
		IQuranLocation nextSajda = null;
		int i = 0;
		for (; i < sajdaList.size(); i++) {
			SajdaProperties sp = sajdaList.get(i);
			nextSajda = new QuranLocation(sp.getSuraNumber(), sp.getAyaNumber());
			if (nextSajda.compareTo(uvc.getLocation()) > 0) {
				break;
			}
		}
		if (i < sajdaList.size()) {
			navTo(nextSajda);
		}
	}

	public void gotoNextHizbQuarter() {
		int quad = QuranPropertiesUtils.getHizbQuadIndex(uvc.getLocation());
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (quad < 7) {
			IQuranLocation newLoc = jp.getHizbQuarters()[quad + 1];
			navTo(newLoc);
		} else if (jp.getIndex() < 30) {
			gotoNextJuz();
		}
	}

	public void gotoPrevHizbQuarter() {
		int quad = QuranPropertiesUtils.getHizbQuadIndex(uvc.getLocation());
		JuzProperties jp = QuranPropertiesUtils.getJuzOf(uvc.getLocation());
		if (quad > 0) {
			IQuranLocation newLoc = jp.getHizbQuarters()[quad - 1];
			navTo(newLoc);
		} else if (jp.getIndex() > 1) {
			gotoPrevJuz();
		}
	}

	public void gotoNextAya() {
		IQuranLocation nextLoc = uvc.getLocation().getNext();
		if (nextLoc != null) {
			navTo(nextLoc);
		}
	}

	public void gotoPrevAya() {
		IQuranLocation prevLoc = uvc.getLocation().getPrev();
		if (prevLoc != null) {
			navTo(prevLoc);
		}
	}

	public void gotoNextPage() {
		if (uvc.getPage() < config.getQuranPaging().getDefault().size()) {
			navTo(config.getQuranPaging().getDefault().getQuranPage(uvc.getPage() + 1).getFrom());
		}
	}

	public void gotoPrevPage() {
		if (uvc.getPage() > 1) {
			navTo(config.getQuranPaging().getDefault().getQuranPage(uvc.getPage() - 1).getFrom());
		}
	}

	public void toggleFullScreen() {
		quranForm.setFullScreen(!quranForm.getShell().getFullScreen(), true);
	}

	public void findBookmarkReferences() {
		IQuranLocation loc = uvc.getLocation();
		logger.info("Find bookmark references to: " + loc);
		List<Object[]> resultList = BookmarkUtils.findReferences(config.getBookmark(), loc);
		logger.debug("Show references in form.");
		new BookmarkReferenceForm(quranForm.getShell(), resultList, loc).open();
	}

	public void manageBookmarks() {
		Shell shell = quranForm.getShell();
		if (bsf != null && Arrays.asList(shell.getShells()).contains(bsf.getShell())) { // shell is already
			// open
			bsf.getShell().forceActive();
			return;
		}

		bsf = new BookmarkSetForm(shell);
		bsf.open();
	}

	public void manageBookmarkSets() {
		new ManageBookmarkSetsForm(quranForm.getShell()).open();
	}

	/**
	 * Bring up bookmark item form.
	 */
	public void bookmarkThis() {
		try {
			String titleMode = config.getProps().getString("bookmark.add.titleMode", "quran");
			String title;
			if (titleMode.equals("quran") || config.getTranslation().getDefault() == null) {
				IQuranText qt = new FilteredQuranText(QuranText.getSimpleTextInstance(), IQuranFilter.NONE);
				title = QuranFilterUtils.filterHarakat(qt.get(uvc.getLocation()));
			} else { // translation mode
				title = config.getTranslation().getDefault().get(uvc.getLocation());
			}
			BookmarkSetForm.addNew(quranForm.getShell(), uvc.getLocation(), net.sf.zekr.common.util.StringUtils
					.abbreviate(title, 20));
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void reload() {
		quranForm.reload();
	}

	public void toggleAudioPanel() {
		AudioPlayerForm playerForm = quranForm.playerUiController.getAudioControllerForm();
		quranForm.playerUiController.toggleAudioControllerForm(playerForm == null || playerForm.isDisposed());
	}

	public void playerStop() {
		quranForm.playerUiController.playerStop(true);
	}

	public void playerTogglePlayPause() {
		int playerStatus = config.getPlayerController().getStatus();
		quranForm.playerUiController.playerTogglePlayPause(playerStatus != PlayerController.PLAYING, true);
	}

	public void toggleDetailPanel() {
		boolean toggleState = config.getProps().getBoolean("view.panel.detail", true);
		quranForm.togglePanel(!toggleState);
	}

	public void configureMultiTrans() {
		Shell shell = quranForm.getShell();
		if (crlf != null && Arrays.asList(shell.getShells()).contains(crlf.getShell())) { // shell is already
			// open
			crlf.getShell().forceActive();
			return;
		}

		crlf = new CustomTranslationListForm(quranForm.getShell());
		crlf.show();
		crlf.loopEver();
	}

	public void playerNext() {
		quranForm.playerUiController.navigate("next");
	}

	public void playerPrev() {
		quranForm.playerUiController.navigate("prev");
	}

	public void onlineHelp() {
		HyperlinkUtils.openBrowser(GlobalConfig.HELP_PAGE);
	}
}
