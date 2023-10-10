package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import reactor.core.publisher.Mono;

public interface ResendActiveUserAccountCodeUseCase {
    Mono<Result<Status>> resendActiveUserAccountCode(Mono<IdUserData> user);
}
