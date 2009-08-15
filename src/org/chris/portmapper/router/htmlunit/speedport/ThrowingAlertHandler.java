/**
 * 
 */
package org.chris.portmapper.router.htmlunit.speedport;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.Page;

/**
 * @author chris
 * @version $Id$
 */
public class ThrowingAlertHandler implements AlertHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gargoylesoftware.htmlunit.AlertHandler#handleAlert(com.gargoylesoftware
	 * .htmlunit.Page, java.lang.String)
	 */
	public void handleAlert(Page page, String alert) {
		throw new RuntimeException("Got alert '" + alert + "' on page " + page);

	}

}
