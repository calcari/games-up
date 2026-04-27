package com.gamesUP.gamesUP.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gamesUP.gamesUP.entities.Purchase;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

        @Query("""
                        SELECT purchase FROM Purchase purchase
                        JOIN FETCH purchase.user
                        JOIN FETCH purchase.lines line
                        JOIN FETCH line.game
                        """)
        List<Purchase> findAllPopulated();

        @Query("""
                        SELECT purchase FROM Purchase purchase
                        JOIN FETCH purchase.user
                        JOIN FETCH purchase.lines line
                        JOIN FETCH line.game
                        WHERE purchase.id = :id
                        """)
        Optional<Purchase> findOnePopulatedById(@Param("id") Long id);

}