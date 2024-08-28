package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/userServices/api/v1/user")
public class UserController {

    private final UserPort userPort;

    public UserController(UserPort userPort) {
        this.userPort = userPort;
    }

    @PostMapping("/checkIsUserWithThisEmailExist")
    public Mono<ResponseEntity<String>> checkIsUserWithThisEmailExist(@RequestBody @Valid Mono<UserEmailData> user) {
        return userPort.checkIsUserWithThisEmailExist(user).flatMap(ConvertToJSON::convert);
    }

    @PostMapping("/sendResetPasswordCode")
    public Mono<ResponseEntity<String>> sendResetPasswordCode(@RequestBody @Valid Mono<UserEmailData> user) {
        return userPort.sendResetPasswordCode(user).flatMap(ConvertToJSON::convert);
    }
//
    @PostMapping("/checkIsCorrectResetPasswordCode")
    public Mono<ResponseEntity<String>> isCorrectResetPasswordCode(@RequestBody @Valid Mono<UserEmailAndCodeData> userEmailAndCodeMono) {
        return userPort.checkIsCorrectResetPasswordCode(userEmailAndCodeMono).flatMap(ConvertToJSON::convert);
    }
//
//    @PostMapping("/login")
//    public Mono<ResponseEntity<String>> logIn(@RequestBody @Valid Mono<EmailAndPasswordData> user) {
//        return userPort.isCorrectLoginCredentials(user).flatMap(ConvertToJSON::convert);
//    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody @Valid Mono<UserRegisterData> user) {
        return userPort.registerUser(user).flatMap(ConvertToJSON::convert);
    }

    @PostMapping("/resetPassword")
    public Mono<ResponseEntity<String>> changePassword(@RequestBody @Valid Mono<ChangePasswordData> changePasswordDataMono) {
        return userPort.changeUserPassword(changePasswordDataMono).flatMap(ConvertToJSON::convert);
    }

    @PostMapping("/resendActiveUserAccountCode")
    public Mono<ResponseEntity<String>> resendActiveUserAccountCode(@RequestBody @Valid Mono<UserEmailData> emailDataMono) {
        return userPort.resendActiveUserAccountCode(emailDataMono).flatMap(ConvertToJSON::convert);
    }
//
    @PostMapping("/activeUserAccount")
    public Mono<ResponseEntity<String>> activeUserAccount(@RequestBody Mono<ActiveAccountCodeData> codeVerificationMono) {
        return userPort.activateUserAccount(codeVerificationMono).flatMap(ConvertToJSON::convert);
    }

    @GetMapping("/getUserAboutId/{idUserDataMono}")
    public Mono<ResponseEntity<String>> getUserAboutId(@PathVariable Long idUserDataMono) {
        return userPort.getUserAboutId(Mono.just(new IdUserData(idUserDataMono))).flatMap(ConvertToJSON::convert);
    }

    @GetMapping("/getAllUsers")
    public Mono<ResponseEntity<String>> getAllUsers() {
        return ConvertToJSON.convert(userPort.getAllUsers());
    }

    @GetMapping("/getUserAboutEmail")
    public Mono<ResponseEntity<String>> getUserAboutEmail(@RequestParam String email) {
        return userPort.getUserAboutEmail(Mono.just(new UserEmailData(email))).flatMap(ConvertToJSON::convert);
    }

    @GetMapping("/getUserMatchingNameAndSurname")
    public Mono<ResponseEntity<String>> getUserMatchingNameAndSurname(@RequestParam String patternName) {
        return ConvertToJSON.convert(userPort.getUserMatchingNameAndSurname(Mono.just(patternName)));
    }

    @PostMapping("/authorizeUser")
    public Mono<ResponseEntity<String>> authorizeUser(@RequestBody @Valid Mono<UserAuthorizeData> user) {
        return userPort.authorizeUser(user).flatMap(ConvertToJSON::convert);
    }

}
