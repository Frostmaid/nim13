package com.games.nim13.player.computer;

import com.google.common.base.Preconditions;

import java.util.Random;

import static com.games.nim13.statemachine.GameStateMachine.MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE;

public class RandomTakeMatchStickStrategy implements TakeMatchStickStrategy {

    private Random random;

    public RandomTakeMatchStickStrategy(Random random) {
        this.random = Preconditions.checkNotNull(random);
    }

    @Override
    public int takeNumberOfMatchSticks() {
        return random.nextInt(MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE) + 1;
    }
}
