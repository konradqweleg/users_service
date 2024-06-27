package com.example.usersservices_mychatserver.entity.response;

public record UserAccessData(String accessToken, String refreshToken,String sessionState) {
}
