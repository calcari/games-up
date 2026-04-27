package com.gamesUP.gamesUP.services;

import java.util.List;

import com.gamesUP.gamesUP.dto.RecommendationItemDTO;

public interface IRecommendationService {

    List<RecommendationItemDTO> getRecommendations(Long userId) throws Exception;
}
