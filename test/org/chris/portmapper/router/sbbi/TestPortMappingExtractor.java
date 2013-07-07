package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.ActionResponse;
import net.sbbi.upnp.messages.UPNPResponseException;

import org.apache.commons.logging.Log;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.router.RouterException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PortMappingExtractor}.
 * 
 * @author chris
 */
public class TestPortMappingExtractor {

	@Mock
	private InternetGatewayDevice routerMock;
	@Mock
	private Log loggerMock;
	private PortMappingExtractor portMappingExtractor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		portMappingExtractor = new PortMappingExtractor(routerMock, 5,
				loggerMock);
	}

	@Test
	public void allMappingsNull() throws RouterException, IOException,
			UPNPResponseException {
		simulateUPNPException(5, 713);
		assertEquals(0, portMappingExtractor.getPortMappings().size());
		verify(loggerMock, times(1)).warn(anyString());
		verify(loggerMock, never()).error(anyString());
		assertNumMappingsFound(0, 5);
	}

	@Test
	public void allMappingsNullMaxNumReached() throws RouterException {
		assertEquals(0, portMappingExtractor.getPortMappings().size());
		verify(loggerMock, times(1)).warn(anyString());
		verify(loggerMock, never()).error(anyString());
		assertNumMappingsFound(0, 5);
	}

	@Test
	public void noMapping() throws RouterException, IOException,
			UPNPResponseException {
		simulateUPNPException(0, 713);
		assertEquals(0, portMappingExtractor.getPortMappings().size());
		assertNoWarningOrErrorLogged();
		assertNumMappingsFound(0, 0);
	}

	@Test
	public void wrongErrorCode() throws RouterException, IOException,
			UPNPResponseException {
		simulateUPNPException(0, 42);
		assertEquals(0, portMappingExtractor.getPortMappings().size());
		verify(loggerMock, never()).warn(anyString());
		verify(loggerMock, never()).error(anyString());
		verify(loggerMock, never()).warn(anyString(), any(Throwable.class));
		verify(loggerMock).error(anyString(), any(Throwable.class));
		assertNumMappingsFound(0, 0);
	}

	@Test
	public void oneMapping() throws RouterException, IOException,
			UPNPResponseException {
		simulateMapping(0);
		simulateUPNPException(1, 713);
		assertEquals(1, portMappingExtractor.getPortMappings().size());
		assertNoWarningOrErrorLogged();
		assertNumMappingsFound(1, 0);
	}

	private void assertNumMappingsFound(final int numFound, final int numNull) {
		verify(loggerMock).info(
				"Found " + numFound + " mappings, " + numNull
						+ " mappings returned as null.");
	}

	private void assertNoWarningOrErrorLogged() {
		verify(loggerMock, never()).warn(anyString());
		verify(loggerMock, never()).error(anyString());
		verify(loggerMock, never()).warn(anyString(), any(Throwable.class));
		verify(loggerMock, never()).error(anyString(), any(Throwable.class));
	}

	private void simulateMapping(final int mappingEntry) throws IOException,
			UPNPResponseException {
		final ActionResponse response = mock(ActionResponse.class);
		when(response.getOutActionArgumentNames()).thenReturn(
				new HashSet<Object>(Arrays.asList(
						PortMapping.MAPPING_ENTRY_ENABLED,
						PortMapping.MAPPING_ENTRY_EXTERNAL_PORT,
						PortMapping.MAPPING_ENTRY_INTERNAL_CLIENT,
						PortMapping.MAPPING_ENTRY_INTERNAL_PORT,
						PortMapping.MAPPING_ENTRY_LEASE_DURATION,
						PortMapping.MAPPING_ENTRY_PORT_MAPPING_DESCRIPTION,
						PortMapping.MAPPING_ENTRY_PROTOCOL,
						PortMapping.MAPPING_ENTRY_REMOTE_HOST)));
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_ENABLED))
				.thenReturn("1");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_EXTERNAL_PORT))
				.thenReturn("2");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_INTERNAL_CLIENT))
				.thenReturn("internal");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_INTERNAL_PORT))
				.thenReturn("3");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_LEASE_DURATION))
				.thenReturn("4");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_PORT_MAPPING_DESCRIPTION))
				.thenReturn("description");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_PROTOCOL))
				.thenReturn("TCP");
		when(
				response.getOutActionArgumentValue(PortMapping.MAPPING_ENTRY_REMOTE_HOST))
				.thenReturn("remote");
		when(routerMock.getGenericPortMappingEntry(mappingEntry)).thenReturn(
				response);
	}

	private void simulateUPNPException(final int mappingEntry,
			final int errorCode) throws IOException, UPNPResponseException {
		when(routerMock.getGenericPortMappingEntry(mappingEntry)).thenThrow(
				new UPNPResponseException(errorCode, "exception for entry "
						+ mappingEntry + ", error code " + errorCode));
	}

}
