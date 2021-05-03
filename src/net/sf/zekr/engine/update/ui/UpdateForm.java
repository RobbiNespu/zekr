/*
 *               In the name of Allah
 * This file is part of The Zekr Project. Use is subject to
 * license terms.
 *
 * Author:         Mohsen Saboorian
 * Start Date:     Feb 15, 2008
 */
package net.sf.zekr.engine.update.ui;

import java.text.DateFormat;

import net.sf.zekr.engine.update.UpdateInfo;
import net.sf.zekr.ui.BaseForm;
import net.sf.zekr.ui.helper.FormUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mohsen Saboorian
 */
public class UpdateForm extends BaseForm {
	private UpdateInfo updateInfo;

	public UpdateForm(UpdateInfo updateInfo, Shell parent) {
		this.updateInfo = updateInfo;
		this.parent = parent;
		this.display = parent.getDisplay();
		makeForm();
	}

	private void makeForm() {
		FillLayout fl = new FillLayout();
		fl.marginHeight = fl.marginWidth = 5;
		shell = createShell(parent, SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL | lang.getSWTDirection());
		shell.setLayout(fl);
		shell.setText(meaning("TITLE"));
		shell.setImages(new Image[] { new Image(display, resource.getString("icon.update.check16")),
				new Image(display, resource.getString("icon.update.check32")) });

		GridLayout gl = new GridLayout(2, false);
		Group body = new Group(shell, SWT.NONE);
		body.setText(meaning("VERSION_INFO"));
		body.setLayout(gl);

		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		Label msg = new Label(body, SWT.WRAP);
		msg.setText(updateInfo.message);
		FontData fd = msg.getFont().getFontData()[0];
		Font font = new Font(display, fd.getName(), fd.getHeight(), SWT.BOLD);
		msg.setFont(font);
		msg.setLayoutData(gd);

		newLabel(body, meaning("RELEASE_NAME") + ":", new GridData(GridData.BEGINNING));
		newText(body, updateInfo.fullName, new GridData(SWT.FILL, SWT.BEGINNING, true, false), false);

		newLabel(body, meaning("VERSION") + ":", new GridData(GridData.BEGINNING));
		newText(body, updateInfo.version, new GridData(SWT.FILL, SWT.BEGINNING, true, false), false);

		String dateStr = updateInfo.releaseDate != null ? DateFormat.getDateInstance().format(updateInfo.releaseDate)
				: "";
		newLabel(body, meaning("RELEASE_DATE") + ":", new GridData(GridData.BEGINNING));
		newText(body, dateStr, new GridData(SWT.FILL, SWT.BEGINNING, true, false), false);

		newLabel(body, meaning("BUILD_NUM") + ":", new GridData(GridData.BEGINNING));
		newText(body, updateInfo.build, new GridData(SWT.FILL, SWT.BEGINNING, true, false), false);

		newLabel(body, meaning("RELEASE_STATE") + ":", new GridData(GridData.BEGINNING));
		newText(body, updateInfo.status, new GridData(SWT.FILL, SWT.BEGINNING, true, false), false);

		newSeparator(body);

		Link downloadLink = newLink(body, "<a href=\"" + updateInfo.downloadUrl + "\">" + meaning("DOWNLOAD") + "</a>",
				new GridData(GridData.BEGINNING));
		downloadLink.setData(FormUtils.URL_DATA, updateInfo.downloadUrl);
		FormUtils.addLinkListener(downloadLink);

		Link notesLink = newLink(body, "<a href=\"" + updateInfo.noteUrl + "\">" + meaning("RELEASE_NOTES") + "</a>",
				new GridData(GridData.BEGINNING));
		notesLink.setData(FormUtils.URL_DATA, updateInfo.noteUrl);
		FormUtils.addLinkListener(notesLink);

		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gd.horizontalSpan = 2;
		Label infoLabel = new Label(body, SWT.NONE);
		infoLabel.setText(meaning("MORE_INFO") + ":");
		infoLabel.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		Text moreInfo = new Text(body, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		moreInfo.setLayoutData(gd);
		moreInfo.setEditable(false);
		moreInfo.setText(updateInfo.info);

		shell.pack();
	}

	private Label newSeparator(Group body) {
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		gd.horizontalSpan = 2;
		Label sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(gd);
		return sep;
	}

	private Text newText(Group body, String textStr, GridData gd, boolean editable) {
		Text text = new Text(body, SWT.NONE);
		text.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		text.setText(textStr);
		text.setEditable(false);
		text.setLayoutData(gd);
		return text;
	}

	private Label newLabel(Group body, String text, GridData layoutData) {
		Label label = new Label(body, SWT.NONE);
		label.setText(text);
		label.setLayoutData(layoutData);
		return label;
	}

	private Link newLink(Group body, String text, Object layoutData) {
		Link link = new Link(body, SWT.NONE);
		link.setText(text);
		link.setLayoutData(layoutData);
		return link;
	}

	public String getFormId() {
		return "UPDATE";
	}
}
