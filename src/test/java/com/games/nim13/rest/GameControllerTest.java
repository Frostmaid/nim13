package com.games.nim13.rest;

import com.games.nim13.db.Game;
import com.games.nim13.db.GameInMemoryRepository;
import com.games.nim13.db.ImmutableGame;
import com.games.nim13.player.ImmutableHumanPlayer;
import com.games.nim13.player.computer.ImmutableComputerPlayer;
import com.games.nim13.player.computer.RandomTakeMatchStickStrategy;
import com.games.nim13.statemachine.GameStateMachine;
import com.games.nim13.statemachine.GameStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @MockBean
    private GameStateMachine gameStateMachine;

    @MockBean
    private GameInMemoryRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void startGameOk() throws Exception {
        String gameId = "1-2-3";
        Game game = getGameWithId(gameId);
        when(gameStateMachine.initGame()).thenReturn(game);

        mockMvc.perform(post("/start_game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId)))
                .andExpect(jsonPath("$.gameStatus", is(game.gameStatus().toString())))
                .andExpect(jsonPath("$.matchStickHeap", is(game.matchStickHeap())))
                .andExpect(jsonPath("$.humanPlayer.name", is(game.humanPlayer().name())))
                .andExpect(jsonPath("$.computerPlayer.name", is(game.computerPlayer().name())))
                .andExpect(jsonPath("$.victor.name", is(game.humanPlayer().name())));

        verify(gameStateMachine).initGame();
    }

    @Test
    public void takeMatchSticksOk() throws Exception {
        String gameId = "1-2-3";
        int numberOfMatchSticks = 2;
        Game game = getGameWithId(gameId);
        when(repository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameStateMachine.triggerPlayerRound(game, numberOfMatchSticks)).thenReturn(game);

        mockMvc.perform(post("/take_match_sticks/" + gameId + "/" + numberOfMatchSticks))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId)))
                .andExpect(jsonPath("$.gameStatus", is(game.gameStatus().toString())))
                .andExpect(jsonPath("$.matchStickHeap", is(game.matchStickHeap())))
                .andExpect(jsonPath("$.humanPlayer.name", is(game.humanPlayer().name())))
                .andExpect(jsonPath("$.computerPlayer.name", is(game.computerPlayer().name())))
                .andExpect(jsonPath("$.victor.name", is(game.humanPlayer().name())));

        verify(repository).findById(gameId);
        verify(gameStateMachine).triggerPlayerRound(game, numberOfMatchSticks);
    }

    @Test
    public void takeMatchSticksNotFound() throws Exception {
        String gameId = "1-2-3";
        int numberOfMatchSticks = 2;

        when(repository.findById(gameId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/take_match_sticks/" + gameId + "/" + numberOfMatchSticks))
                .andExpect(status().isNotFound());

        verify(repository).findById(gameId);
    }

    @Test
    public void getGameOk() throws Exception {
        String gameId = "1-2-3";
        Game game = getGameWithId(gameId);
        when(repository.findById(gameId)).thenReturn(Optional.of(game));

        mockMvc.perform(get("/game/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId)))
                .andExpect(jsonPath("$.gameStatus", is(game.gameStatus().toString())))
                .andExpect(jsonPath("$.matchStickHeap", is(game.matchStickHeap())))
                .andExpect(jsonPath("$.humanPlayer.name", is(game.humanPlayer().name())))
                .andExpect(jsonPath("$.computerPlayer.name", is(game.computerPlayer().name())))
                .andExpect(jsonPath("$.victor.name", is(game.humanPlayer().name())));

        verify(repository).findById(gameId);
    }

    @Test
    public void getGameNotFound() throws Exception {
        String gameId = "1-2-3";
        when(repository.findById(gameId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/game/" + gameId))
                .andExpect(status().isNotFound());
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
                .victor(humanPlayer)
                .computerPlayer(new ImmutableComputerPlayer.Builder()
                        .strategy(new RandomTakeMatchStickStrategy(new Random()))
                        .build())
                .build();
    }
}