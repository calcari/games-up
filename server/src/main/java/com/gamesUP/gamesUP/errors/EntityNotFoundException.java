package com.gamesUP.gamesUP.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {

    public EntityNotFoundException(String className, Long id) {
        super(HttpStatus.NOT_FOUND, className + " n°" + id + " not found");
    }
}
