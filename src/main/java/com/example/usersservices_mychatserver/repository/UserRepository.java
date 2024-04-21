package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.model.UserMyChat;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserMyChat, Long>  {
    @Query("select * from users_services_scheme.user_my_chat t where t.email = :email")
    Mono<UserMyChat> findByEmail(String email);

    @Query("update users_services_scheme.user_my_chat set is_active_account = true where id = :idUser")
    Mono<UserMyChat> activeUserAccount(Long idUser);

    @Query("update users_services_scheme.user_my_chat set password = :newPassword where id = :idUser")
    Mono<Void> changePassword(Long idUser, String newPassword);

    @Query("select * from users_services_scheme.user_my_chat t where lower(t.name) like lower(concat('%', :patternName, '%')) or lower(t.surname) like lower(concat('%', :patternSurname, '%'))")
    Flux<UserMyChat> findUsersMatchingNameOrSurname(String patternName, String patternSurname);

    @Query("select * from users_services_scheme.user_my_chat t where lower(t.name) like lower(concat('%', :patternName, '%')) and lower(t.surname) like lower(concat('%', :patternSurname, '%'))")
    Flux<UserMyChat> findUsersMatchingNameAndSurname(String patternName, String patternSurname);

}