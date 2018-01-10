package com.games.nim13.rest;

import com.games.nim13.Game;
import com.games.nim13.GameInMemoryRepository;
import com.games.nim13.statemachine.GameStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class GameController {

    private GameStateMachine gameStateMachine;
    private GameInMemoryRepository repository;

    @Autowired
    public GameController(GameStateMachine gameStateMachine, GameInMemoryRepository repository) {
        this.gameStateMachine = gameStateMachine;
        this.repository = repository;
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable String gameId) {
        return repository.findById(gameId)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    @PostMapping("/start_game")
    public HttpEntity<Game> startGame() {
        return ok(gameStateMachine.initGame());
    }

    @PostMapping("/take_match_sticks/{gameId}/{numberOfMatchSticks}")
    public HttpEntity<Game> takeMatchSticks(@PathVariable String gameId, @PathVariable int numberOfMatchSticks) {
        return repository.findById(gameId)
                .map(game -> ok(gameStateMachine.triggerPlayerRound(game, numberOfMatchSticks)))
                .orElse(notFound().build());
    }

}
