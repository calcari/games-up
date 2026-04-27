package com.gamesUP.gamesUP.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gamesUP.gamesUP.entities.PurchaseLine;

@Repository
public interface PurchaseLineRepository extends JpaRepository<PurchaseLine, Long> {
}
