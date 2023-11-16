package com.example.usersservices_mychatserver.integration.dbUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

@Service
public class DatabaseActionUtilService {
    @Autowired
    private DatabaseClient databaseClient;

    public void clearAllUsersInDatabase() {
        databaseClient.sql("DELETE FROM users_services_scheme.user_my_chat where 1=1").
                fetch().
                rowsUpdated()
                .block();
    }


    public void clearAllVerificationCodesInDatabase() {
        databaseClient.sql("DELETE FROM users_services_scheme.code_verification where 1=1").
                fetch().
                rowsUpdated()
                .block();
    }

    public void clearAllDataInDatabase() {
        clearAllUsersInDatabase();
        clearAllVerificationCodesInDatabase();
    }
}
