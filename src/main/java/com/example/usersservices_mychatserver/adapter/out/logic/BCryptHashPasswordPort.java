package com.example.usersservices_mychatserver.adapter.out.logic;

import com.example.usersservices_mychatserver.port.out.logic.HashPasswordPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptHashPasswordPort implements HashPasswordPort {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public String cryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public Boolean checkPassword(String password, String hashedPassword) {
      return   bCryptPasswordEncoder.matches(password, hashedPassword);
    }


}
