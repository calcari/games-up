package com.gamesUP.gamesUP.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.gamesUP.gamesUP.entities.Category;

@RepositoryRestResource
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
