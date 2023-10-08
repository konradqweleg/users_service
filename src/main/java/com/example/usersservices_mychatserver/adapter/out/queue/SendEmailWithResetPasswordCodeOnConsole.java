package com.example.usersservices_mychatserver.adapter.out.queue;

import com.example.usersservices_mychatserver.port.out.queue.SendEmailWithResetPasswordCodePort;
import org.springframework.stereotype.Service;

@Service
public class SendEmailWithResetPasswordCodeOnConsole implements SendEmailWithResetPasswordCodePort {
    @Override
    public void sendResetPasswordCode(String email, String code) {
        System.out.println("Send email with reset password code to " + email + " with code " + code);
    }
}
