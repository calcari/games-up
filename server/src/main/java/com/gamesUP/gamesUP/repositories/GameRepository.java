package com.gamesUP.gamesUP.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gamesUP.gamesUP.entities.Game;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("""
                SELECT game FROM Game game
                JOIN FETCH game.author
                JOIN FETCH game.publisher
                LEFT JOIN FETCH game.genre
                LEFT JOIN FETCH game.category
            """)
    List<Game> findAllPopulated();

    @Query("""
                SELECT game FROM Game game
                JOIN FETCH game.author
                JOIN FETCH game.publisher
                LEFT JOIN FETCH game.genre
                LEFT JOIN FETCH game.category
                WHERE game.id = :id
            """)
    Optional<Game> findByIdPopulated(Long id);

    @Query("""
                SELECT game FROM Game game
                JOIN FETCH game.author
                JOIN FETCH game.publisher
                LEFT JOIN FETCH game.genre
                LEFT JOIN FETCH game.category
                JOIN game.wishedBy user
                WHERE user.id = :userId
            """)
    List<Game> wishlistPopulated(Long userId);
}
