/**
 *
 */
package org.chris.portmapper.model;

/**
 * This {@link Enum} represents the protocol of a {@link SinglePortMapping}, possible values are {@link #TCP} and
 * {@link #UDP}.
 *
 * @author chris
 */
public enum Protocol {

    TCP("TCP"), UDP("UDP");

    private final String name;

    private Protocol(final String name) {
        this.name = name;
    }

    public static Protocol getProtocol(final String name) {
        if (name != null && name.equalsIgnoreCase("TCP")) {
            return TCP;
        }
        if (name != null && name.equalsIgnoreCase("UDP")) {
            return UDP;
        }
        throw new IllegalArgumentException("Invalid protocol name '" + name + "'");
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }
}