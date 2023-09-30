package com.example.usersservices_mychatserver.adapter.out.logic;

import com.example.usersservices_mychatserver.port.out.logic.HashPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptHashPassword implements HashPassword {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public String cryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }


}
