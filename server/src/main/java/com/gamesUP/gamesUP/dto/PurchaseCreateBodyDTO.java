package com.gamesUP.gamesUP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreateBodyDTO {

    @Size(min = 1)
    private List<PurchaseLineItemCreateDTO> lines;
}
