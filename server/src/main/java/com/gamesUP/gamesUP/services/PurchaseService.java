package com.gamesUP.gamesUP.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gamesUP.gamesUP.dto.PurchaseCreateBodyDTO;
import com.gamesUP.gamesUP.entities.Purchase;
import com.gamesUP.gamesUP.entities.PurchaseLine;
import com.gamesUP.gamesUP.errors.EntityNotFoundException;
import com.gamesUP.gamesUP.errors.StockTooLowException;
import com.gamesUP.gamesUP.repositories.GameRepository;
import com.gamesUP.gamesUP.repositories.PurchaseLineRepository;
import com.gamesUP.gamesUP.repositories.PurchaseRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private PurchaseLineRepository purchaseLineRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;

    public List<Purchase> getAllPurchases() {
        return this.purchaseRepository.findAllPopulated();
    }

    public Purchase createPurchase(Long userId, PurchaseCreateBodyDTO body) {

        var user = this.userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));

        var purchase = new Purchase();
        purchase.setUser(user);
        purchase.setDate(LocalDateTime.now());
        purchase = this.purchaseRepository.save(purchase);

        for (var linePayload : body.getLines()) {

            var game = this.gameRepository.findById(linePayload.getGameId()).orElseThrow(() -> new EntityNotFoundException("Game", linePayload.getGameId()));

            if (game.getStock() < linePayload.getQuantity()) {
                throw new StockTooLowException(game.getId(), linePayload.getQuantity(), game.getStock());
            }

            var line = new PurchaseLine();
            line.setPurchase(purchase);
            line.setPrice(game.getPrice()); // Application du prix au moment de l'achat. (Il pourrait changer ensuite)
            line.setQuantity(linePayload.getQuantity());
            line.setGame(game);
            this.purchaseLineRepository.save(line);

            purchase.getLines().add(line);

            // Diminution du stock
            game.setStock(game.getStock() - linePayload.getQuantity());
            this.gameRepository.save(game);

        }

        var populatedPurchase = this.purchaseRepository.findOnePopulatedById(purchase.getId()).orElseThrow();
        return populatedPurchase;
    }
}
