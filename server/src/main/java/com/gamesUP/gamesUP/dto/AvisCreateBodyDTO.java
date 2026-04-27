package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AvisCreateBodyDTO {

    @NotBlank
    private String comment;

    @Positive
    @Max(5)
    private int note;
}
