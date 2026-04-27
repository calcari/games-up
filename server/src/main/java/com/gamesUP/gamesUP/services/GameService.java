package com.gamesUP.gamesUP.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gamesUP.gamesUP.dto.AvisCreateBodyDTO;
import com.gamesUP.gamesUP.dto.GameBodyDTO;
import com.gamesUP.gamesUP.entities.Avis;
import com.gamesUP.gamesUP.entities.Game;
import com.gamesUP.gamesUP.errors.EntityNotFoundException;
import com.gamesUP.gamesUP.repositories.AvisRepository;
import com.gamesUP.gamesUP.repositories.CategoryRepository;
import com.gamesUP.gamesUP.repositories.ContactRepository;
import com.gamesUP.gamesUP.repositories.GameRepository;
import com.gamesUP.gamesUP.repositories.GenreRepository;
import com.gamesUP.gamesUP.repositories.UserRepository;

@Service
public class GameService implements IGameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AvisRepository avisRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Game> getAllGamesPopulated() {
        return gameRepository.findAllPopulated();
    }

    public Game getGameByIdPopulated(Long id) {
        return gameRepository.findByIdPopulated(id).orElseThrow(() -> new EntityNotFoundException("Game", id));
    }

    public Game createGame(GameBodyDTO request) {
        var author = contactRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Contact (Author)", request.getAuthorId()));
        var publisher = contactRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new EntityNotFoundException("Contact (Publisher)", request.getPublisherId()));

        var genre = genreRepository.findById(request.getGenreId());
        var category = categoryRepository.findById(request.getCategoryId());

        var game = new Game();
        game.setName(request.getName());
        game.setAuthor(author);
        game.setPublisher(publisher);
        game.setGenre(genre.orElse(null));
        game.setCategory(category.orElse(null));
        game.setNumEdition(request.getNumEdition());
        game.setStock(request.getStock());
        game.setPrice(request.getPrice());
        return gameRepository.save(game);
    }

    public Game updateGame(Long id, GameBodyDTO request) {
        var game = gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Game", id));
        var author = contactRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Contact (Author)", request.getAuthorId()));
        var publisher = contactRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new EntityNotFoundException("Contact (Publisher)", request.getPublisherId()));

        var genre = genreRepository.findById(request.getGenreId());
        var category = categoryRepository.findById(request.getCategoryId());

        game.setName(request.getName());
        game.setAuthor(author);
        game.setPublisher(publisher);
        game.setGenre(genre.orElse(null));
        game.setCategory(category.orElse(null));
        game.setNumEdition(request.getNumEdition());
        game.setStock(request.getStock());
        game.setPrice(request.getPrice());
        return gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new EntityNotFoundException("Game", id);
        }
        this.gameRepository.deleteById(id);
    }

    public List<Avis> getGameAvis(Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new EntityNotFoundException("Game", gameId);
        }
        return this.avisRepository.findByGameId(gameId);
    }

    public Avis createAvis(Long gameId, Long writerId, AvisCreateBodyDTO body) {
        var game = gameRepository.findById(gameId).orElseThrow(() -> new EntityNotFoundException("Game", gameId));
        var writer = userRepository.findById(writerId).orElseThrow(() -> new EntityNotFoundException("User", writerId));

        var avis = new Avis();
        avis.setGame(game);
        avis.setWriter(writer);
        avis.setComment(body.getComment());
        avis.setNote(body.getNote());
        avis = this.avisRepository.save(avis);

        return avis;
    }
}
