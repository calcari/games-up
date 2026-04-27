package com.gamesUP.gamesUP.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.gamesUP.gamesUP.entities.Game;
import com.gamesUP.gamesUP.repositories.CategoryRepository;
import com.gamesUP.gamesUP.repositories.ContactRepository;
import com.gamesUP.gamesUP.repositories.GameRepository;
import com.gamesUP.gamesUP.repositories.GenreRepository;
import com.gamesUP.gamesUP.services.GameService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class GameServiceUnitTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void getAllGamesPopulated_Success() {

        // GIVEN
        var game = new Game();
        game.setName("1000 Bornes");
        when(gameRepository.findAllPopulated()).thenReturn(List.of(game));

        // WHEN
        var result = gameService.getAllGamesPopulated();

        // THEN
        assertEquals(game.getName(), result.get(0).getName());
    }

    @Test
    void getGameByIdPopulated_Success() {

        // GIVEN
        var game = new Game();
        game.setName("1000 Bornes");
        when(gameRepository.findByIdPopulated(org.mockito.Mockito.anyLong())).thenReturn(Optional.of(game));

        // WHEN
        var result = gameService.getGameByIdPopulated(1L);

        // THEN
        assertEquals(game.getName(), result.getName());
    }

}
