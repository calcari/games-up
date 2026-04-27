package com.gamesUP.gamesUP.services;

import com.gamesUP.gamesUP.dto.UserResponseDTO;
import com.gamesUP.gamesUP.entities.Game;
import com.gamesUP.gamesUP.entities.User;
import com.gamesUP.gamesUP.errors.EntityNotFoundException;
import com.gamesUP.gamesUP.repositories.GameRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponseDTO::fromEntity).toList();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + email + " not found"));
    }

    public List<Game> getWishlist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        return gameRepository.wishlistPopulated(userId);
    }

    public void addGameToWishlist(Long userId, Long gameId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
        var game = gameRepository.findById(gameId).orElseThrow(() -> new EntityNotFoundException("Game", gameId));

        user.getWishList().add(game);
        userRepository.save(user);
    }

    public void removeGameFromWishlist(Long userId, Long gameId) {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));

        user.getWishList().removeIf(g -> g.getId().equals(gameId));
        userRepository.save(user);
    }
}
