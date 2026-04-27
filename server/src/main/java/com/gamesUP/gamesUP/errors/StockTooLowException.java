package com.gamesUP.gamesUP.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StockTooLowException extends ResponseStatusException {

    public StockTooLowException(Long gameId, int quantity, int stock) {
        super(HttpStatus.BAD_REQUEST, "Stock insuffisant pour le jeu n°" + gameId + " (demandé : " + quantity + " dispo : " + stock + ")");
    }
}
