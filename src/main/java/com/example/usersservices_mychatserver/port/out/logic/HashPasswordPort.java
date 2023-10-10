package com.example.usersservices_mychatserver.port.out.logic;

public interface HashPasswordPort {
    String cryptPassword(String password);

    Boolean checkPassword(String password, String hashedPassword);
}
