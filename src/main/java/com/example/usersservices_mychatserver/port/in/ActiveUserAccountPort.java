package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.request.ActiveAccountCodeData;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.response.Result;
import com.example.usersservices_mychatserver.entity.response.Status;
import com.example.usersservices_mychatserver.model.CodeVerification;
import reactor.core.publisher.Mono;

public interface ActiveUserAccountPort {
    Mono<Result<Status>> activateUserAccount(Mono<ActiveAccountCodeData> codeVerificationMono);
    Mono<Result<Status>> resendActiveUserAccountCode(Mono<IdUserData> user);

}
