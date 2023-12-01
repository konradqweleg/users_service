package com.example.usersservices_mychatserver.integration.integration.request_util;

import java.net.URI;
import java.net.URISyntaxException;

public class RequestUtil {
    private int serverPort;
    private static String prefixHttp = "http://localhost:";
    private static String prefixServicesApiV1 = "/userServices/api/v1/user";

    public RequestUtil(int serverPort) {
        this.serverPort = serverPort;
    }

    public URI createRequestLogin() throws URISyntaxException {
        return new URI(prefixHttp + serverPort + prefixServicesApiV1 + "/login");
    }

    public URI createRequestRegister() throws URISyntaxException {
        return new URI(prefixHttp + serverPort + prefixServicesApiV1 + "/register");
    }

    public URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI(prefixHttp + serverPort + prefixServicesApiV1 + "/activeUserAccount");
    }

    public URI createRequestResendActiveUserAccountCode() throws URISyntaxException {
        return new URI(prefixHttp + serverPort + prefixServicesApiV1 + "/resendActiveUserAccountCode");
    }

    public URI createRequestSendResetPasswordCode() throws URISyntaxException {
        return new URI(prefixHttp + serverPort + prefixServicesApiV1 + "/sendResetPasswordCode");
    }

    public URI createRequestResetPassword() throws URISyntaxException {
        return new URI(prefixHttp + serverPort + prefixServicesApiV1 + "/resetPassword");
    }

}
