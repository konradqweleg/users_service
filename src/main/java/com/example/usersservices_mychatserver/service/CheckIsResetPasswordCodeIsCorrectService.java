package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.entity.IdUserData;
import com.example.usersservices_mychatserver.entity.Result;
import com.example.usersservices_mychatserver.entity.Status;
import com.example.usersservices_mychatserver.entity.UserEmailAndCode;
import com.example.usersservices_mychatserver.port.in.CheckIsCorrectResetPasswordCodeUseCase;
import com.example.usersservices_mychatserver.port.out.persistence.ResetPasswordCodeRepositoryPort;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class CheckIsResetPasswordCodeIsCorrectService implements CheckIsCorrectResetPasswordCodeUseCase {
    private final UserRepositoryPort postgreUserRepository;
    private final ResetPasswordCodeRepositoryPort resetPasswordCodeRepository;

    public CheckIsResetPasswordCodeIsCorrectService(UserRepositoryPort postgreUserRepository, ResetPasswordCodeRepositoryPort resetPasswordCodeRepository) {
        this.postgreUserRepository = postgreUserRepository;
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
    }

    @Override
    public Mono<Result<Status>> checkIsCorrectResetPasswordCode(Mono<UserEmailAndCode> emailAndCodeMono) {
    return     emailAndCodeMono.flatMap(emailAndCode-> postgreUserRepository.findUserWithEmail(emailAndCode.email()).
            flatMap(userFromDb-> resetPasswordCodeRepository.findResetPasswordCodeForUser(new IdUserData(userFromDb.id())).flatMap(codeFromDb->{
               if(codeFromDb.code().equals(emailAndCode.code())){
                   return Mono.just(Result.<Status>success(new Status(true)));
               }else{
                   return Mono.just(Result.<Status>error("Bad code"));
               }
           }).switchIfEmpty(Mono.just(Result.<Status>error("User not found"))))).switchIfEmpty(Mono.just(Result.<Status>error("User not found")));

    }
}
