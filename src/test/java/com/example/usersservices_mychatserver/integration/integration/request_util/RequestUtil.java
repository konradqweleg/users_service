package com.example.usersservices_mychatserver.integration.integration.request_util;

import java.net.URI;
import java.net.URISyntaxException;

public class RequestUtil {
    private int serverPort;
    public RequestUtil(int serverPort) {
        this.serverPort = serverPort;
    }

    public URI createRequestLogin() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/login");
    }

    public URI createRequestRegister() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/register");
    }

    public URI createRequestActiveUserAccount() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount");
    }

    public URI createRequestResendActiveUserAccountCode() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/activeAccount/resendCode");
    }

    public URI createRequestSendResetPasswordCode() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/resetPasswordCode/sendCode");
    }

    public URI createRequestResetPassword() throws URISyntaxException {
        return new URI("http://localhost:" + serverPort + "/authentication/changePassword");
    }

}
