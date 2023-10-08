package com.example.usersservices_mychatserver.port.out.queue;

public interface SendEmailWithResetPasswordCodePort {
    void sendResetPasswordCode(String email, String code);
}
