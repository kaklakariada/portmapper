/**
 * 
 */
package org.chris.portmapper.router.htmlunit.speedport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

/**
 * @author chris
 * 
 */
public class Test {

	private final static Log log = LogFactory.getLog(Test.class);

	static WebClient webClient;

	/**
	 * @param args
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FailingHttpStatusCodeException
	 * @throws InterruptedException
	 */
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		String password = "";
		String hostname = "";

		webClient = new WebClient(BrowserVersion.FIREFOX_3);
		webClient.setJavaScriptEnabled(true);
		webClient.setRefreshHandler(new ThreadedRefreshHandler());
		webClient.setAlertHandler(new ThrowingAlertHandler());

		String loginUrl = "http://" + hostname + "/top_start_passwort.stm";
		log.debug("Getting login page " + loginUrl);
		HtmlPage page = webClient.getPage(loginUrl);
		log.debug("Getting third frame of page " + page);

		List<FrameWindow> window = page.getFrames();
		page = (HtmlPage) window.get(2).getEnclosedPage();

		HtmlForm form = page.getFormByName("tF");
		log.debug("Fill and submit form " + form);
		form.getInputByName("pws").setValueAttribute(password);
		page = ((ClickableElement) form.getHtmlElementById("t_but4")).click();

		assert page.getFrames().size() == 0;
		// HtmlPage header = (HtmlPage) window.get(0).getEnclosedPage();
		// HtmlPage mstart = (HtmlPage) window.get(1).getEnclosedPage();
		// HtmlPage hctistart = (HtmlPage) window.get(2).getEnclosedPage();
		log.debug("Sleeping 3s");
		Thread.sleep(3000);

		if (getMenuPage().getElementById("m_but6") == null) {
			printWindow();
			throw new RuntimeException("Not logged in");
		}

		// Click Uebersicht
		((ClickableElement) getMenuPage().getElementById("m_but41")).click();

		String ipText = getElementAsTextByXPath(getMainPage(),
				"/html/body/div/div/div[3]/table/tbody/tr/td[2]/text()");

		String firmwareVersion = getElementAsTextByXPath(
				getMainPage(),
				"/html/body/div/div/div[17]/table/tbody/tr/td[2]/script/following-sibling::text()");

		System.out.println(ipText);
		System.out.println(firmwareVersion);

		log.debug("Click Netzwerk button");
		// click Netzwerk button m_but32
		page = ((ClickableElement) getMenuPage().getElementById("m_but32"))
				.click();
		String internalIP = getElementAsTextByXPath(
				getMainPage(),
				"/html/body/div/div/div[4]/table/tbody/tr/td[2]/script/following-sibling::text()");

		System.out.println(internalIP);

		log.debug("Click button NAT & Portregeln");
		page = clickButtonByXPath(getMainPage(),
				"/html/body/div/div/div[9]/table/tbody/tr/td/a");
		log.debug("Click button Port-Umleitung");
		page = clickButtonByXPath(getMainPage(),
				"/html/body/div/div/div[7]/table/tbody/tr/td/a");

		List<?> portForwardingLinks = getMainPage().getByXPath(
				"/html/body/div/div/div/table/tbody/tr/td/a");
		for (Object object : portForwardingLinks) {
			HtmlAnchor link = (HtmlAnchor) object;
			String linkTextName = getElementAsTextByXPath(link,
					"nobr/b/following-sibling::text()");
			if (!linkTextName.equals("Neue Regel definieren")) {
				link.click();
				// printWindow();
				String ruleName = ((DomNode) getMainPage().getFirstByXPath(
						"/html/body/div/div/div[5]/table/tbody/tr/td[2]/input"))
						.asText();

				HtmlCheckBoxInput activeCheckBox = getMainPage()
						.getFirstByXPath(
								"/html/body/div/div/div[5]/table/tbody/tr/td[3]/input");

				boolean active = activeCheckBox.isChecked();
				System.out.println("Active: " + active);

				HtmlSelect internalHost = getMainPage()
						.getFirstByXPath(
								"/html/body/div/div/div[6]/table/tbody/tr/td[2]/select");

				HtmlOption selectedHost = internalHost.getSelectedOptions()
						.get(0);
				String selectedHostName = selectedHost.asText();
				System.out.println("internalHost: " + selectedHostName);

				List<Integer> externalPortsTCP = csvToIntList(getElementAsTextByXPath(
						getMainPage(),
						"/html/body/div/div/div[11]/table/tbody/tr/td[2]/input"));
				List<Integer> externalPortsUDP = csvToIntList(getElementAsTextByXPath(
						getMainPage(),
						"/html/body/div/div/div[12]/table/tbody/tr/td[2]/input"));
				List<Integer> internalPortsTCP = csvToIntList(getElementAsTextByXPath(
						getMainPage(),
						"/html/body/div/div/div[15]/table/tbody/tr/td[2]/input"));
				List<Integer> internalPortsUDP = csvToIntList(getElementAsTextByXPath(
						getMainPage(),
						"/html/body/div/div/div[16]/table/tbody/tr/td[2]/input"));
				System.out.println("ext tcp: " + externalPortsTCP);
				System.out.println("ext udp: " + externalPortsUDP);
				System.out.println("int tcp: " + internalPortsTCP);
				System.out.println("int udp: " + internalPortsUDP);

				if (externalPortsTCP.size() > 0 && externalPortsUDP.size() > 0) {
					throw new RuntimeException(
							"Mapping "
									+ ruleName
									+ " has both TCP and UDP ports. This is not supported, please delete this mapping.");
				}
				if (externalPortsTCP.isEmpty() && externalPortsUDP.isEmpty()) {
					throw new RuntimeException(
							"Mapping "
									+ ruleName
									+ " has neither TCP nor UDP ports. This is not supported, please delete this mapping.");
				}

				if (externalPortsTCP.size() != internalPortsTCP.size()
						|| externalPortsUDP.size() != internalPortsUDP.size()) {
					throw new RuntimeException(
							"Mapping "
									+ ruleName
									+ " has a different number of ports for internal and external ports. This is not supported, please delete this mapping.");
				}

				if (externalPortsTCP.size() > 1 || externalPortsUDP.size() > 1) {
					throw new RuntimeException(
							"Mapping "
									+ ruleName
									+ " has more than one TCP or UDP ports. This is not supported, please delete this mapping.");
				}

				PortMapping mapping;

				// TODO: map host name to ip
				if (externalPortsTCP.size() == 1) {
					mapping = new PortMapping(Protocol.TCP, null,
							externalPortsTCP.get(0), selectedHostName,
							internalPortsTCP.get(0), ruleName);
				} else {
					mapping = new PortMapping(Protocol.UDP, null,
							externalPortsUDP.get(0), selectedHostName,
							internalPortsUDP.get(0), ruleName);
				}

				log.debug("Found mapping " + mapping.getCompleteDescription());
			}
		}

