package com.example.usersservices_mychatserver.adapter.out.logic;

import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCode;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class Generate4DigitCode implements GenerateRandomCode {
    private static final Random random = new Random();
    @Override
    public String generateCode() {
        String generatedCode = String.format("%04d", random.nextInt(10000));
        return generatedCode;
    }
}
