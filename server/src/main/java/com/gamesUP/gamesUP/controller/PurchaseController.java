package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseCreateBodyDTO;
import com.gamesUP.gamesUP.dto.PurchaseResponseDTO;
import com.gamesUP.gamesUP.services.IPurchaseService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PurchaseController {

    @Autowired
    private IPurchaseService purchaseService;

    @GetMapping("/purchases")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases() {
        var purchaseList = this.purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchaseList.stream().map(PurchaseResponseDTO::fromEntity).toList());
    }

    @PostMapping("/purchases")
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<PurchaseResponseDTO> createPurchase(@AuthenticationPrincipal Jwt jwt, @RequestBody PurchaseCreateBodyDTO body) {
        Long userId = jwt.getClaim("userId");
        var purchase = this.purchaseService.createPurchase(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(PurchaseResponseDTO.fromEntity(purchase));
    }
}
