package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.model.UserMyChat;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserMyChat, Long>  {
    Mono<UserMyChat> findByEmail(String email);

    @Query("select * from user_my_chat t where lower(t.name) like lower(concat('%', :patternName, '%')) or lower(t.surname) like lower(concat('%', :patternSurname, '%'))")
    Flux<UserMyChat> findUsersMatchingNameOrSurname(String patternName, String patternSurname);

    @Query("select * from user_my_chat t where lower(t.name) like lower(concat('%', :patternName, '%')) and lower(t.surname) like lower(concat('%', :patternSurname, '%'))")
    Flux<UserMyChat> findUsersMatchingNameAndSurname(String patternName, String patternSurname);

}