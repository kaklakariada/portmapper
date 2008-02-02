/**
 * 
 */
package org.chris.portmapper.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JLabel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class URLLabel extends JLabel {

	private Log logger = LogFactory.getLog(this.getClass());
	private String url;

	public URLLabel(String text, String labelUrl) {
		super("<html><a href=\\\\\\\\\\\"" + labelUrl + "\\\\\\\\\\\">" + text
				+ "</a></html>");
		this.url = labelUrl;
		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				logger.debug("User clicked on URLLabel: open URL '" + url
						+ "' in browser");
				try {
					BrowserLauncher.openURL(url);
				} catch (IOException e) {
					logger.warn(
							"Could not start browser for URL '" + url + "'", e);
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
}
