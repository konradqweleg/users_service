package com.example.usersservices_mychatserver.adapter.out.logic;

import com.example.usersservices_mychatserver.port.out.logic.StoreAdminTokensPort;
import org.springframework.stereotype.Service;

@Service
public class StoreAdminAccessKeycloakAdapter implements StoreAdminTokensPort {

    private String adminAccessToken;
    private String adminRefreshToken;

    @Override
    public void storeAdminTokens(String accessToken, String refreshToken) {
        this.adminAccessToken = accessToken;
        this.adminRefreshToken = refreshToken;
    }
    @Override
    public void storeRefreshToken(String refreshToken) {
        this.adminRefreshToken = refreshToken;
    }
    @Override
    public void storeAccessToken(String accessToken) {
        this.adminAccessToken = accessToken;
    }

    @Override
    public String getAdminAccessToken() {
        return adminAccessToken;
    }

    @Override
    public String getAdminRefreshToken() {
       return adminRefreshToken;
    }
}
