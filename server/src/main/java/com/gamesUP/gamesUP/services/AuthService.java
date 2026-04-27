package com.gamesUP.gamesUP.services;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.dto.LoginBodyDTO;
import com.gamesUP.gamesUP.dto.RegisterBodyDTO;
import com.gamesUP.gamesUP.entities.User;
import com.gamesUP.gamesUP.enums.Role;
import com.gamesUP.gamesUP.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtEncoder jwtEncoder;

    public String register(RegisterBodyDTO req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.CLIENT);

        userRepository.save(user);

        String token = this.generateToken(user);
        return token;
    }

    public String login(LoginBodyDTO request) {

        // Authentification par spring security
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Récupération de l'utilisateur pour l'ajouter dans le JWT
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        // Pas besoin de throw une vraie erreur ici, spring vient d'authentifier l'utilisateur

        String token = this.generateToken(user);

        return token;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }
}