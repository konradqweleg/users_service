package com.example.usersservices_mychatserver.adapter.out.persistence;


import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.persistence.RepositoryPort;
import com.example.usersservices_mychatserver.repository.CodeVerificationRepository;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PostgreRepository implements RepositoryPort {

    private final UserRepository userRepository;
    private final CodeVerificationRepository codeVerificationRepository;

    public PostgreRepository(UserRepository userRepository, CodeVerificationRepository codeVerificationRepository) {
        this.userRepository = userRepository;
        this.codeVerificationRepository = codeVerificationRepository;
    }

    @Override
    public Mono<UserMyChat> saveUser(UserMyChat user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<CodeVerification> saveVerificationCode(CodeVerification code) {
       return codeVerificationRepository.save(code);
    }

    @Override
    public Mono<UserMyChat> findUserWithEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
