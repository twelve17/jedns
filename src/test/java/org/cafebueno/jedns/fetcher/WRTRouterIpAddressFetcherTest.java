package org.cafebueno.jedns.fetcher;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import org.junit.Test;

public class WRTRouterIpAddressFetcherTest {

    @Test
    public void testWRTRouterFetch() throws IpAdressFetcherException {
        WRTRouterIpAddressFetcher fetcher = new WRTRouterIpAddressFetcher();
        String ip = fetcher.fetchIpAddress();
        Matcher m = IpAddressFetcher.IP_ADDRESS.matcher(ip);
        assertTrue(m.matches());
    }

}
