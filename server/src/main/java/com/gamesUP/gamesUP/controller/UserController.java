package com.gamesUP.gamesUP.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamesUP.gamesUP.dto.GamePopulatedDTO;
import com.gamesUP.gamesUP.dto.RecommendationItemDTO;
import com.gamesUP.gamesUP.dto.UserResponseDTO;
import com.gamesUP.gamesUP.services.IRecommendationService;
import com.gamesUP.gamesUP.services.IUserService;

import jakarta.annotation.security.RolesAllowed;

@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRecommendationService recommendationService;

    @GetMapping("/users")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @GetMapping("/me")
    @RolesAllowed({ "CLIENT", "ADMIN" })
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        var user = this.userService.getUserById(userId);
        return ResponseEntity.ok(UserResponseDTO.fromEntity(user));
    }

    @GetMapping("/me/wishlist")
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<List<GamePopulatedDTO>> getMyWishlist(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        var wishlist = this.userService.getWishlist(userId);
        return ResponseEntity.ok(wishlist.stream().map(GamePopulatedDTO::fromEntity).toList());
    }

    @PostMapping("/me/wishlist/{gameId}")
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<Void> addGameToMyWishlist(@AuthenticationPrincipal Jwt jwt, @PathVariable Long gameId) {
        Long userId = jwt.getClaim("userId");
        this.userService.addGameToWishlist(userId, gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/wishlist/{gameId}")
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<Void> removeGameFromMyWishlist(@AuthenticationPrincipal Jwt jwt, @PathVariable Long gameId) {
        Long userId = jwt.getClaim("userId");
        this.userService.removeGameFromWishlist(userId, gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/recommendations")
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<List<RecommendationItemDTO>> getMyRecommendations(@AuthenticationPrincipal Jwt jwt) throws Exception {
        Long userId = jwt.getClaim("userId");
        var recommendations = this.recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }
}
