package com.example.usersservices_mychatserver.adapter.out.queue;

import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithVerificationCodePort;
import org.springframework.stereotype.Service;

@Service
public class SendEmailWithVerificationCodeOnConsole implements SendEmailWithVerificationCodePort {
    @Override
    public void sendVerificationCode(UserMyChat user, String code) {
        System.out.println("Send code for user "+user.name()+" code value = "+code);
    }
}
