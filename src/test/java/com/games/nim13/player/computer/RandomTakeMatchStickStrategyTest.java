package com.games.nim13.player.computer;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static com.games.nim13.statemachine.GameStateMachine.MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class RandomTakeMatchStickStrategyTest {

    @Mock
    private Random random;

    @InjectMocks
    private RandomTakeMatchStickStrategy testee;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @UseDataProvider("getInputAndResult")
    public void takeNumberOfMatchSticks(int randomResult, int takeNumberOfMatchSticks) {
        when(random.nextInt(MAX_NUMBER_OF_MATCH_STICKS_TO_TAKE)).thenReturn(randomResult);

        assertThat(testee.takeNumberOfMatchSticks()).isEqualTo(takeNumberOfMatchSticks);
    }

    @DataProvider
    public static Object[][] getInputAndResult() {
        return new Object[][]{{2, 3}, {1, 2}, {0, 1}};
    }
}