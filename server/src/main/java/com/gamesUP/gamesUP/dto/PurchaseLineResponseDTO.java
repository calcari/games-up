package com.gamesUP.gamesUP.dto;

import com.gamesUP.gamesUP.entities.PurchaseLine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLineResponseDTO {

    private Long gameId;
    private String gameName;
    private Double unitPrice;
    private int quantity;

    public static PurchaseLineResponseDTO fromEntity(PurchaseLine line) {
        var game = line.getGame();
        return new PurchaseLineResponseDTO(
                game != null ? game.getId() : null,
                game != null ? game.getName() : null,
                line.getPrice(),
                line.getQuantity());
    }
}
