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

import com.gamesUP.gamesUP.entities.Category;
import com.gamesUP.gamesUP.repositories.CategoryRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;
import com.gamesUP.gamesUP.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Category category1;
    private Category category2;

    @BeforeEach
    void beforeEach() {
        TestUtils.seedUsers(userRepository, passwordEncoder);

        category1 = categoryRepository.save(new Category(null, "Jeux collaboratifs"));
        category2 = categoryRepository.save(new Category(null, "Jeux de cartes"));
    }

    @Test
    @WithMockUser(username = "client@test.fr", roles = "CLIENT")
    void getCategories_Success() throws Exception {

        mockMvc.perform(get("/categories"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.categories[0].type").value(this.category1.getType()));
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void createCategory_Success() throws Exception {
        var body = """
                {
                    "type": "Jeux de culture générale"
                }
                """;

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void deleteCategory_Success() throws Exception {

        mockMvc.perform(delete("/categories/" + this.category1.getId()))
                .andExpect(status().isNoContent());

        assertTrue(categoryRepository.findById(this.category1.getId()).isEmpty());
    }
}
