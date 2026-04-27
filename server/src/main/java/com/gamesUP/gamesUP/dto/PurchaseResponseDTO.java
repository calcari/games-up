package com.gamesUP.gamesUP.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.gamesUP.gamesUP.entities.Purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime date;
    private boolean paid;
    private boolean delivered;
    private boolean archived;
    private List<PurchaseLineResponseDTO> lines;
    private Double totalPrice;

    public static PurchaseResponseDTO fromEntity(Purchase purchase) {

        return new PurchaseResponseDTO(
                purchase.getId(),
                purchase.getUser() != null ? purchase.getUser().getId() : null,
                purchase.getUser() != null ? purchase.getUser().getName() : null,
                purchase.getDate(),
                purchase.isPaid(),
                purchase.isDelivered(),
                purchase.isArchived(),
                purchase.getLines().stream().map(PurchaseLineResponseDTO::fromEntity).toList(),
                purchase.getTotal());
    }
}
