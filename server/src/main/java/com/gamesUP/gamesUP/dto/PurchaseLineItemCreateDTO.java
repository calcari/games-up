package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLineItemCreateDTO {

    @NonNull
    private Long gameId;

    @Positive
    private int quantity;
}
