package com.gamesUP.gamesUP.services;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gamesUP.gamesUP.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AppUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var userEntity = userRepository.findByEmail(email);
        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException(email + " not found");
        }

        // Ajout du prefix ROLE_ pour correspondre qu'on puisse faire @PreAuthorize("hasRole('ADMIN')")
        GrantedAuthority role = new SimpleGrantedAuthority("ROLE_" + userEntity.get().getRole().name());

        // Fournit le mot de passe et l'username à spring security
        return new org.springframework.security.core.userdetails.User(
                userEntity.get().getEmail(),
                userEntity.get().getPassword(),
                List.of(role));
    }

}