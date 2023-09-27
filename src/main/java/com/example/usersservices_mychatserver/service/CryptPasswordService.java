package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.service.interfaces.PasswordHashService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CryptPasswordService implements PasswordHashService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Override
    public String cryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
