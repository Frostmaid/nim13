package com.games.nim13;

import com.games.nim13.player.ImmutableHumanPlayer;
import com.games.nim13.player.computer.ImmutableComputerPlayer;
import com.games.nim13.player.computer.RandomTakeMatchStickStrategy;
import com.games.nim13.statemachine.GameStatus;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class GameInMemoryRepositoryTest {

    private GameInMemoryRepository testee = new GameInMemoryRepository();

    @Test
    public void getByIdSuccess() {
        Game game = getGameWithId("id");

        testee.save(game);

        Optional<Game> gameGetById = testee.findById(game.id());
        assertThat(gameGetById.isPresent()).isTrue();
        assertThat(gameGetById.get()).isEqualTo(game);
    }

    @Test
    @UseDataProvider("getByIdData")
    public void getByIdNotFound(String id) {
        Game game = getGameWithId("id");

        testee.save(game);

        Optional<Game> gameGetById = testee.findById(id);
        assertThat(gameGetById.isPresent()).isFalse();
    }


    @DataProvider
    public static Object[] getByIdData() {
        return new Object[]{"", "  ", "-1", "invalid"};
    }

    private static Game getGameWithId(String id) {
        ImmutableHumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(1)
                .build();

        return new ImmutableGame.Builder()
                .id(id)
                .matchStickHeap(10)
                .gameStatus(GameStatus.TURN)
                .actualPlayer(humanPlayer)
                .humanPlayer(humanPlayer)
                .computerPlayer(new ImmutableComputerPlayer.Builder()
                        .strategy(new RandomTakeMatchStickStrategy(new Random()))
                        .build())
                .build();
    }
}