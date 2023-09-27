package com.example.usersservices_mychatserver.repository;

import com.example.usersservices_mychatserver.model.CodeVerification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CodeVerificationRepository extends ReactiveCrudRepository<CodeVerification, Long> {
}
