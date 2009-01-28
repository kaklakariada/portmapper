/**
 * 
 */
package org.chris.portmapper.router.htmlunit.speedport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author chris
 * 
 */
public class SpeedportRouter implements IRouter {

	private final static Log logger = LogFactory.getLog(SpeedportRouter.class);
	private WebClient webClient;

	private SpeedportRouter(WebClient webClient) {
		this.webClient = webClient;
	}

	/**
	 * @param string
	 * @param string2
	 * @return
	 * @throws RouterException
	 */
	public static IRouter connect(String url, String password)
			throws RouterException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_2);
		webClient.setRefreshHandler(new ThreadedRefreshHandler());
		try {
			logger.debug("Getting url " + url);
			HtmlPage page = webClient.getPage(url);

			HtmlForm form = page.getFormByName("tF");
			form.getInputByName("pws").setValueAttribute(password);
			logger.debug("Click login button");
			page = ((ClickableElement) form.getHtmlElementById("t_but4"))
					.click();
			System.out.println(page.asText());
			System.out.println(page.getTitleText());
		} catch (FailingHttpStatusCodeException e) {
			logger.error("", e);
			throw new RouterException("FailingHttpStatusCodeException", e);
		} catch (MalformedURLException e) {
			logger.error("", e);
			throw new RouterException("MalformedURLException", e);
		} catch (IOException e) {
			logger.error("", e);
			throw new RouterException("IOException", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.chris.portmapper.router.IRouter#addPortMapping(org.chris.portmapper
	 * .router.PortMapping)
	 */
	@Override
	public void addPortMapping(PortMapping mapping) throws RouterException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.chris.portmapper.router.IRouter#addPortMappings(java.util.Collection)
	 */
	@Override
	public void addPortMappings(Collection<PortMapping> mappings)
			throws RouterException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#disconnect()
	 */
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#getExternalIPAddress()
	 */
	@Override
	public String getExternalIPAddress() throws RouterException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#getInternalHostName()
	 */
	@Override
	public String getInternalHostName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#getInternalPort()
	 */
	@Override
	public int getInternalPort() throws RouterException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#getName()
	 */
	@Override
	public String getName() throws RouterException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#getPortMappings()
	 */
	@Override
	public Collection<PortMapping> getPortMappings() throws RouterException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#getUpTime()
	 */
	@Override
	public long getUpTime() throws RouterException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouter#logRouterInfo()
	 */
	@Override
	public void logRouterInfo() throws RouterException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.chris.portmapper.router.IRouter#removeMapping(org.chris.portmapper
	 * .router.PortMapping)
	 */
	@Override
	public void removeMapping(PortMapping mapping) throws RouterException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.chris.portmapper.router.IRouter#removePortMapping(org.chris.portmapper
	 * .router.Protocol, java.lang.String, int)
	 */
	@Override
	public void removePortMapping(Protocol protocol, String remoteHost,
			int externalPort) throws RouterException {
		// TODO Auto-generated method stub

	}

}
