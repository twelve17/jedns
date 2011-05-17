package org.cafebueno.jedns.fetcher;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class LocalSystemIpAddressFetcher implements IpAddressFetcher {

    private static final String PROPERTY_KEY = "LocalSystemIpAddressFetcher.interface.name";
    private static final String DEFAULT_INTERFACE_NAME = "eth0";

    public String fetchIpAddress() throws IpAdressFetcherException {
        String interfaceName = getInterfaceName();

        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                if (ni.getName().equals(interfaceName)) {
                    InetAddress address = null;
                    Enumeration<InetAddress> addresses =  ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress tmp = addresses.nextElement();
                        if (!tmp.isLoopbackAddress() && tmp.getHostAddress().indexOf(":") == -1) {
                            address = tmp;
                            break;
                        }
                    }
                    return address.getHostAddress();
                }
            }

            return null;

        }
        catch (Exception e) {
            throw new IpAdressFetcherException("unable to get IP address", e);
        }
    }

    private String getInterfaceName() {
        String interfaceName;
        String custom = System.getProperty(PROPERTY_KEY);
        if (custom == null) {
            interfaceName = DEFAULT_INTERFACE_NAME;
        }
        else {
            interfaceName = custom;
        }
        return interfaceName;
    }

}
