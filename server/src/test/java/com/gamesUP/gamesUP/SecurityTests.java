package com.gamesUP.gamesUP;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.gamesUP.gamesUP.config.SecurityConfig;
import com.gamesUP.gamesUP.repositories.UserRepository;
import com.gamesUP.gamesUP.utils.TestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityConfig securityConfig;

    @BeforeEach
    void seedUsers() {
        TestUtils.seedUsers(userRepository, passwordEncoder);
    }

    @Test
    void login_FailOnWrongPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("""
                        {
                            "email": "client@test.fr",
                            "password": "mauvais_mdp"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ReturnsValidToken() throws Exception {
        String authHeader = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("""
                        {
                            "email": "admin@test.fr",
                            "password": "mdp_admin"
                        }
                        """))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);

        assertNotNull(authHeader);
        assert (authHeader.startsWith("Bearer "));

        String token = authHeader.replace("Bearer ", "");
        var decodedToken = securityConfig.jwtDecoder().decode(token);
        assertEquals("admin@test.fr", decodedToken.getSubject());
        assertEquals("ADMIN", decodedToken.getClaim("role"));
    }

    @Test
    void register_Success() throws Exception {
        String authHeader = mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content("""
                        {
                            "email": "jean.dupont@test.fr",
                            "password": "motdepasse",
                            "name": "Jean Dupont"
                        }
                        """))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);

        assertNotNull(authHeader);
        assert (authHeader.startsWith("Bearer "));

        String token = authHeader.replace("Bearer ", "");
        var decodedToken = securityConfig.jwtDecoder().decode(token);
        assertEquals("jean.dupont@test.fr", decodedToken.getSubject());
        assertEquals("CLIENT", decodedToken.getClaim("role"));
    }

    @Test
    void register_DuplicateEmail() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content("""
                        {
                            "email": "client@test.fr",
                            "password": "motdepasse1",
                            "name": "Jean Double"
                        }
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void register_FailWhenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content("""
                        {
                            "email": "court@test.fr",
                            "password": "court",
                            "name": "Jean Court"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noAuthReturns401() throws Exception {
        mockMvc.perform(get("/games")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@test.fr", roles = "ADMIN")
    void adminEndpointsAllowedToAdmins() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(username = "client@test.fr", roles = "CLIENT")
    void adminEndpointsForbiddenToClients() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
    }
}
