package com.example.usersservices_mychatserver.port.out.logic;

public interface StoreAdminTokensPort {
    void storeAdminTokens(String accessToken, String refreshToken);
    void storeRefreshToken(String refreshToken);

    void storeAccessToken(String accessToken);
    String getAdminAccessToken();
    String getAdminRefreshToken();
}
