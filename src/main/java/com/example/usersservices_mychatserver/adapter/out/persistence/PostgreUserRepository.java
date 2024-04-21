package com.example.usersservices_mychatserver.adapter.out.persistence;


import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.repository.CodeVerificationRepository;
import com.example.usersservices_mychatserver.repository.ResetPasswordCodeRepository;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
public class PostgreUserRepository implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final CodeVerificationRepository codeVerificationRepository;
    private final ResetPasswordCodeRepository resetPasswordCodeRepository;

    public PostgreUserRepository(UserRepository userRepository, CodeVerificationRepository codeVerificationRepository, ResetPasswordCodeRepository resetPasswordCodeRepository) {
        this.userRepository = userRepository;
        this.codeVerificationRepository = codeVerificationRepository;
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
    }

    @Override
    public Mono<CodeVerification> findActiveUserAccountCodeForUserWithId(Long idUser) {
        return codeVerificationRepository.findByIdUser(idUser);
    }

    @Override
    public Mono<CodeVerification> saveVerificationCode(CodeVerification code) {
        return codeVerificationRepository.save(code);
    }

    @Override
    public Mono<Void> deleteUserActiveAccountCode(Long idUser) {
        return codeVerificationRepository.deleteByIdUser(idUser);
    }


    @Override
    public Mono<Void> deleteUserActiveAccountCode(CodeVerification codeVerification1) {
        return codeVerificationRepository.delete(codeVerification1);
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

    @Override
    public Mono<Void> changePassword(Long idUser, String newPassword) {
        return userRepository.changePassword(idUser, newPassword);
    }

    @Override
    public Flux<UserMyChat> findAllUsers() {
      return userRepository.findAll();
    }

    @Override
    public Flux<UserMyChat> findUserMatchingNameOrSurname(String patternName, String patternSurname) {
        return userRepository.findUsersMatchingNameOrSurname(patternName,patternSurname);
    }

    @Override
    public Flux<UserMyChat> findUserMatchingNameAndSurname(String patternName, String patternSurname) {
        return userRepository.findUsersMatchingNameAndSurname(patternName,patternSurname);
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
    public Mono<ResetPasswordCode> findResetPasswordCodeForUserById(IdUserData idUser) {
        return resetPasswordCodeRepository.findResetPasswordCodeByIdUser(idUser.idUser());
    }


}
