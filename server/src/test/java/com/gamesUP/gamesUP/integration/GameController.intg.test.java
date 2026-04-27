package com.gamesUP.gamesUP.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.gamesUP.gamesUP.entities.Category;
import com.gamesUP.gamesUP.entities.Contact;
import com.gamesUP.gamesUP.entities.Game;
import com.gamesUP.gamesUP.entities.Genre;
import com.gamesUP.gamesUP.repositories.CategoryRepository;
import com.gamesUP.gamesUP.repositories.ContactRepository;
import com.gamesUP.gamesUP.repositories.GameRepository;
import com.gamesUP.gamesUP.repositories.GenreRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;
import com.gamesUP.gamesUP.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GameControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private GameRepository gameRepository;

        @Autowired
        private ContactRepository contactRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private GenreRepository genreRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private Contact author;
        private Contact publisher;
        private Category category;
        private Genre genre;
        private Game game;

        @BeforeEach
        void beforeEach() {
                // GIVEN
                TestUtils.seedUsers(userRepository, passwordEncoder);

                author = new Contact();
                author.setName("Auteur");
                author = contactRepository.save(author);

                publisher = new Contact();
                publisher.setName("Éditeur");
                publisher = contactRepository.save(publisher);

                category = new Category();
                category.setType("Catégorie");
                category = categoryRepository.save(category);

                genre = new Genre();
                genre.setType("Genre");
                genre = genreRepository.save(genre);

                game = new Game();
                game.setName("UNO");
                game.setAuthor(author);
                game.setPublisher(publisher);
                game.setCategory(category);
                game.setGenre(genre);
                game.setNumEdition(1);
                game.setPrice(12.5);
                game.setStock(3);
                gameRepository.save(game);
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void getAllGames_Success() throws Exception {
                // WHEN

                mockMvc.perform(get("/games"))
                                // THEN
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$[0].name").value("UNO"));
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void getGameById_Success() throws Exception {
                // WHEN

                mockMvc.perform(get("/games/" + this.game.getId()))
                                // THEN
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$.id").value(this.game.getId()))
                                .andExpect(jsonPath("$.name").value("UNO"))
                                .andExpect(jsonPath("$.authorName").value("Auteur"))
                                .andExpect(jsonPath("$.publisherName").value("Éditeur"))
                                .andExpect(jsonPath("$.categoryType").value("Catégorie"))
                                .andExpect(jsonPath("$.genreType").value("Genre"));
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void getGameById_NotFound() throws Exception {
                // WHEN

                mockMvc.perform(get("/games/999"))
                                // THEN
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
        void createGame_Success() throws Exception {
                // WHEN
                var body = ("""
                                {
                                    "name": "Monopoly",
                                    "authorId": %d,
                                    "publisherId": %d,
                                    "numEdition": 2,
                                    "stock": 7,
                                    "price": 34.0,
                                    "genreId": %d,
                                    "categoryId": %d
                                }
                                """)
                                .formatted(
                                                this.author.getId(),
                                                this.publisher.getId(),
                                                this.genre.getId(),
                                                this.category.getId());

                mockMvc.perform(post("/games")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                // THEN
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value("Monopoly"))
                                .andExpect(jsonPath("$.numEdition").value(2))
                                .andExpect(jsonPath("$.stock").value(7))
                                .andExpect(jsonPath("$.price").value(34.0))
                                .andExpect(jsonPath("$.authorName").value("Auteur"))
                                .andExpect(jsonPath("$.publisherName").value("Éditeur"))
                                .andExpect(jsonPath("$.genreType").value("Genre"))
                                .andExpect(jsonPath("$.categoryType").value("Catégorie"));
        }

        @Test
        @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
        void updateGame_Success() throws Exception {
                // WHEN
                var body = ("""
                                {
                                    "name": "Monopoly",
                                    "authorId": %d,
                                    "publisherId": %d,
                                    "numEdition": 2,
                                    "stock": 7,
                                    "price": 34.0,
                                    "genreId": %d,
                                    "categoryId": %d
                                }
                                """)
                                .formatted(
                                                this.author.getId(),
                                                this.publisher.getId(),
                                                this.genre.getId(),
                                                this.category.getId());

                mockMvc.perform(put("/games/" + this.game.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                // THEN
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$.id").value(this.game.getId()))
                                .andExpect(jsonPath("$.name").value("Monopoly"))
                                .andExpect(jsonPath("$.numEdition").value(2))
                                .andExpect(jsonPath("$.stock").value(7))
                                .andExpect(jsonPath("$.price").value(34.0))
                                .andExpect(jsonPath("$.authorName").value("Auteur"))
                                .andExpect(jsonPath("$.publisherName").value("Éditeur"))
                                .andExpect(jsonPath("$.genreType").value("Genre"))
                                .andExpect(jsonPath("$.categoryType").value("Catégorie"));
        }

        @Test
        @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
        void deleteGame_Success() throws Exception {
                // WHEN

                mockMvc.perform(delete("/games/" + this.game.getId()))
                                // THEN
                                .andExpect(status().is2xxSuccessful());

                mockMvc.perform(get("/games/" + this.game.getId()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void getAvisForGame_Success() throws Exception {

                mockMvc.perform(get("/games/" + this.game.getId() + "/avis"))
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void postAvis_Success() throws Exception {
                var body = """
                                {
                                    "email": "client@test.fr",
                                    "password": "mdp_client"
                                }
                                """;

                String authHeader = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andReturn()
                                .getResponse()
                                .getHeader(HttpHeaders.AUTHORIZATION);

                body = """
                                {
                                    "comment": "Très fun en famille",
                                    "note": 5
                                }
                                """;

                mockMvc.perform(post("/games/" + this.game.getId() + "/avis")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .content(body))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.comment").value("Très fun en famille"))
                                .andExpect(jsonPath("$.note").value(5))
                                .andExpect(jsonPath("$.gameId").value(this.game.getId()));

                mockMvc.perform(get("/games/" + this.game.getId() + "/avis").with(user("client@test.fr").roles("CLIENT")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }

}
