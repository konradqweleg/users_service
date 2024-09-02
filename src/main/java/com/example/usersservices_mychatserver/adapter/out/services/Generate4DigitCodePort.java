package com.example.usersservices_mychatserver.adapter.out.services;

import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class Generate4DigitCodePort implements GenerateRandomCodePort {
    private static final Random random = new SecureRandom();
    @Override
    public String generateCode() {
        return String.format("%04d", random.nextInt(10000));
    }
}
