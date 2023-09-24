package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.CreateUserUseCase;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateUserService implements CreateUserUseCase {
    private final UserRepository userRepository;

    public CreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserMyChat> createUser(UserMyChat user) {
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        String userPasswordHashed = bc.encode(user.password());
        UserMyChat userWithHashedPassword = new UserMyChat(user.id(), user.name(), user.surname(),user.email(),userPasswordHashed,user.idRole());
        return userRepository.save(userWithHashedPassword);
    }
}
