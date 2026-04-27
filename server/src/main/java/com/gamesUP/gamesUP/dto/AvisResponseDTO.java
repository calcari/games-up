package com.gamesUP.gamesUP.dto;

import com.gamesUP.gamesUP.entities.Avis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvisResponseDTO {

    private Long id;
    private Long gameId;
    private Long writerId;
    private String writerName;
    private String comment;
    private int note;

    public static AvisResponseDTO fromEntity(Avis avis) {
        var writer = avis.getWriter();
        var game = avis.getGame();
        return new AvisResponseDTO(
                avis.getId(),
                game != null ? game.getId() : null,
                writer != null ? writer.getId() : null,
                writer != null ? writer.getName() : null,
                avis.getComment(),
                avis.getNote());
    }
}
