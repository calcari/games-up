package com.gamesUP.gamesUP.services;

import java.util.List;

import com.gamesUP.gamesUP.dto.AvisCreateBodyDTO;
import com.gamesUP.gamesUP.dto.GameBodyDTO;
import com.gamesUP.gamesUP.entities.Avis;
import com.gamesUP.gamesUP.entities.Game;

public interface IGameService {

    List<Game> getAllGamesPopulated();

    Game getGameByIdPopulated(Long id);

    Game createGame(GameBodyDTO payload);

    Game updateGame(Long id, GameBodyDTO payload);

    void deleteGame(Long id);

    List<Avis> getGameAvis(Long gameId);

    Avis createAvis(Long gameId, Long writerId, AvisCreateBodyDTO payload);
}