		// click logout
		page = ((ClickableElement) getMenuPage().getElementById("m_but6"))
				.click();
		printWindow();
		// printFrames(page.getFrames());
		// page = (HtmlPage) page.getFrames().get(0).getEnclosedPage();

	}

	/**
	 * @param externalPortsTCPText
	 * @return
	 */
	private static List<Integer> csvToIntList(String text) {
		String[] values = text.split(",");
		List<Integer> intValues = new ArrayList<Integer>(values.length);
		for (String value : values) {
			if (value.trim().length() > 0) {
				intValues.add(Integer.decode(value.trim()));
			}
		}
		return intValues;
	}

	private static HtmlPage clickButtonByXPath(DomNode page, String xPath)
			throws IOException {
		return ((ClickableElement) page.getFirstByXPath(xPath)).click();
	}

	private static HtmlPage getMenuPage() {
		WebWindow window = webClient.getWebWindows().get(0);
		HtmlPage page = ((HtmlPage) window.getEnclosedPage());
		if (page.getFrames().size() != 5) {
			printWindow();
			throw new RuntimeException("Not enough frames. Are we logged in?");
		}
		HtmlPage menu = (HtmlPage) page.getFrameByName("menu")
				.getEnclosedPage();
		return menu;
	}

	private static HtmlPage getMainPage() {
		WebWindow window = webClient.getWebWindows().get(0);
		HtmlPage page = ((HtmlPage) window.getEnclosedPage());
		if (page.getFrames().size() != 5) {
			printWindow();
			throw new RuntimeException("Not enough frames. Are we logged in?");
		}
		HtmlPage main = (HtmlPage) page.getFrameByName("hcti")
				.getEnclosedPage();
		return main;
	}

	private static String getElementAsTextByXPath(DomElement element,
			String xPath) {
		List<?> result = element.getByXPath(xPath);
		if (result.size() == 0) {
			throw new RuntimeException("XPath does not match");
		} else if (result.size() > 1) {
			throw new RuntimeException("Found more than one match for xpath: "
					+ result);
		}
		return ((DomNode) result.get(0)).asText();
	}

	private static String getElementAsTextByXPath(HtmlPage page, String xPath) {
		return getElementAsTextByXPath(page.getDocumentElement(), xPath);
	}

	private static void printWindow() {
		WebWindow window = webClient.getWebWindows().get(0);
		HtmlPage page = (HtmlPage) window.getEnclosedPage();
		if (page.getFrames().size() > 0) {
			printFrames(page.getFrames());
		} else {
			printPage(page);
		}
	}

	/**
	 * @param page
	 */
	private static void printPage(HtmlPage page) {
		System.out.println("page: " + page.asText());
	}

	private static void printFrames(List<FrameWindow> window) {
		if (window.size() == 0) {
			System.out.println("no frames");
		}
		int i = 0;
		for (FrameWindow frameWindow : window) {
			HtmlPage page = (HtmlPage) frameWindow.getEnclosedPage();
			System.out.println("frame " + i + ": " + page.asText());
			i++;
		}
	}
}
