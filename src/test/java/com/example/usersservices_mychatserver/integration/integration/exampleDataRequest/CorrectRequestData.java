package com.example.usersservices_mychatserver.integration.integration.exampleDataRequest;

import com.example.usersservices_mychatserver.entity.request.EmailAndPasswordData;
import com.example.usersservices_mychatserver.entity.request.UserRegisterDataDTO;

public class CorrectRequestData {

    public static  final EmailAndPasswordData USER_LOGIN_DATA = new EmailAndPasswordData("correctMail@format.eu", "password");

    public static final UserRegisterDataDTO USER_REGISTER_DATA = new UserRegisterDataDTO("John", "Walker", "correctMail@format.eu", "password");
    public static final UserRegisterDataDTO USER_REGISTER_DATA_2 = new UserRegisterDataDTO("Joe", "Doe", "mail@mail.eu", "password");
    public static final UserRegisterDataDTO USER_REGISTER_DATA_3 = new UserRegisterDataDTO("Adam", "Rupert", "rupert@mail.ei", "password");
}
