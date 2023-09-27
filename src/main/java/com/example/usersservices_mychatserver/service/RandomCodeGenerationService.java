package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.service.interfaces.CodeGenerationService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomCodeGenerationService implements CodeGenerationService {

    private static final Random random = new Random();
    @Override
    public String generateCode() {
        String code = String.format("%04d", random.nextInt(10000));
        return code;
    }
}
