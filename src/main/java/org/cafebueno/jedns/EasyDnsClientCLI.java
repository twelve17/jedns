package org.cafebueno.jedns;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cafebueno.jedns.EasyDnsClient.InitException;
import org.cafebueno.jedns.fetcher.IpAdressFetcherException;

public class EasyDnsClientCLI {

    public static void main(String[] args) {

        Option domainManagementUrl = new Option("d", "domain-management-url", true, "URL to POST DNS update (default " + EasyDnsClient.DEFAULT_DOMAIN_MANAGEMENT_URL + ")");

        Option fetcherClass = new Option("f", "fetcher-class", true, "fetcher class (default " + EasyDnsClient.DEFAULT_FETCHER_CLASS + ")");

        Option username = new Option("u", "username", true, "easy dns username");
        username.setRequired(true);

        Option password = new Option("p", "password", true, "easy dns password");
        password.setRequired(true);

        Option recordIds = new Option("r", "record-ids", true, "comma separated list of record ids");
        recordIds.setRequired(true);

        Option errorOnIpSame = new Option("e", "error-on-same", false,
                "exit with an error code if we get a 'error-record-ip-same' response (normally this one is treated as an 'ok' response)");
        recordIds.setRequired(false);

        Options options = getOptions(domainManagementUrl, fetcherClass, username, password, recordIds, errorOnIpSame);

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine line = parser.parse(options, args);
            String domainManagementUrlValue = line.getOptionValue(domainManagementUrl.getOpt());
            String fetcherClassValue = line.getOptionValue(fetcherClass.getOpt());
            String recordIdList = line.getOptionValue(recordIds.getOpt());
            String usernameValue = line.getOptionValue(username.getOpt());
            String passwordValue = line.getOptionValue(password.getOpt());
            boolean isErrorOnIpSame = line.hasOption(errorOnIpSame.getOpt());

            EasyDnsClient client = new EasyDnsClient(fetcherClassValue, domainManagementUrlValue);
            EasyDnsResponse response = client.updateRecord(usernameValue, passwordValue, splitRecordIds(recordIdList));

            if (shouldThrowError(response, isErrorOnIpSame)) {
                System.err.println("received error response: " + response.name());
                System.exit(1);
            }
            else {
                if (response.isIpSameError()) {
                    System.out.println("ip address did not need updating");
                }
                else {
                    System.out.println("successfully updated address");
                }
                System.exit(0);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (IpAdressFetcherException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (InitException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean shouldThrowError(EasyDnsResponse response, boolean isErrorOnIpSame) {
        if (isErrorOnIpSame) {
            if (response.isErrorResponse()) {
                return true;
            }
        }
        else {
            if (response.isErrorResponse() && !response.isIpSameError()) {
                return true;
            }
        }
        return false;
    }

    private static Options getOptions(Option... optionItems) {
        Options options = new Options();
        for (Option o : optionItems) {
            options.addOption(o);
        }
        return options;
    }

    private static String[] splitRecordIds(String source) {

        List<String> items = new ArrayList<String>();
        for (String s : source.split(",")) {
            items.add(s.trim());
        }
        return items.toArray(new String[items.size()]);
    }

}
