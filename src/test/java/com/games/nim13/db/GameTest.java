package com.games.nim13.db;

import com.games.nim13.player.ImmutableHumanPlayer;
import com.games.nim13.player.computer.ImmutableComputerPlayer;
import com.games.nim13.player.computer.RandomTakeMatchStickStrategy;
import com.games.nim13.statemachine.GameStatus;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


public class GameTest {

    @Test
    public void lastPlayerComputerPlayer() {
        ImmutableHumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(1)
                .build();

        ImmutableComputerPlayer computerPlayer = new ImmutableComputerPlayer.Builder()
                .strategy(new RandomTakeMatchStickStrategy(new Random()))
                .build();

        Game game = new ImmutableGame.Builder()
                .id("game id")
                .matchStickHeap(10)
                .gameStatus(GameStatus.TURN)
                .currentPlayer(humanPlayer)
                .humanPlayer(humanPlayer)
                .computerPlayer(computerPlayer)
                .build();

        assertThat(game.lastPlayer()).isEqualTo(computerPlayer);
    }

    @Test
    public void lastPlayerHumanPlayer() {
        ImmutableHumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(1)
                .build();

        ImmutableComputerPlayer computerPlayer = new ImmutableComputerPlayer.Builder()
                .strategy(new RandomTakeMatchStickStrategy(new Random()))
                .build();

        Game game = new ImmutableGame.Builder()
                .id("game id")
                .matchStickHeap(10)
                .gameStatus(GameStatus.TURN)
                .currentPlayer(computerPlayer)
                .humanPlayer(humanPlayer)
                .computerPlayer(computerPlayer)
                .build();

        assertThat(game.lastPlayer()).isEqualTo(humanPlayer);
    }

    @Test(expected = IllegalStateException.class)
    public void lastPlayerIllegalState() {
        ImmutableHumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(1)
                .build();

        ImmutableComputerPlayer computerPlayer = new ImmutableComputerPlayer.Builder()
                .strategy(new RandomTakeMatchStickStrategy(new Random()))
                .build();

        ImmutableComputerPlayer computerPlayer2 = new ImmutableComputerPlayer.Builder()
                .strategy(new RandomTakeMatchStickStrategy(new Random()))
                .build();

        Game game = new ImmutableGame.Builder()
                .id("game id")
                .matchStickHeap(10)
                .gameStatus(GameStatus.TURN)
                .currentPlayer(computerPlayer2)
                .humanPlayer(humanPlayer)
                .computerPlayer(computerPlayer)
                .build();

        game.lastPlayer();
    }


}