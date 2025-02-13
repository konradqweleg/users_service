package com.example.usersservices_mychatserver.adapter.in.rest;

import com.example.usersservices_mychatserver.adapter.in.rest.util.ConvertToJSON;
import com.example.usersservices_mychatserver.adapter.in.rest.util.ResponseUtil;
import com.example.usersservices_mychatserver.entity.request.IdUserData;
import com.example.usersservices_mychatserver.entity.request.UserEmailDataDTO;
import com.example.usersservices_mychatserver.entity.response.UserData;
import com.example.usersservices_mychatserver.port.in.UserPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserInformationController {
    private final UserPort userPort;

    public UserInformationController(UserPort userPort) {
        this.userPort = userPort;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserData>> getUserAboutId(@PathVariable Long id) {
        IdUserData idUserData = new IdUserData(id);
        return ResponseUtil.toResponseEntity(userPort.getUserAboutId(idUserData), HttpStatus.OK);
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<List<UserData>>> getUserMatchingNameAndSurname(@RequestParam String patternName) {
        return ResponseUtil.toResponseEntity(userPort.getUserMatchingNameAndSurname(patternName), HttpStatus.OK);
    }

    @GetMapping
    public Mono<ResponseEntity<String>> getAllUsers() {
        return ConvertToJSON.convert(userPort.getAllUsers());
    }

    @GetMapping("/email")
    public Mono<ResponseEntity<UserData>> getUserAboutEmail(@RequestParam String email) {
        UserEmailDataDTO userEmailData = new UserEmailDataDTO(email);
        return ResponseUtil.toResponseEntity(userPort.getUserAboutEmail(userEmailData), HttpStatus.OK);
    }

    @GetMapping("/existence")
    public Mono<ResponseEntity<Boolean>> checkIsUserWithThisEmailExist(@RequestBody @Valid UserEmailDataDTO userEmail) {
        return ResponseUtil.toResponseEntity(userPort.checkIsUserWithThisEmailExist(userEmail), HttpStatus.OK);
    }
}
