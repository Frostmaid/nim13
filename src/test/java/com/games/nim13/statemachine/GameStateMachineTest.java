package com.games.nim13.statemachine;

import com.games.nim13.Game;
import com.games.nim13.GameInMemoryRepository;
import com.games.nim13.ImmutableGame;
import com.games.nim13.player.HumanPlayer;
import com.games.nim13.player.ImmutableHumanPlayer;
import com.games.nim13.player.computer.ComputerPlayer;
import com.games.nim13.player.computer.ImmutableComputerPlayer;
import com.games.nim13.player.computer.RandomTakeMatchStickStrategy;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static com.games.nim13.statemachine.GameStatus.*;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class GameStateMachineTest {

    private static final String GAME_ID = "any game id";

    @Mock
    private Random random;

    @Mock
    private GameInMemoryRepository gameRepository;

    @InjectMocks
    private GameStateMachine testee;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void initGame() {
        int computerTakeMatchSticks = 2;
        when(random.nextInt(2)).thenReturn(1);
        when(random.nextInt(3)).thenReturn(computerTakeMatchSticks - 1);

        Game game = testee.initGame();

        verify(gameRepository).save(game);
        verify(random).nextInt(2);

        assertThat(game.gameStatus()).isEqualTo(WAITING_FOR_USER_INPUT);
        assertThat(game.actualPlayer()).isInstanceOf(HumanPlayer.class);
        assertThat(game.matchStickHeap()).isEqualTo(13 - computerTakeMatchSticks);
    }

    @Test
    public void triggerPlayerRound() {
        int startMatchStickHeap = 10;
        int humanTakeMatchSticks = 2;
        int computerTakeMatchSticks = 2;

        Game startingGame = getGameWithStatus(WAITING_FOR_USER_INPUT, startMatchStickHeap);
        when(random.nextInt(3)).thenReturn(computerTakeMatchSticks - 1);

        Game resultGame = testee.triggerPlayerRound(startingGame, humanTakeMatchSticks);

        verify(gameRepository).save(resultGame);
        verify(random).nextInt(3);

        assertThat(resultGame.gameStatus()).isEqualTo(WAITING_FOR_USER_INPUT);
        assertThat(resultGame.actualPlayer()).isInstanceOf(HumanPlayer.class);
        assertThat(resultGame.matchStickHeap()).isEqualTo(startMatchStickHeap - humanTakeMatchSticks - computerTakeMatchSticks);
    }

    @Test
    @UseDataProvider("getInvalidMatchSticks")
    public void triggerPlayerRoundWithTooManyMatchSticks(int humanTakeMatchSticks) {
        int startMatchStickHeap = 10;

        Game startingGame = getGameWithStatus(WAITING_FOR_USER_INPUT, startMatchStickHeap);

        try {
            testee.triggerPlayerRound(startingGame, humanTakeMatchSticks);
            fail();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("Not allowed number of match sticks. You can only choose 1 to 3 match sticks.");
        }
    }

    @DataProvider
    public static Object[] getInvalidMatchSticks() {
        return new Object[]{0, -1, 4};
    }

    @Test
    public void triggerPlayerRoundInWrongState() {
        Game startingGame = getGameWithStatus(TURN, 10);

        try {
            testee.triggerPlayerRound(startingGame, 2);
            fail();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("This operation is not allowed in status: " + GameStatus.TURN);
        }
    }

    @Test
    public void humanEndsGame() {
        int startMatchStickHeap = 1;
        int humanTakeMatchSticks = 1;

        Game startingGame = getGameWithStatus(WAITING_FOR_USER_INPUT, startMatchStickHeap);

        Game resultGame = testee.triggerPlayerRound(startingGame, humanTakeMatchSticks);

        verify(gameRepository).save(resultGame);

        assertThat(resultGame.gameStatus()).isEqualTo(END);
        assertThat(resultGame.actualPlayer()).isInstanceOf(HumanPlayer.class);
        assertThat(resultGame.victor().isPresent()).isTrue();
        assertThat(resultGame.victor().get()).isInstanceOf(ComputerPlayer.class);
        assertThat(resultGame.matchStickHeap()).isEqualTo(0);
    }

    @Test
    public void computerEndsGame() {
        int startMatchStickHeap = 2;
        int humanTakeMatchSticks = 1;
        int computerTakeMatchSticks = 2;

        Game startingGame = getGameWithStatus(WAITING_FOR_USER_INPUT, startMatchStickHeap);
        when(random.nextInt(3)).thenReturn(computerTakeMatchSticks - 1);

        Game resultGame = testee.triggerPlayerRound(startingGame, humanTakeMatchSticks);

        verify(gameRepository).save(resultGame);
        verify(random).nextInt(3);

        assertThat(resultGame.gameStatus()).isEqualTo(END);
        assertThat(resultGame.actualPlayer()).isInstanceOf(ComputerPlayer.class);
        assertThat(resultGame.victor().isPresent()).isTrue();
        assertThat(resultGame.victor().get()).isInstanceOf(HumanPlayer.class);
        assertThat(resultGame.matchStickHeap()).isEqualTo(0);
    }

    private Game getGameWithStatus(GameStatus gameStatus, int matchStickHeap) {
        ImmutableHumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(1)
                .build();

        return new ImmutableGame.Builder()
                .id(GAME_ID)
                .actualPlayer(humanPlayer)
                .humanPlayer(humanPlayer)
                .computerPlayer(new ImmutableComputerPlayer.Builder()
                        .strategy(new RandomTakeMatchStickStrategy(random))
                        .build())
                .gameStatus(gameStatus)
                .matchStickHeap(matchStickHeap)
                .build();
    }
}