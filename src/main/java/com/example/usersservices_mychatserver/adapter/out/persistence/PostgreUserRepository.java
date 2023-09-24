package com.example.usersservices_mychatserver.adapter.out.persistence;


import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.repository.UserRepository;
import reactor.core.publisher.Mono;

public   class PostgreUserRepository implements UserRepositoryPort {

    private final UserRepository userRepository;

    public PostgreUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserMyChat> saveUser(UserMyChat user) {
       return userRepository.save(user);
    }
}
