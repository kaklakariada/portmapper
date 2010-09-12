/**
 * 
 */
package org.chris.portmapper.model;

import org.jdesktop.application.Application;

/**
 * This class represents the refresh rate of a {@link PortMappingPreset}, i.e.
 * the time delay after which it is automatically applied after connecting to a
 * router.
 * 
 * @author chris
 */
public enum RefreshRate {

	DEACTIVATED(0, "preset.refresh_rate.deactivated"),

	FIVE_SECONDS(5, "preset.refresh_rate.5sec"),

	THIRTY_MINUTES(30 * 60, "preset.refresh_rate.30min"),

	ONE_HOUR(60 * 60, "preset.refresh_rate.1hr"),

	TWO_HOURS(2 * 60 * 60, "preset.refresh_rate.2hrs"),

	FIVE_HOURS(5 * 60 * 60, "preset.refresh_rate.5hrs");

	@Override
	public String toString() {
		return Application.getInstance().getContext().getResourceManager()
				.getResourceMap().getString(getMessageCode());
	}

	private final int seconds;
	private final String messageCode;

	private RefreshRate(int seconds, String messageCode) {
		this.seconds = seconds;
		this.messageCode = messageCode;
	}

	/**
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * @return the messageCode
	 */
	public String getMessageCode() {
		return messageCode;
	}
}
