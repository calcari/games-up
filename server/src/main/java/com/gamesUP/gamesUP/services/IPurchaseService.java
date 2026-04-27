package com.gamesUP.gamesUP.services;

import java.util.List;

import com.gamesUP.gamesUP.dto.PurchaseCreateBodyDTO;
import com.gamesUP.gamesUP.entities.Purchase;

public interface IPurchaseService {

    List<Purchase> getAllPurchases();

    Purchase createPurchase(Long userId, PurchaseCreateBodyDTO payload);
}
