package com.gamesUP.gamesUP.integration;

import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.http.HttpHeaders;
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
class UserControllerIntegrationTest {

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

        private Game game;

        @BeforeEach
        void beforeEach() {
                TestUtils.seedUsers(userRepository, passwordEncoder);

                var author = new Contact();
                author.setName("Auteur");
                author = contactRepository.save(author);

                var publisher = new Contact();
                publisher.setName("Éditeur");
                publisher = contactRepository.save(publisher);

                var category = new Category();
                category.setType("Catégorie");
                category = categoryRepository.save(category);

                var genre = new Genre();
                genre.setType("Genre");
                genre = genreRepository.save(genre);

                game = new Game();
                game.setName("Jeu en wishlist");
                game.setAuthor(author);
                game.setPublisher(publisher);
                game.setCategory(category);
                game.setGenre(genre);
                game.setNumEdition(1);
                game.setPrice(15.0);
                game.setStock(2);
                game = gameRepository.save(game);
        }

        @Test
        @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
        void getAllUsers_Success() throws Exception {

                mockMvc.perform(get("/users"))
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void getAllUsers_ForbiddenForClient() throws Exception {

                mockMvc.perform(get("/users"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void getMe_Success() throws Exception {

                // WithMockUser ne créé pas de jwt, on fait un login pour en obtenir un
                String authHeader = TestUtils.loginClient(mockMvc);

                mockMvc.perform(get("/me").header(HttpHeaders.AUTHORIZATION, authHeader))
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$.email").value("client@test.fr"))
                                .andExpect(jsonPath("$.role").value("CLIENT"));
        }

        @Test
        @WithMockUser(username = "client@test.fr", roles = "CLIENT")
        void whishlist_AddAndRemove() throws Exception {

                // WithMockUser ne créé pas de jwt, on fait un login pour en obtenir un
                String authHeader = TestUtils.loginClient(mockMvc);

                mockMvc.perform(get("/me/wishlist").header(HttpHeaders.AUTHORIZATION, authHeader))
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$", hasSize(0)));

                mockMvc.perform(post("/me/wishlist/" + this.game.getId()).header(HttpHeaders.AUTHORIZATION, authHeader))
                                .andExpect(status().is2xxSuccessful());

                mockMvc.perform(get("/me/wishlist").header(HttpHeaders.AUTHORIZATION, authHeader))
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name").value("Jeu en wishlist"));

                mockMvc.perform(delete("/me/wishlist/" + this.game.getId()).header(HttpHeaders.AUTHORIZATION, authHeader))
                                .andExpect(status().is2xxSuccessful());

                mockMvc.perform(get("/me/wishlist").header(HttpHeaders.AUTHORIZATION, authHeader))
                                .andExpect(status().is2xxSuccessful())
                                .andExpect(jsonPath("$", hasSize(0)));
        }
}
