package com.games.nim13.player;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(builder = ImmutableHumanPlayer.Builder.class)
public interface HumanPlayer extends Player {

    default String name() {
        return "Human Player";
    }

}
