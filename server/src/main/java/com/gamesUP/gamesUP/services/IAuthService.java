package com.gamesUP.gamesUP.services;

import com.gamesUP.gamesUP.dto.LoginBodyDTO;
import com.gamesUP.gamesUP.dto.RegisterBodyDTO;
import com.gamesUP.gamesUP.entities.User;

public interface IAuthService {

    String register(RegisterBodyDTO payload);

    String login(LoginBodyDTO payload);

    String generateToken(User user);
}
