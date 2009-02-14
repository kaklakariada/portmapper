/**
 * 
 */
package org.chris.portmapper.gui.util;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

/**
 * This class implements a lable that looks and behaves like a link, i.e. you
 * can click on it and the URL is opened in a browser.
 * 
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class URLLabel extends JLabel {

	private static Log logger = LogFactory.getLog(URLLabel.class);

	private String url, text;

	private static BrowserLauncher launcher;
	static {
		try {
			launcher = new BrowserLauncher();
		} catch (BrowserLaunchingInitializingException e) {
			logger
					.warn(
							"Could not initialize browser launcher: links will not work",
							e);
		} catch (UnsupportedOperatingSystemException e) {
			logger
					.warn(
							"Could not initialize browser launcher: links will not work",
							e);
		}
	}

	public URLLabel(String name) {
		super();
		this.url = name;
		this.text = name;
		this.setLabelText();
		this.setName(name);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				logger.debug("User clicked on URLLabel: open URL '" + url
						+ "' in browser");
				if (launcher != null) {
					launcher.openURLinBrowser(url);
				} else {
					logger
							.warn("Browser launcher was not initialized, please open url manually: "
									+ url);
				}
			}

			public void mouseEntered(MouseEvent arg0) {

			}

			public void mouseExited(MouseEvent arg0) {

			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {

			}
		});
	}

	private void setLabelText() {
		super.setText("<html><a href=\\\\\\\\\\\"" + url + "\\\\\\\\\\\">"
				+ text + "</a></html>");
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		setLabelText();
	}

	public String getLabel() {
		return text;
	}

	public void setLabel(String text) {
		this.text = text;
		setLabelText();
	}
}
