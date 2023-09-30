package com.example.usersservices_mychatserver.service;

import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.in.RegisterUserUseCase;
import com.example.usersservices_mychatserver.port.out.logic.GenerateRandomCode;
import com.example.usersservices_mychatserver.port.out.logic.HashPassword;
import com.example.usersservices_mychatserver.port.out.persistence.RepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final RepositoryPort postgreRepository;
    private final HashPassword passwordHashService;

    private final GenerateRandomCode generateCode;

    public RegisterUserService(RepositoryPort postgreRepository, HashPassword passwordHashService, GenerateRandomCode generateCode) {
        this.postgreRepository = postgreRepository;
        this.passwordHashService = passwordHashService;
        this.generateCode = generateCode;
    }

    private UserMyChat hashUserPassword(UserMyChat user){
        String userPasswordHashed = passwordHashService.cryptPassword(user.password());
        UserMyChat userWithHashedPassword = new UserMyChat(user.id(), user.name(), user.surname(),user.email(),userPasswordHashed,user.idRole(),user.isActiveAccount());
        return userWithHashedPassword;
    }
    private void generateUserRegisterCode(String email){

        String registerCode = generateCode.generateCode();
        System.out.println(email);
        Mono<UserMyChat> idUser = postgreRepository.findUserWithEmail(email);
        idUser.subscribeOn(Schedulers.immediate()).subscribe(x->{
            System.out.println("id = "+x.id());
            CodeVerification codeVerification = new CodeVerification(null,x.id(),registerCode);
            postgreRepository.saveVerificationCode(codeVerification).subscribeOn(Schedulers.immediate()).subscribe();
        });

    }
    @Override
    public Mono<UserMyChat> registerUser(Mono<UserMyChat> user) {
        user.subscribeOn(Schedulers.boundedElastic()).subscribe(x->{
            UserMyChat userWithHashedPassword = hashUserPassword(x);
            generateUserRegisterCode(x.email());
            Mono<UserMyChat> createdUser= postgreRepository.saveUser(userWithHashedPassword);
            createdUser.subscribeOn(Schedulers.immediate()).subscribe();
        });

       // generateUserRegisterCode(user.email());
        System.out.println("DODANO");
        return user;
    }
}
