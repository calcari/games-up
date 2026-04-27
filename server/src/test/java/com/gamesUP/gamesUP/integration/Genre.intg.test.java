package com.gamesUP.gamesUP.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.gamesUP.gamesUP.entities.Genre;
import com.gamesUP.gamesUP.repositories.GenreRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;
import com.gamesUP.gamesUP.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GenreRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void seed() {
        TestUtils.seedUsers(userRepository, passwordEncoder);

        genre1 = genreRepository.save(new Genre(null, "Jeux d'ambiance"));
        genre2 = genreRepository.save(new Genre(null, "Jeux de rôle"));
    }

    @Test
    @WithMockUser(username = "client@test.fr", roles = "CLIENT")
    void getGenres_Success() throws Exception {

        mockMvc.perform(get("/genres"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.genres[0].type").value(this.genre1.getType()));
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void createGenre_Success() throws Exception {
        var body = """
                {
                    "type": "Jeux de logique"
                }
                """;

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void deleteGenre_Success() throws Exception {

        mockMvc.perform(delete("/genres/" + this.genre1.getId()))
                .andExpect(status().isNoContent());

        assertTrue(genreRepository.findById(this.genre1.getId()).isEmpty());
    }
}
