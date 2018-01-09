package com.games.nim13.statemachine;

import com.games.nim13.Game;
import com.games.nim13.GameInMemoryRepository;
import com.games.nim13.ImmutableGame;
import com.games.nim13.player.ComputerPlayer;
import com.games.nim13.player.HumanPlayer;
import com.games.nim13.player.ImmutableHumanPlayer;
import com.games.nim13.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

import static com.games.nim13.statemachine.GameStatus.*;

@Component
public class GameStateMachine {

    private static final int START_NUMBER_OF_MATCH_STICKS = 13;

    private GameInMemoryRepository gameRepository;
    private Random random;

    @Autowired
    public GameStateMachine(GameInMemoryRepository gameRepository, Random random) {
        this.gameRepository = gameRepository;
        this.random = random;
    }

    public Game initGame() {
        HumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(0)
                .build();

        ComputerPlayer computerPlayer = new ComputerPlayer();

        Game game = new ImmutableGame.Builder()
                .id(UUID.randomUUID().toString())
                .matchStickHeap(START_NUMBER_OF_MATCH_STICKS)
                .humanPlayer(humanPlayer)
                .computerPlayer(computerPlayer)
                .actualPlayer(getRandomFirstPlayer(humanPlayer, computerPlayer))
                .gameStatus(EVALUATION)
                .build();

        return triggerRound(game);
    }

    public Game triggerPlayerRound(String id, int numberOfMatchSticks) {
        //TODO handle optional
        Game game = gameRepository.findById(id).get();
        if (game.gameStatus() != GameStatus.WAITING_FOR_USER_INPUT) {
            throw new IllegalArgumentException("This operation is not allowed in status: " + game.gameStatus());
        }

        HumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(numberOfMatchSticks)
                .build();

        return triggerRound(new ImmutableGame.Builder()
                .from(game)
                .actualPlayer(humanPlayer)
                .humanPlayer(humanPlayer)
                .gameStatus(TURN)
                .build());
    }

    private Game triggerRound(Game game) {
        switch (game.gameStatus()) {
            case EVALUATION:
                return triggerRound(evaluation(game));
            case WAITING_FOR_USER_INPUT:
                gameRepository.save(game);
                return game;
            case TURN:
                return triggerRound(turn(game));
            case END:
                Game end = end(game);
                gameRepository.save(end);
                return end;
            default:
                throw new IllegalStateException();
        }
    }

    private Game end(Game game) {
        //TODO last Player is not victor
        return new ImmutableGame.Builder()
                .from(game)
                .victor(game.actualPlayer())
                .build();
    }

    private Game evaluation(Game game) {
        if (game.matchStickHeap() == 0) {
            return new ImmutableGame.Builder()
                    .from(game)
                    .gameStatus(END)
                    .build();
        }

        if (game.computerPlayer().equals(game.actualPlayer())) {
            return new ImmutableGame.Builder()
                    .from(game)
                    .actualPlayer(game.humanPlayer())
                    .gameStatus(WAITING_FOR_USER_INPUT)
                    .build();
        }

        if (game.humanPlayer().equals(game.actualPlayer())) {
            return new ImmutableGame.Builder()
                    .from(game)
                    .actualPlayer(game.computerPlayer())
                    .gameStatus(TURN)
                    .build();
        }

        throw new IllegalStateException();
    }

    private Game turn(Game game) {
        return new ImmutableGame.Builder()
                .from(takeMatchSticks(game))
                .gameStatus(EVALUATION)
                .build();
    }

    //TODO maybe move to Match Stick Heap?
    private Game takeMatchSticks(Game game) {
        int numberOfMatchSticks = game.actualPlayer().takeMatchSticks();
        if (game.matchStickHeap() - numberOfMatchSticks <= 0) {
            return new ImmutableGame.Builder()
                    .from(game)
                    .matchStickHeap(0)
                    .build();
        } else {
            return new ImmutableGame.Builder()
                    .from(game)
                    .matchStickHeap(game.matchStickHeap() - numberOfMatchSticks)
                    .build();
        }
    }

    private Player getRandomFirstPlayer(HumanPlayer humanPlayer, ComputerPlayer computerPlayer) {
        if (random.nextInt(2) == 1) {
            return humanPlayer;
        }

        return computerPlayer;
    }

}
