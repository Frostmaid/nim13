package com.games.nim13.player.computer;

import com.google.common.base.Preconditions;

import java.util.Random;

public class RandomTakeMatchStickStrategy implements TakeMatchStickStrategy {

    private Random random;

    public RandomTakeMatchStickStrategy(Random random) {
        this.random = Preconditions.checkNotNull(random);
    }

    @Override
    public int takeNumberOfMatchSticks() {
        return random.nextInt(3) + 1;
    }
}
