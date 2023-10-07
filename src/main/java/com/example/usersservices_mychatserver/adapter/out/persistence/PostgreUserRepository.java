package com.example.usersservices_mychatserver.adapter.out.persistence;


import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PostgreUserRepository implements UserRepositoryPort {

    private final UserRepository userRepository;


    public PostgreUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public Mono<UserMyChat> saveUser(UserMyChat user) {
        return userRepository.save(user);
    }



    @Override
    public Mono<UserMyChat> findUserWithEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Mono<UserMyChat> activeUserAccount(Long idUser) {
        return  userRepository.activeUserAccount(idUser);
    }

    @Override
    public Mono<UserMyChat> findUserById(Long idUser) {
        return userRepository.findById(idUser);
    }


}
