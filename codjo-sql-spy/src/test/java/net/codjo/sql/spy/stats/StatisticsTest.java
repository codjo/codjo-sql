package net.codjo.sql.spy.stats;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static net.codjo.sql.spy.stats.Statistics.max;
import static net.codjo.sql.spy.stats.Statistics.min;
import static org.junit.Assert.assertEquals;

/**
 * Test for class {@link net.codjo.sql.spy.stats.Statistics}.
 */
@RunWith(Theories.class)
public class StatisticsTest {
    @DataPoint
    public static TestData NO_VALUE = new TestData(-1, -1, 0);
    @DataPoint
    public static TestData ONE_VALUE = new TestData(1, 1, 1, 1);
    @DataPoint
    public static TestData TWO_VALUES = new TestData(2, 7, 9, 7, 2);
    @DataPoint
    public static TestData THREE_VALUES = new TestData(2, 8, 15, 8, 2, 5);


    @Theory
    public void testGetTime(TestData data) {
        Statistics stats = createOneQuery(data);
        assertEquals(data.expectedTime, stats.getTime());
    }


    @Theory
    public void testGetMinTime(TestData data) {
        Statistics stats = createOneQuery(data);
        assertEquals(data.expectedMinTime, stats.getMinTime());
    }


    @Theory
    public void testGetMaxTime(TestData data) {
        Statistics stats = createOneQuery(data);
        assertEquals(data.expectedMaxTime, stats.getMaxTime());
    }


    @Theory
    public void testGetCount(TestData data) {
        Statistics stats = createOneQuery(data);
        assertEquals(data.times.length, stats.getCount());
    }


    @Theory
    public void testAdd(TestData testData1, TestData testData2) {
        Statistics stats1 = createOneQuery(testData1);
        Statistics stats2 = createOneQuery(testData2);
        int stats1InitialCount = stats1.getCount();

        stats1.add(stats2);

        assertEquals(testData1.expectedTime + testData2.expectedTime, stats1.getTime());
        assertEquals(min(testData1.expectedMinTime, testData2.expectedMinTime), stats1.getMinTime());
        assertEquals(max(testData1.expectedMaxTime, testData2.expectedMaxTime), stats1.getMaxTime());
        assertEquals(stats1InitialCount + stats2.getCount(), stats1.getCount());
    }


    @Test
    public void testMin() {
        assertEquals(-1, min(-1, -1));
        assertEquals(3, min(3, -1));
        assertEquals(2, min(-1, 2));
        assertEquals(4, min(6, 4));
    }


    @Test
    public void testMax() {
        assertEquals(-1, max(-1, -1));
        assertEquals(3, max(3, -1));
        assertEquals(2, max(-1, 2));
        assertEquals(9, max(9, 4));
    }


    private Statistics createOneQuery(TestData data) {
        Statistics result = new Statistics();
        for (long time : data.times) {
            result.addTime(time);
            result.inc();
        }
        return result;
    }


    private static class TestData {
        final long[] times;
        final long expectedMinTime;
        final long expectedMaxTime;
        final long expectedTime;


        public TestData(long expectedMinTime, long expectedMaxTime, long expectedTime, long... times) {
            this.times = times;
            this.expectedMinTime = expectedMinTime;
            this.expectedMaxTime = expectedMaxTime;
            this.expectedTime = expectedTime;
        }
    }
}

