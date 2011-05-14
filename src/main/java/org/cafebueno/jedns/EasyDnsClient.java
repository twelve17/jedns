package org.cafebueno.jedns;

import org.cafebueno.jedns.fetcher.IpAddressFetcher;
import org.cafebueno.jedns.fetcher.IpAdressFetcherException;

import org.cafebueno.jedns.fetcher.WRTRouterIpAddressFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class EasyDnsClient {

    // https://www.dnsmadeeasy.com/servlet/updateip?username=test@example.com&password=mypass&id=1007,1008,1009&ip=12.13.14.15
    private String domainManagementUrl = "https://www.dnsmadeeasy.com/servlet/updateip";

    private IpAddressFetcher fetcher;
    private String characterEncoding = "UTF-8";

    public EasyDnsClient() {
        fetcher = new WRTRouterIpAddressFetcher();
    }

    public EasyDnsResponse updateRecord(String username, String password, String[] recordIds) throws IpAdressFetcherException, URISyntaxException {
        String ipAddress = fetchIpAddress();
        return postRequest(username, password, ipAddress, recordIds);
    }

    protected EasyDnsResponse postRequest(String username, String password, String ipAddress, String[] recordIds) throws IpAdressFetcherException, URISyntaxException {

        try {
            HttpClient httpclient = new DefaultHttpClient();

            StringBuffer recordIdParam = new StringBuffer();
            for (int i = 0; i < recordIds.length; i++) {
                String id = recordIds[i];
                recordIdParam.append(id);
                if (i < recordIds.length - 1) {
                    recordIdParam.append(",");
                }
            }

            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("username", username));
            qparams.add(new BasicNameValuePair("password", password));
            qparams.add(new BasicNameValuePair("ip", ipAddress));
            qparams.add(new BasicNameValuePair("id", recordIdParam.toString()));

            URI source = new URI(domainManagementUrl);
            URI uri = URIUtils.createURI(source.getScheme(), source.getHost(), -1, source.getPath(), URLEncodedUtils.format(qparams, characterEncoding), null);

            HttpPost httppost = new HttpPost(uri);

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity == null) {
                return null;
            }
            else {
                InputStream instream = entity.getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(instream, writer, characterEncoding);
                String responseOutput = writer.toString();
                return new ResponseParser().parseResponse(responseOutput);
            }
        }
        catch (ClientProtocolException e) {
            throw new IpAdressFetcherException("Unable to fetch IP address", e);
        }
        catch (IOException e) {
            throw new IpAdressFetcherException("Unable to fetch IP address", e);
        }
    }

    public static class ResponseParser {
        private static final Pattern EASY_DNS_RESPONSE_OUTPUT = Pattern.compile("\\A\\w+\\s+(.+)([\n\r]+)\\Z");

        public EasyDnsResponse parseResponse(String response) {
            Matcher m = EASY_DNS_RESPONSE_OUTPUT.matcher(response);
            if (m.matches()) {
                return EasyDnsResponse.getByCode(m.group(1));
            }
            else {
                return EasyDnsResponse.NONE;
            }
        }
    }

    protected String fetchIpAddress() throws IpAdressFetcherException {
        return fetcher.fetchIpAddress();
    }
}
