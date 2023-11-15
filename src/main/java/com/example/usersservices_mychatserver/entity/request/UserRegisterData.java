package com.example.usersservices_mychatserver.entity.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserRegisterData (@NotNull @Size(min = 2, message = "Name should have at least 2 characters")  String name,

                                @Size(min = 2, message = "Surname should have at least 2 characters")   @NotNull String surname,

                                @Size(min = 6, message = "Email should have at least 6 characters")    @NotNull @Email   String email,

                                @Size(min = 6, message = "Password should have at least 6 characters")   @NotNull  String password

){

}
