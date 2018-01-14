package com.games.nim13.statemachine;

import com.games.nim13.db.Game;
import com.games.nim13.db.GameInMemoryRepository;
import com.games.nim13.db.ImmutableGame;
import com.games.nim13.player.HumanPlayer;
import com.games.nim13.player.ImmutableHumanPlayer;
import com.games.nim13.player.Player;
import com.games.nim13.player.computer.ComputerPlayer;
import com.games.nim13.player.computer.ImmutableComputerPlayer;
import com.games.nim13.player.computer.SimpleTakeMatchStickStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

import static com.games.nim13.statemachine.GameStatus.*;

@Component
public class GameStateMachine {

    public static final int START_NUMBER_OF_MATCH_STICKS = 13;

    public static final int MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE = 3;

    public static final int MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE = 1;

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

        ComputerPlayer computerPlayer = new ImmutableComputerPlayer.Builder()
                .strategy(new SimpleTakeMatchStickStrategy(START_NUMBER_OF_MATCH_STICKS))
                .build();

        return triggerRound(new ImmutableGame.Builder()
                .id(UUID.randomUUID().toString())
                .matchStickHeap(START_NUMBER_OF_MATCH_STICKS)
                .humanPlayer(humanPlayer)
                .computerPlayer(computerPlayer)
                .currentPlayer(getRandomFirstPlayer(humanPlayer, computerPlayer))
                .gameStatus(EVALUATION)
                .build());
    }

    public Game triggerPlayerRound(Game game, int numberOfMatchSticks) {
        if (game.gameStatus() != GameStatus.WAITING_FOR_USER_INPUT) {
            throw new IllegalArgumentException("This operation is not allowed in status: " + game.gameStatus());
        }

        HumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(numberOfMatchSticks)
                .build();

        return triggerRound(new ImmutableGame.Builder()
                .from(game)
                .currentPlayer(humanPlayer)
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

    private static Game end(Game game) {
        return new ImmutableGame.Builder()
                .from(game)
                .victor(game.lastPlayer())
                .build();
    }

    private static Game turn(Game game) {
        return new ImmutableGame.Builder()
                .from(takeMatchSticks(game))
                .gameStatus(EVALUATION)
                .build();
    }

    private static Game evaluation(Game game) {
        if (game.matchStickHeap() == 0) {
            return new ImmutableGame.Builder()
                    .from(game)
                    .gameStatus(END)
                    .build();
        }

        return switchCurrentPlayer(game);
    }

    private static Game switchCurrentPlayer(Game game) {
        if (game.computerPlayer().equals(game.currentPlayer())) {
            return new ImmutableGame.Builder()
                    .from(game)
                    .currentPlayer(game.humanPlayer())
                    .gameStatus(WAITING_FOR_USER_INPUT)
                    .build();
        }

        if (game.humanPlayer().equals(game.currentPlayer())) {
            ImmutableComputerPlayer computerPlayer = new ImmutableComputerPlayer.Builder()
                    .strategy(new SimpleTakeMatchStickStrategy(game.matchStickHeap()))
                    .build();

            return new ImmutableGame.Builder()
                    .from(game)
                    .computerPlayer(computerPlayer)
                    .currentPlayer(computerPlayer)
                    .gameStatus(TURN)
                    .build();
        }

        throw new IllegalStateException();
    }

    private static Game takeMatchSticks(Game game) {
        int numberOfMatchSticks = game.currentPlayer().takeMatchSticks();

        if (invalidNumberOfMatchSticks(numberOfMatchSticks)) {
            throw new IllegalArgumentException("Not allowed number of match sticks. You can only choose 1 to 3 match sticks.");
        }

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

    private static boolean invalidNumberOfMatchSticks(int numberOfMatchSticks) {
        return numberOfMatchSticks < MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE
                || numberOfMatchSticks > MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE;
    }

    private Player getRandomFirstPlayer(HumanPlayer humanPlayer, ComputerPlayer computerPlayer) {
        if (random.nextInt(2) == 1) {
            return humanPlayer;
        }

        return computerPlayer;
    }

}
