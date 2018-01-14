package com.games.nim13.player.computer;

import static com.games.nim13.statemachine.GameStateMachine.MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE;
import static com.games.nim13.statemachine.GameStateMachine.MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE;

public class SimpleTakeMatchStickStrategy implements TakeMatchStickStrategy {

    private int currentMatchStickHeap;

    public SimpleTakeMatchStickStrategy(int currentMatchStickHeap) {
        this.currentMatchStickHeap = currentMatchStickHeap;
    }

    @Override
    public int takeNumberOfMatchSticks() {
        if (currentMatchStickHeap == MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE) {
            return MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE;
        }
        if (currentMatchStickHeap <= MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE + MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE) {
            return currentMatchStickHeap - MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE;
        }
        return MIN_NUMBER_OF_MATCH_STICKS_TO_TAKE;
    }
}
