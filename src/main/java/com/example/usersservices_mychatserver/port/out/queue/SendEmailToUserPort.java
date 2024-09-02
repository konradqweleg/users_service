package com.example.usersservices_mychatserver.port.out.queue;

public interface SendEmailToUserPort {
    void sendResetPasswordCode(String email, String code);
    void sendVerificationCode(String userEmail, String code);
}
