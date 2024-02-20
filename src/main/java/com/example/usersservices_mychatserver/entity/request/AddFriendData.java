package com.example.usersservices_mychatserver.entity.request;

import jakarta.validation.constraints.NotNull;

public record AddFriendData(@NotNull Long idUserFirst,@NotNull Long idUserSecond) {
}
