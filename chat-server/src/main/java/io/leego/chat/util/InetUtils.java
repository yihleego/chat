package io.leego.chat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author Leego Yih
 */
public final class InetUtils {
    private static final Logger logger = LoggerFactory.getLogger(InetUtils.class);

    private InetUtils() {
    }

    public static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            int lowest = Integer.MAX_VALUE;
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    logger.trace("Testing interface: {}", ifc.getDisplayName());
                    if (ifc.getIndex() < lowest || result == null) {
                        lowest = ifc.getIndex();
                    } else {
                        continue;
                    }
                    Enumeration<InetAddress> addrs = ifc.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress address = addrs.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            logger.trace("Found non-loopback interface: {}", ifc.getDisplayName());
                            result = address;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.error("Cannot get first non-loopback address", e);
        }
        if (result != null) {
            return result;
        }
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.warn("Unable to retrieve localhost", e);
        }
        return null;
    }
}
