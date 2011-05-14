package org.cafebueno.jedns.fetcher;

import java.util.regex.Pattern;

public interface IpAddressFetcher {
    public Pattern IP_ADDRESS = Pattern.compile("\\A(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\Z");
    
    public String fetchIpAddress() throws IpAdressFetcherException;

}