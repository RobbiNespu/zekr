package net.sf.zekr.ui.splash;

import java.io.File;

import net.sf.zekr.common.config.ResourceManager;
import net.sf.zekr.common.runtime.Naming;
import net.sf.zekr.engine.log.Logger;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractSplachScreen {
	protected final Logger logger = Logger.getLogger(this.getClass());

	protected final ResourceManager resource = ResourceManager.getInstance();
	protected Image splashImage = null;
	// protected boolean showSplash = ApplicationConfig.getInstance().getProps().getBoolean(
	// "options.general.showSplash");
	protected final boolean showSplash;

	protected Shell shell;
	protected Display display;

	public AbstractSplachScreen(Display display) {
		showSplash = !new File(Naming.CONFIG_DIR + "/.DONTSHOWSPASH").exists();
		this.display = display;
		splashImage = new Image(display, resource.getString("image.splashScreen"));
	}

	/**
	 * Shows a splash screen until the whole application is started. This can be done by disposing splash screen after
	 * other parts of the application are started.
	 */
	protected abstract void showSplash();

	public void show() {
		if (showSplash)
			showSplash();
	}

	public abstract void dispose();

}
