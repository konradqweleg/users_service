package com.example.usersservices_mychatserver.adapter.out.persistence;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.exception.SaveDataInRepositoryException;
import com.example.usersservices_mychatserver.model.CodeVerification;
import com.example.usersservices_mychatserver.model.ResetPasswordCode;
import com.example.usersservices_mychatserver.model.UserMyChat;
import com.example.usersservices_mychatserver.port.out.persistence.UserRepositoryPort;
import com.example.usersservices_mychatserver.repository.CodeVerificationRepository;
import com.example.usersservices_mychatserver.repository.ResetPasswordCodeRepository;
import com.example.usersservices_mychatserver.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PostgresUserRepository implements UserRepositoryPort {
    private final Logger log = LogManager.getLogger(PostgresUserRepository.class);
    private final UserRepository userRepository;
    private final CodeVerificationRepository codeVerificationRepository;
    private final ResetPasswordCodeRepository resetPasswordCodeRepository;

    public PostgresUserRepository(UserRepository userRepository, CodeVerificationRepository codeVerificationRepository, ResetPasswordCodeRepository resetPasswordCodeRepository) {
        this.userRepository = userRepository;
        this.codeVerificationRepository = codeVerificationRepository;
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
    }

    @Override
    public Mono<CodeVerification> findActiveUserAccountCodeForUserWithId(Long idUser) {
        return codeVerificationRepository.findByIdUser(idUser)
                .doOnError(throwable -> log.error("Error while finding active account code for user with id: {}", idUser, throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while finding active account code for user with id: " + idUser, throwable)));
    }

    @Override
    public Mono<CodeVerification> saveVerificationCode(CodeVerification code) {
        return codeVerificationRepository.save(code)
                .doOnError(throwable -> log.error("Error while saving code for user with id: {}", code.idUser(), throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while saving code for user with id: " + code.idUser(), throwable)));
    }

    @Override
    public Mono<Void> deleteUserActiveAccountCode(Long idUser) {
        return codeVerificationRepository.deleteByIdUser(idUser)
                .doOnError(throwable -> log.error("Error while deleting code for user with id: {}", idUser, throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while deleting code for user with id: " + idUser, throwable)));
    }

    @Override
    public Mono<Void> deleteUserActiveAccountCode(CodeVerification codeVerification) {
        return codeVerificationRepository.delete(codeVerification)
                .doOnError(throwable -> log.error("Error while deleting code for user with id: {}", codeVerification.idUser(), throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while deleting code for user with id: " + codeVerification.idUser(), throwable)));
    }

    @Override
    public Mono<UserMyChat> saveUser(UserMyChat userMyChat) {
        return userRepository.save(userMyChat)
                .doOnError(throwable -> log.error("Error while saving user with email: {}", userMyChat.email(), throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while saving user with email: " + userMyChat.email(), throwable)));
    }

    @Override
    public Mono<UserMyChat> findUserWithEmail(String email) {
        return userRepository.findByEmail(email)
                .doOnError(throwable -> log.error("Error while finding user with email: {}", email, throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while finding user with email: " + email, throwable)));
    }

    @Override
    public Mono<UserMyChat> findUserById(Long idUser) {
        return userRepository.findById(idUser)
                .doOnError(throwable -> log.error("Error while finding user with id: {}", idUser, throwable))
                .onErrorResume(throwable -> Mono.error(new SaveDataInRepositoryException("Error while finding user with id: " + idUser, throwable)));
    }

    @Override
    public Flux<UserMyChat> findAllUsers() {
        return userRepository.findAll()
                .doOnError(throwable -> log.error("Error while finding all users", throwable))
                .onErrorResume(throwable -> Flux.error(new SaveDataInRepositoryException("Error while finding all users", throwable)));
    }

    @Override
    public Flux<UserMyChat> findUserMatchingNameOrSurname(String patternName, String patternSurname) {
        return userRepository.findUsersMatchingNameOrSurname(patternName, patternSurname)
                .doOnError(throwable -> log.error("Error while finding user with name: {} or surname: {}", patternName, patternSurname, throwable))
                .onErrorResume(throwable -> Flux.error(new SaveDataInRepositoryException("Error while finding user with name: " + patternName + " or surname: " + patternSurname, throwable)));
    }

    @Override
    public Flux<UserMyChat> findUserMatchingNameAndSurname(String patternName, String patternSurname) {
        return userRepository.findUsersMatchingNameAndSurname(patternName, patternSurname)
                .doOnError(throwable -> log.error("Error while finding user with name: {} and surname: {}", patternName, patternSurname, throwable))
                .onErrorResume(throwable -> Flux.error(new SaveDataInRepositoryException("Error while finding user with name: " + patternName + " and surname: " + patternSurname, throwable)));
    }

    @Override
    public Mono<Void> insertResetPasswordCode(ResetPasswordCode resetPasswordCode) {
        return resetPasswordCodeRepository.save(resetPasswordCode)
                .then()
                .doOnError(ex -> log.error("Error while saving reset password code for user with ID: {}", resetPasswordCode.idUser(), ex))
                .onErrorResume(ex -> Mono.error(new SaveDataInRepositoryException("Error while saving reset password code", ex)));
    }

    @Override
    public Mono<Void> deleteResetPasswordCodeForUser(IdUserData idUser) {
        return resetPasswordCodeRepository.deleteByIdUser(idUser.idUser())
                .doOnError(ex -> log.error("Error while deleting reset password code for user with ID: {}", idUser.idUser(), ex))
                .onErrorResume(ex -> Mono.error(new SaveDataInRepositoryException("Error while deleting reset password code", ex)));
    }

    @Override
    public Mono<ResetPasswordCode> findResetPasswordCodeForUserById(IdUserData idUser) {
        return resetPasswordCodeRepository.findResetPasswordCodeByIdUser(idUser.idUser())
                .doOnError(ex -> log.error("Error while finding reset password code for user with ID: {}", idUser.idUser(), ex))
                .onErrorResume(ex -> Mono.error(new SaveDataInRepositoryException("Error while finding reset password code", ex)));
    }
}