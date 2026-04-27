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

import com.gamesUP.gamesUP.entities.Contact;
import com.gamesUP.gamesUP.repositories.ContactRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;
import com.gamesUP.gamesUP.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ContactRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Contact author;
    private Contact editor;

    @BeforeEach
    void beforeEach() {
        TestUtils.seedUsers(userRepository, passwordEncoder);

        author = new Contact();
        author.setName("Jean auteur");
        author = contactRepository.save(author);

        editor = new Contact();
        editor.setName("Jean éditeur");
        editor = contactRepository.save(editor);
    }

    @Test
    @WithMockUser(username = "client@test.fr", roles = "CLIENT")
    void getContacts_Success() throws Exception {

        mockMvc.perform(get("/contacts"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.contacts[0].name").value(this.author.getName()));
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void createContact_Success() throws Exception {
        var body = """
                {
                    "name": "Jean indépendant"
                }
                """;

        mockMvc.perform(post("/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void deleteContact_Success() throws Exception {

        mockMvc.perform(delete("/contacts/" + this.author.getId()))
                .andExpect(status().isNoContent());

        assertTrue(contactRepository.findById(this.author.getId()).isEmpty());
    }
}
