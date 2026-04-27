package com.gamesUP.gamesUP.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.gamesUP.gamesUP.entities.User;
import com.gamesUP.gamesUP.enums.Role;
import com.gamesUP.gamesUP.repositories.UserRepository;

public class TestUtils {

    public static void seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        var client = new User();
        client.setName("name_client");
        client.setEmail("client@test.fr");
        client.setPassword(passwordEncoder.encode("mdp_client"));
        client.setRole(Role.CLIENT);
        userRepository.save(client);

        var admin = new User();
        admin.setName("name_admin");
        admin.setEmail("admin@test.fr");
        admin.setPassword(passwordEncoder.encode("mdp_admin"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    public static String loginClient(MockMvc mockMvc) throws Exception {
        var body = """
                 {
                     "email": "client@test.fr",
                     "password": "mdp_client"
                 }
                """;
        return mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(body))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.AUTHORIZATION);
    }
}
