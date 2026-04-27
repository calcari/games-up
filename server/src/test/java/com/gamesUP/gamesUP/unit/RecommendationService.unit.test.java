package com.gamesUP.gamesUP.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.entities.Avis;
import com.gamesUP.gamesUP.entities.Category;
import com.gamesUP.gamesUP.entities.Contact;
import com.gamesUP.gamesUP.entities.Game;
import com.gamesUP.gamesUP.entities.Genre;
import com.gamesUP.gamesUP.repositories.AvisRepository;
import com.gamesUP.gamesUP.repositories.GameRepository;
import com.gamesUP.gamesUP.services.RecommendationService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RecommendationServiceUnitTest {

    @Mock
    private AvisRepository avisRepository;

    @Mock
    private GameRepository gameRepository;

    @Spy
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_Success() throws Exception {

        // GIVEN
        var author = new Contact();
        author.setId(1L);
        var publisher = new Contact();
        publisher.setId(2L);
        var genre = new Genre();
        genre.setId(3L);
        var category = new Category();
        category.setId(4L);

        var game = new Game();
        game.setId(10L);
        game.setName("1000 Bornes");
        game.setAuthor(author);
        game.setPublisher(publisher);
        game.setGenre(genre);
        game.setCategory(category);
        game.setPrice(15.5);
        game.setMeanNote(4.2);

        var avis = new Avis();
        avis.setNote(5);
        avis.setGame(game);

        when(avisRepository.findPurchasedAvisByUserId(1L)).thenReturn(List.of(avis));
        when(gameRepository.findByIdPopulated(game.getId())).thenReturn(Optional.of(game));

        var json = new ObjectMapper();
        var fakeRecommandedGame = json.createObjectNode()
                .put("game_id", 42L)
                .put("game_name", "Monopoly");
        var fakeFastApiResponse = json.createArrayNode().add(fakeRecommandedGame);

        doReturn(fakeFastApiResponse).when(recommendationService).callPythonApi(any());

        // WHEN
        var result = recommendationService.getRecommendations(1L);

        // THEN
        assertEquals(1, result.size());
        assertEquals(42L, result.get(0).getGameId());
        assertEquals("Monopoly", result.get(0).getGameName());
    }
}
