package com.games.nim13.rest;

import com.games.nim13.db.Game;
import com.games.nim13.db.GameInMemoryRepository;
import com.games.nim13.statemachine.GameStateMachine;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "View the game for a specific id.", response = Game.class)
    public ResponseEntity<Game> getGame(@PathVariable String gameId) {
        return repository.findById(gameId)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    @PostMapping("/start_game")
    @ApiOperation(value = "Start a new game.", response = Game.class)
    public HttpEntity<Game> startGame() {
        return ok(gameStateMachine.initGame());
    }

    @PostMapping("/take_match_sticks/{gameId}/{numberOfMatchSticks}")
    @ApiOperation(value = "Perform a turn for a game with a specific id. You can take 1 to 3 match sticks.", response = Game.class)
    public HttpEntity<Game> takeMatchSticks(@PathVariable String gameId, @PathVariable int numberOfMatchSticks) {
        return repository.findById(gameId)
                .map(game -> ok(gameStateMachine.triggerPlayerRound(game, numberOfMatchSticks)))
                .orElse(notFound().build());
    }

}
