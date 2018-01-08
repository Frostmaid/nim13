package com.games.nim13.player;

import java.util.Random;

//TODO
public class ComputerPlayer implements Player {

    private Random random = new Random();

    @Override
    public String name() {
        return "Computer Player";
    }

    @Override
    public int takeMatchSticks() {
        return random.nextInt(3) + 1;
    }
}
