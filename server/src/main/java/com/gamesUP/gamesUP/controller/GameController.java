package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AvisCreateBodyDTO;
import com.gamesUP.gamesUP.dto.AvisResponseDTO;
import com.gamesUP.gamesUP.dto.GameBodyDTO;
import com.gamesUP.gamesUP.dto.GamePopulatedDTO;
import com.gamesUP.gamesUP.services.IGameService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private IGameService gameService;

    @GetMapping
    @RolesAllowed({ "CLIENT", "ADMIN" })
    public ResponseEntity<List<GamePopulatedDTO>> getAllGames() {
        var games = gameService.getAllGamesPopulated().stream()
                .map(GamePopulatedDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    @RolesAllowed({ "CLIENT", "ADMIN" })
    public ResponseEntity<GamePopulatedDTO> getGameById(@PathVariable Long id) {
        var game = gameService.getGameByIdPopulated(id);
        return ResponseEntity.ok(GamePopulatedDTO.fromEntity(game));
    }

    @PostMapping
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<GamePopulatedDTO> createGame(@RequestBody GameBodyDTO body) {
        var created = gameService.createGame(body);
        var populated = gameService.getGameByIdPopulated(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(GamePopulatedDTO.fromEntity(populated));
    }

    @PutMapping("/{id}")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<GamePopulatedDTO> updateGame(@PathVariable Long id, @RequestBody GameBodyDTO body) {
        var updated = gameService.updateGame(id, body);
        var populated = gameService.getGameByIdPopulated(updated.getId());
        return ResponseEntity.ok(GamePopulatedDTO.fromEntity(populated));
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/avis")
    @RolesAllowed({ "CLIENT", "ADMIN" })
    public ResponseEntity<List<AvisResponseDTO>> getAvisForGame(@PathVariable Long id) {
        var avisList = this.gameService.getGameAvis(id);
        return ResponseEntity.ok(avisList.stream().map(AvisResponseDTO::fromEntity).toList());
    }

    @PostMapping("/{id}/avis")
    @RolesAllowed({ "CLIENT", "ADMIN" })
    public ResponseEntity<AvisResponseDTO> createAvis(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @RequestBody AvisCreateBodyDTO body) {
        Long writerId = jwt.getClaim("userId");
        var avis = this.gameService.createAvis(id, writerId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(AvisResponseDTO.fromEntity(avis));
    }
}
