package com.gamesUP.gamesUP.services;

import java.util.List;

import com.gamesUP.gamesUP.dto.UserResponseDTO;
import com.gamesUP.gamesUP.entities.Game;
import com.gamesUP.gamesUP.entities.User;

public interface IUserService {

    List<UserResponseDTO> getAllUsers();

    User getUserById(Long userId);

    User getUserByEmail(String email);

    List<Game> getWishlist(Long userId);

    void addGameToWishlist(Long userId, Long gameId);

    void removeGameFromWishlist(Long userId, Long gameId);
}
