package com.gamesUP.gamesUP.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
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
class PurchaseControllerIntegrationTest {

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
        game.setName("Commande test");
        game.setAuthor(author);
        game.setPublisher(publisher);
        game.setCategory(category);
        game.setGenre(genre);
        game.setNumEdition(1);
        game.setPrice(20.30);
        game.setStock(5);
        game = gameRepository.save(game);
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void getPurchases_Success() throws Exception {

        mockMvc.perform(get("/purchases"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "client@test.fr", roles = "CLIENT")
    void getPurchases_ForbiddenForClient() throws Exception {

        mockMvc.perform(get("/purchases"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPurchase_Success() throws Exception {
        String authHeader = TestUtils.loginClient(mockMvc);
        var body = """
                {
                    "lines": [ { "gameId": %d, "quantity": 2 } ]
                }
                """
                .formatted(this.game.getId());

        mockMvc.perform(post("/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("name_client"))
                .andExpect(jsonPath("$.lines[0].quantity").value(2))
                .andExpect(jsonPath("$.lines[0].unitPrice").value(20.30))
                .andExpect(jsonPath("$.totalPrice").value(40.60));

        this.game = gameRepository.findById(this.game.getId()).orElseThrow();
        Assertions.assertEquals(5 - 2, this.game.getStock());

        mockMvc.perform(get("/purchases").with(user("admin@test.fr").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].totalPrice").value(40.60));
    }

    @Test
    void createPurchase_StockTooLow() throws Exception {
        String authHeader = TestUtils.loginClient(mockMvc);
        var body = """
                {
                    "lines": [ { "gameId": %d, "quantity": 100 } ]
                }
                """
                .formatted(this.game.getId());

        mockMvc.perform(post("/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .content(body))
                .andExpect(status().isBadRequest());

    }
}
