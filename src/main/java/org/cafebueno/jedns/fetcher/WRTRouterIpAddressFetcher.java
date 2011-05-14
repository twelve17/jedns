package org.cafebueno.jedns.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Just another one of these:<br/>
 * <br/>
 * http://www.dnsmadeeasy.com/s0306/res/ddnsc.html
 * 
 * @author alex
 * @author $Author$
 * @version $Revision$
 */
public class WRTRouterIpAddressFetcher implements IpAddressFetcher {

    private String routerAddress = "192.168.1.100";
    private String routerFetchAddressUrl;

    public WRTRouterIpAddressFetcher() {
        buildFetchUrl();
    }

    public WRTRouterIpAddressFetcher(String routerAddress) {
        this.routerAddress = routerAddress;
        buildFetchUrl();
    }

    private void buildFetchUrl() {
        routerFetchAddressUrl = "http://" + routerAddress;
    }

    public String fetchIpAddress() throws IpAdressFetcherException {
        String ip = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(routerFetchAddressUrl);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                WRTRouterIpAddressFetcher.Parser parser = new Parser();
                ip = parser.parseOutput(instream);
            }
        }
        catch (ClientProtocolException e) {
            throw new IpAdressFetcherException("Unable to fetch IP address", e);
        }
        catch (IOException e) {
            throw new IpAdressFetcherException("Unable to fetch IP address", e);
        }

        return ip;
    }

    public static class Parser {

        private static final String START_MATCH = "<span id=\"wan_ipaddr\">";
        private static final String END_MATCH = "</span>";
        private static final Pattern IP_TAG_PATTERN = Pattern.compile(START_MATCH + "(.+)");

        private String characterEncoding = "UTF-8";

        public Parser() {
        }

        public Parser(String characterEncoding) {
            this.characterEncoding = characterEncoding;
        }

        public String parseOutput(InputStream instream) throws IOException {
            StringWriter writer = new StringWriter();
            IOUtils.copy(instream, writer, characterEncoding);
            String theString = writer.toString();
            return parseOutput(theString);
        }

        public String parseOutput(String input) throws IOException {
            String ip = null;

            int startIndex = input.indexOf(START_MATCH);

            String sub1 = input.substring(startIndex, input.length() - 1);
            int endIndex = sub1.indexOf(END_MATCH);

            String sub2 = sub1.substring(0, endIndex);
            Matcher m = IP_TAG_PATTERN.matcher(sub2);
            if (m.matches()) {
                ip = m.group(1);
            }
            return ip;
        }
    }
}