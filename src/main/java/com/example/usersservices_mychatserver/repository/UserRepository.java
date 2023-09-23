package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.entity.UserMyChat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<UserMyChat, Long> {
}