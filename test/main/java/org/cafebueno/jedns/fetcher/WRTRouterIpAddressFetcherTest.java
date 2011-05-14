package org.cafebueno.jedns.fetcher;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import org.cafebueno.jedns.EasyDnsClient;
import org.cafebueno.jedns.EasyDnsResponse;
import org.junit.Test;

public class WRTRouterIpAddressFetcherTest {

    @Test
    public void testWRTRouterFetch() throws IpAdressFetcherException {
        WRTRouterIpAddressFetcher fetcher = new WRTRouterIpAddressFetcher();
        String ip = fetcher.fetchIpAddress();
        Matcher m = IpAddressFetcher.IP_ADDRESS.matcher(ip);
        assertTrue(m.matches());
    }

    @Test
    public void testUpdateIpAddress() throws URISyntaxException, IpAdressFetcherException {
        EasyDnsClient client = new EasyDnsClient();
        EasyDnsResponse response = client.updateRecord("someuser", "somepassword", new String[] { "12345" });
        assertTrue("response", !(response.equals(EasyDnsResponse.NONE)));
    }

}
