package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.entity.request.*;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    private final UserPort userPort;

    public UserController(UserPort userPort) {
        this.userPort = userPort;
    }

//    @GetMapping("/email-existence")
//    public Mono<ResponseEntity<String>> checkIsUserWithThisEmailExist(@RequestBody @Valid Mono<UserEmailDataDTO> user) {
//        return userPort.checkIsUserWithThisEmailExist(user).flatMap(ConvertToJSON::convert);
//    }

//    @PostMapping("/password-reset/code")
//    public Mono<ResponseEntity<String>> sendResetPasswordCode(@RequestBody @Valid Mono<UserEmailDataDTO> user) {
//        return userPort.sendResetPasswordCode(user).flatMap(ConvertToJSON::convert);
//    }

    @GetMapping("/test")
    public Mono<ResponseEntity<String>> test() {
        return Mono.just(ResponseEntity.ok("Test"));
    }

//    @PostMapping("/password-reset/validate-code")
//    public Mono<ResponseEntity<String>> isCorrectResetPasswordCode(@RequestBody @Valid Mono<UserEmailAndCodeDTO> userEmailAndCodeMono) {
//        return userPort.checkIsCorrectResetPasswordCode(userEmailAndCodeMono).flatMap(ConvertToJSON::convert);
//    }
//    @PostMapping("/register")
//    public Mono<ResponseEntity<String>> registerUser(@RequestBody @Valid Mono<UserRegisterDataDTO> user) {
//        return userPort.registerUser(user).flatMap(ConvertToJSON::convert);
//    }

    @PostMapping("/password-reset/change")
    public Mono<ResponseEntity<String>> changePassword(@RequestBody @Valid Mono<ChangePasswordData> changePasswordDataMono) {
        return userPort.changeUserPassword(changePasswordDataMono).flatMap(ConvertToJSON::convert);
    }

//    @PostMapping("/activate/resend-activation-code")
//    public Mono<ResponseEntity<String>> resendActiveUserAccountCode(@RequestBody @Valid Mono<UserEmailData> emailDataMono) {
//        return userPort.resendActiveUserAccountCode(emailDataMono).flatMap(ConvertToJSON::convert);
//    }

//    @PostMapping("/activate")
//    public Mono<ResponseEntity<String>> activeUserAccount(@RequestBody Mono<ActiveAccountCodeData> codeVerificationMono) {
//        return userPort.activateUserAccount(codeVerificationMono).flatMap(ConvertToJSON::convert);
//    }

//    @GetMapping("/{id}")
//    public Mono<ResponseEntity<String>> getUserAboutId(@PathVariable Long id) {
//        return userPort.getUserAboutId(Mono.just(new IdUserData(id))).flatMap(ConvertToJSON::convert);
//    }

    @GetMapping
    public Mono<ResponseEntity<String>> getAllUsers() {
        return ConvertToJSON.convert(userPort.getAllUsers());
    }

//    @GetMapping("/email")
//    public Mono<ResponseEntity<String>> getUserAboutEmail(@RequestParam String email) {
//        return userPort.getUserAboutEmail(Mono.just(new UserEmailDataDTO(email))).flatMap(ConvertToJSON::convert);
//    }

//    @GetMapping("/search")
//    public Mono<ResponseEntity<String>> getUserMatchingNameAndSurname(@RequestParam String patternName) {
//        return ConvertToJSON.convert(userPort.getUserMatchingNameAndSurname(Mono.just(patternName)));
//    }

//    @PostMapping("/login")
//    public Mono<ResponseEntity<String>> authorizeUser(@RequestBody @Valid LoginData user) {
//        return userPort.login(user).flatMap(ConvertToJSON::convert);
//    }

}
