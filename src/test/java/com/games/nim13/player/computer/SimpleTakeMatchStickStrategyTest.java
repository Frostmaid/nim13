package com.games.nim13.player.computer;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DataProviderRunner.class)
public class SimpleTakeMatchStickStrategyTest {

    @Test
    @UseDataProvider("getInputAndResult")
    public void takeNumberOfMatchSticks(int currentMatchStickHeap, int takeMatchSticks) {
        SimpleTakeMatchStickStrategy strategy = new SimpleTakeMatchStickStrategy(currentMatchStickHeap);

        assertThat(strategy.takeNumberOfMatchSticks()).isEqualTo(takeMatchSticks);
    }

    @DataProvider
    public static Object[][] getInputAndResult() {
        return new Object[][]{{13, 1}, {12, 1}, {11, 1}, {10, 1}, {9, 1}, {8, 1},
                {7, 1}, {6, 1}, {5, 1}, {4, 3}, {3, 2}, {2, 1}, {1, 1}};
    }
}