package com.games.nim13.player.computer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.games.nim13.player.Player;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(builder = ImmutableComputerPlayer.Builder.class)
public interface ComputerPlayer extends Player {

    TakeMatchStickStrategy strategy();

    default String name() {
        return "Computer Player";
    }

    @Override
    default int takeMatchSticks() {
        return strategy().takeNumberOfMatchSticks();
    }
}
