package com.games.nim13.player;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Player {

    @JsonProperty("name")
    String name();

    int takeMatchSticks();
}
