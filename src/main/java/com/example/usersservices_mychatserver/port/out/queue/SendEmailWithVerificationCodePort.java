package com.example.usersservices_mychatserver.port.out.queue;

import com.example.usersservices_mychatserver.model.UserMyChat;

public interface SendEmailWithVerificationCodePort {
    void sendVerificationCode(UserMyChat user, String code);
}
