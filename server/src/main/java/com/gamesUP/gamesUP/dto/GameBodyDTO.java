package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameBodyDTO {

    @NotBlank
    private String name;

    @NotNull
    private Long authorId;

    @NotNull
    private Long publisherId;

    @NotNull
    private int numEdition;

    @NotNull
    private int stock;

    @NotNull
    private Double price;

    // Nullable
    private Long genreId;

    // Nullable
    private Long categoryId;
}
