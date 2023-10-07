package com.example.usersservices_mychatserver.port.in;

import com.example.usersservices_mychatserver.entity.ActiveUserAccountDataResponse;
import com.example.usersservices_mychatserver.entity.IdUserData;
import com.example.usersservices_mychatserver.entity.ResendUserActiveAccountCodeDataResponse;
import com.example.usersservices_mychatserver.entity.Result;
import reactor.core.publisher.Mono;

public interface ResendActiveUserAccountCodeUseCase {
    Mono<Result<ResendUserActiveAccountCodeDataResponse>> resendActiveUserAccountCode(Mono<IdUserData> user);
}
