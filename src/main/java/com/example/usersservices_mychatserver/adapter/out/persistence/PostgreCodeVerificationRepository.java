package com.example.usersservices_mychatserver.adapter.out.persistence;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.port.out.persistence.CodeVerificationRepositoryPort;
import com.example.usersservices_mychatserver.repository.CodeVerificationRepository;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class PostgreCodeVerificationRepository implements CodeVerificationRepositoryPort {
    private final CodeVerificationRepository codeVerificationRepository;
    private final UserRepository userRepository;

    public PostgreCodeVerificationRepository(CodeVerificationRepository codeVerificationRepository, UserRepository userRepository) {
        this.codeVerificationRepository = codeVerificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<CodeVerification> findUserActiveAccountCodeById(Long idUser) {
        return codeVerificationRepository.findByIdUser(idUser);
    }

    @Override
    public Mono<CodeVerification> saveVerificationCode(CodeVerification code) {
        return codeVerificationRepository.save(code);
    }

    @Override
    public Mono<Void> deleteUserActivationCode(Long idUser) {
        return codeVerificationRepository.deleteByIdUser(idUser);
    }


    @Override
    public Mono<Void> deleteUserActivationCode(CodeVerification codeVerification1) {
        return codeVerificationRepository.delete(codeVerification1);
    }
}
