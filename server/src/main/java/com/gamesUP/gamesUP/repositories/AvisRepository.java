package com.gamesUP.gamesUP.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gamesUP.gamesUP.entities.Avis;

import java.util.List;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {

    @Query("SELECT avis FROM Avis avis JOIN FETCH avis.writer WHERE avis.game.id = :gameId ORDER BY avis.id")
    List<Avis> findByGameId(@Param("gameId") Long gameId);

    // Avis d’un utilisateur sur les jeux qu’il a déjà achetés
    @Query("""
            SELECT DISTINCT avis FROM Avis avis
            INNER JOIN FETCH avis.game game
            INNER JOIN PurchaseLine purchaseLine ON purchaseLine.game = game
            INNER JOIN purchaseLine.purchase purchase
            INNER JOIN purchase.user buyer
            WHERE avis.writer.id = :userId
            AND buyer.id = :userId
            """)
    List<Avis> findPurchasedAvisByUserId(@Param("userId") Long userId);

}
