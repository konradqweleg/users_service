package com.example.usersservices_mychatserver.port.out.queue;

import com.example.usersservices_mychatserver.model.UserMyChat;

public interface SendEmailToUserPort {
    void sendResetPasswordCode(String email, String code);
    void sendVerificationCode(UserMyChat user, String code);
}
