package com.games.nim13.db;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.games.nim13.player.Player;
import com.games.nim13.statemachine.GameStatus;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(builder = ImmutableGame.Builder.class)
public interface Game {

    String id();

    GameStatus gameStatus();

    int matchStickHeap();

    Player humanPlayer();

    Player computerPlayer();

    Player currentPlayer();

    Optional<Player> victor();

    default Player lastPlayer() {
        if (currentPlayer().equals(humanPlayer())) {
            return computerPlayer();
        }

        if (currentPlayer().equals(computerPlayer())) {
            return humanPlayer();
        }

        throw new IllegalStateException();
    }

}
