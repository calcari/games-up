package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginBodyDTO {

    @Email
    private String email;

    @NotBlank
    private String password;
}
