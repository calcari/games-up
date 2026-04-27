package com.gamesUP.gamesUP.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gamesUP.gamesUP.dto.RecommendationItemDTO;
import com.gamesUP.gamesUP.entities.Avis;
import com.gamesUP.gamesUP.errors.EntityNotFoundException;
import com.gamesUP.gamesUP.repositories.AvisRepository;
import com.gamesUP.gamesUP.repositories.GameRepository;

@Service
public class RecommendationService implements IRecommendationService {

    @Value("${python.api.url:http://127.0.0.1:8000/recommendations/}")
    private String pythonApiUrl; // Pour pouvoir avoir une URL http://python-api:8000/recommendations/ avec Docker

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private AvisRepository avisRepository;

    public List<RecommendationItemDTO> getRecommendations(Long userId) throws Exception {

        var json = new ObjectMapper();

        var avisList = this.avisRepository.findPurchasedAvisByUserId(userId);
        var purchasesArray = json.createArrayNode();

        for (Avis avis : avisList) {
            var game = this.gameRepository.findByIdPopulated(avis.getGame().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Game", avis.getGame().getId()));

            var line = json.createObjectNode()
                    .put("game_id", game.getId())
                    .put("game_name", game.getName())
                    .put("author_id", game.getAuthor().getId())
                    .put("publisher_id", game.getPublisher().getId())
                    .put("price", game.getPrice())
                    .put("user_note", avis.getNote())
                    .put("genre_id", game.getGenre().getId())
                    .put("category_id", game.getCategory().getId())
                    .put("mean_note", game.getMeanNote());
            purchasesArray.add(line);
        }

        ObjectNode body = json.createObjectNode()
                .put("user_id", userId)
                .set("purchases", purchasesArray);

        JsonNode recommendations = this.callPythonApi(body);

        if (recommendations == null || !recommendations.isArray()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Python API failed");
        }

        var result = new ArrayList<RecommendationItemDTO>();
        for (JsonNode recommendation : recommendations) {

            var dto = new RecommendationItemDTO();
            dto.setGameId(recommendation.get("game_id").asLong());
            dto.setGameName(recommendation.get("game_name").asText());

            result.add(dto);
        }
        return result;
    }

    public JsonNode callPythonApi(ObjectNode body) {
        return RestClient.create().post()
                .uri(this.pythonApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
    }
}
