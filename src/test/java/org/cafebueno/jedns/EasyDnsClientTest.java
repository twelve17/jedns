package org.cafebueno.jedns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import org.cafebueno.jedns.EasyDnsClient.InitException;
import org.cafebueno.jedns.EasyDnsClient.ResponseParser;
import org.cafebueno.jedns.fetcher.IpAdressFetcherException;
import org.cafebueno.jedns.fetcher.LocalSystemIpAddressFetcher;
import org.junit.Ignore;
import org.junit.Test;

public class EasyDnsClientTest {

    @Test
    public void testParseResponse() {
        String responseStr = "7004532 error-record-ip-same\n\r\n";

        ResponseParser parser = new ResponseParser();
        EasyDnsResponse response = parser.parseResponse(responseStr);
        assertEquals(EasyDnsResponse.ERROR_RECORD_IP_SAME, response);
    }

    @Ignore("this should be an integration test :)")
    @Test
    public void testUpdateIpAddress() throws URISyntaxException, IpAdressFetcherException, InitException {
        String fetcherClass = LocalSystemIpAddressFetcher.class.getName();
        EasyDnsClient client = new EasyDnsClient(fetcherClass, null);
        EasyDnsResponse response = client.updateRecord("someuser", "somepassword", new String[] { "12345" });
        assertTrue("response", !(response.equals(EasyDnsResponse.NONE)));
    }
}
