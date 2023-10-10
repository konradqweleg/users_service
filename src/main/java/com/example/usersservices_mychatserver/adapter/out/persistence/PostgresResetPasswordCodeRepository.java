package com.example.usersservices_mychatserver.adapter.out.persistence;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.repository.ResetPasswordCodeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PostgresResetPasswordCodeRepository implements ResetPasswordCodeRepositoryPort {
    ResetPasswordCodeRepository resetPasswordCodeRepository;

    public PostgresResetPasswordCodeRepository(ResetPasswordCodeRepository resetPasswordCodeRepository) {
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
    }
    @Override
    public Mono<Void> insertResetPasswordCode(ResetPasswordCode resetPasswordCode) {
       return resetPasswordCodeRepository.save(resetPasswordCode).flatMap(resetPasswordCode1 -> Mono.empty());
    }

    @Override
    public Mono<Void> deleteResetPasswordCodeForUser(IdUserData idUser) {
       return resetPasswordCodeRepository.deleteByIdUser(idUser.idUser());
    }

    @Override
    public Mono<ResetPasswordCode> findResetPasswordCodeForUser(IdUserData idUser) {
        return resetPasswordCodeRepository.findResetPasswordCodeByIdUser(idUser.idUser());
    }
}
