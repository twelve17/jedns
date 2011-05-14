package org.cafebueno.jedns;

import static org.junit.Assert.assertEquals;

import org.cafebueno.jedns.EasyDnsClient.ResponseParser;
import org.junit.Test;

public class EasyDnsClientTest {

    @Test
    public void testParseResponse() {
        String responseStr = "7004532 error-record-ip-same\n\r\n";

        ResponseParser parser = new ResponseParser();
        EasyDnsResponse response = parser.parseResponse(responseStr);
        assertEquals(EasyDnsResponse.ERROR_RECORD_IP_SAME, response);
    }
}
