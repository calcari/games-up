package com.gamesUP.gamesUP.dto;

import com.gamesUP.gamesUP.entities.Game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePopulatedDTO {

    private Long id;
    private String name;
    private int numEdition;
    private int stock;
    private Double price;
    private Long authorId;
    private String authorName;
    private Long publisherId;
    private String publisherName;
    private Long genreId;
    private String genreType;
    private Long categoryId;
    private String categoryType;
    private Double meanNote;

    public static GamePopulatedDTO fromEntity(Game game) {
        var author = game.getAuthor();
        var publisher = game.getPublisher();
        var genre = game.getGenre();
        var category = game.getCategory();
        return new GamePopulatedDTO(
                game.getId(),
                game.getName(),
                game.getNumEdition(),
                game.getStock(),
                game.getPrice(),
                author != null ? author.getId() : null,
                author != null ? author.getName() : null,
                publisher != null ? publisher.getId() : null,
                publisher != null ? publisher.getName() : null,
                genre != null ? genre.getId() : null,
                genre != null ? genre.getType() : null,
                category != null ? category.getId() : null,
                category != null ? category.getType() : null,
                game.getMeanNote());

    }
}
