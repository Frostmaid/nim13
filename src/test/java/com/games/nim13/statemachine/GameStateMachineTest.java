package com.games.nim13.statemachine;

import com.games.nim13.Game;
import com.games.nim13.GameInMemoryRepository;
import com.games.nim13.ImmutableGame;
import com.games.nim13.player.ComputerPlayer;
import com.games.nim13.player.HumanPlayer;
import com.games.nim13.player.ImmutableHumanPlayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Random;

import static com.games.nim13.statemachine.GameStatus.TURN;
import static com.games.nim13.statemachine.GameStatus.WAITING_FOR_USER_INPUT;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameStateMachineTest {

    private static final String GAME_ID = "any game id";

    @Mock
    private Random random;

    @Mock
    private GameInMemoryRepository gameRepository;

    @InjectMocks
    private GameStateMachine testee;

    @Test
    public void initGame() {
        when(random.nextInt(2)).thenReturn(1);

        Game game = testee.initGame();

        verify(gameRepository).save(game);
        verify(random).nextInt(2);

        assertThat(game.gameStatus()).isEqualTo(WAITING_FOR_USER_INPUT);
        assertThat(game.actualPlayer()).isInstanceOf(HumanPlayer.class);
    }

    @Test
    public void triggerPlayerRound() {
        Game startingGame = getGameWithStatus(WAITING_FOR_USER_INPUT);

        Game resultGame = testee.triggerPlayerRound(startingGame, 2);

        verify(gameRepository).save(resultGame);

        assertThat(resultGame.gameStatus()).isEqualTo(WAITING_FOR_USER_INPUT);
        assertThat(resultGame.actualPlayer()).isInstanceOf(HumanPlayer.class);
        //TODO
        // assertThat(game.matchStickHeap()).isEqualTo(10);
    }

    @Test
    public void triggerPlayerRoundInWrongState() {
        Game startingGame = getGameWithStatus(TURN);

        try {
            testee.triggerPlayerRound(startingGame, 2);
            fail();
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("This operation is not allowed in status: " + GameStatus.TURN);
        }
    }

    private static Game getGameWithStatus(GameStatus gameStatus) {
        ImmutableHumanPlayer humanPlayer = new ImmutableHumanPlayer.Builder()
                .takeMatchSticks(0)
                .build();

        return new ImmutableGame.Builder()
                .id(GAME_ID)
                .actualPlayer(humanPlayer)
                .humanPlayer(humanPlayer)
                .computerPlayer(new ComputerPlayer())
                .gameStatus(gameStatus)
                .matchStickHeap(10)
                .build();
    }
}