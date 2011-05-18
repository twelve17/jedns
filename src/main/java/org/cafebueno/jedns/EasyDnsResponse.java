package org.cafebueno.jedns;

import java.util.HashMap;
import java.util.Map;

public enum EasyDnsResponse {

    ERROR_AUTH("error-auth", "Invalid username or password, or invalid IP syntax"),

    ERROR_AUTH_SUSPEND("error-auth-suspend", "User has had his / her account suspended.  This is if I get complaints about them or if they misuse the service."),

    ERROR_AUTH_VOIDED("error-auth-voided", "User has had his / her account revoked.  Same thing as suspended but this is permanent. So far no one has been voided."),

    ERROR_RECORD_INVALID("error-record-invalid", "Record does not exist in the system. / Unable to update record in system database."),

    ERROR_RECORD_AUTH("error-record-auth", "User does not have access to this record."),

    ERROR_RECORD_IP_SAME("error-record-ip-same", "IP never changed so nothing was done."),

    ERROR_SYSTEM("error-system", "General system error which is caught and recognized by the system."),

    ERROR("error", "General system error unrecognized by the system."),

    // I made this one up as a placeholder for when we don't get a response for some reason
    NONE("_error-no-response_", "No response received from EasyDNS service."),

    SUCCESS("success", "  The one and only good message. :)");

    private static Map<String, EasyDnsResponse> byCode = new HashMap<String, EasyDnsResponse>();
    static {
        for (EasyDnsResponse e : values()) {
            byCode.put(e.getCode(), e);
        }
    }

    private String code;
    private String description;

    private EasyDnsResponse(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isErrorResponse() {
        return getCode().startsWith("error");
    }

    public boolean isIpSameError() {
        return equals(ERROR_RECORD_IP_SAME);
    }

    public static EasyDnsResponse getByCode(String code) {
        return byCode.get(code);
    }
}