package com.example.usersservices_mychatserver.adapter.out.queue;

import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.queue.SendEmailToUserPort;
import org.springframework.stereotype.Service;

@Service
public class SendEmailToUserOnConsole implements SendEmailToUserPort {
    @Override
    public void sendResetPasswordCode(String email, String code) {
        System.out.println("Send email with reset password code to " + email + " with code " + code);
    }

    @Override
    public void sendVerificationCode(String userEmail, String code) {
        System.out.println("Send code for user "+userEmail+" code value = "+code);
    }
}
