package com.example.usersservices_mychatserver.integration.integration.request_util;

import java.net.URI;
import java.net.URISyntaxException;

public class RequestUtil {
    private int serverPort;
    public RequestUtil(int serverPort) {
        this.serverPort = serverPort;
    }

    public URI createRequestLogin() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/userServices/api/v1/user/login");
    }

    public URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/userServices/api/v1/user/register");
    }

    public URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/userServices/api/v1/user/activeUserAccount");
    }

    public URI createRequestResendActiveUserAccountCode() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/userServices/api/v1/user/resendActiveUserAccountCode");
    }

    public URI createRequestSendResetPasswordCode() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/userServices/api/v1/user/sendResetPasswordCode");
    }

    public URI createRequestResetPassword() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/userServices/api/v1/user/resetPassword");
    }

}
