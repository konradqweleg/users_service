package com.example.usersservices_mychatserver.adapter.out.logic;

import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCodePort;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class Generate4DigitCodePort implements GenerateRandomCodePort {
    private static final Random random = new Random();
    @Override
    public String generateCode() {
        return String.format("%04d", random.nextInt(10000));
    }
}
