package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.model.UserMyChat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserMyChat, Long>  {

}