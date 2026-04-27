package com.gamesUP.gamesUP.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesUP.gamesUP.repositories.UserRepository;
import com.gamesUP.gamesUP.services.RecommendationService;
import com.gamesUP.gamesUP.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RecommendationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Spy au lieu de Mock : on garde la vraie logique du service mais on stub
    // l'appel HTTP vers l'api Python.
    @SpyBean
    private RecommendationService recommendationService;

    @BeforeEach
    void beforeEach() {
        TestUtils.seedUsers(userRepository, passwordEncoder);
    }

    @Test
    void getMyRecommendations_Success() throws Exception {

        // GIVEN
        var json = new ObjectMapper();
        var fakeRecommandedGame = json.createObjectNode()
                .put("game_id", 42L)
                .put("game_name", "Monopoly");
        var fakeFastApiResponse = json.createArrayNode().add(fakeRecommandedGame);

        doReturn(fakeFastApiResponse).when(recommendationService).callPythonApi(any());
        String authHeader = TestUtils.loginClient(mockMvc);

        // WHEN
        mockMvc.perform(get("/me/recommendations").header(HttpHeaders.AUTHORIZATION, authHeader))
                // THEN
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameId").value(42))
                .andExpect(jsonPath("$[0].gameName").value("Monopoly"));
    }
}
