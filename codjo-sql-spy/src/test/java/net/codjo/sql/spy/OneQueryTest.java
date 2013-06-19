package net.codjo.sql.spy;
import junit.framework.TestCase;
import net.codjo.sql.spy.ConnectionSpy.OneQuery;
import net.codjo.util.time.MockTimeSource;
import net.codjo.util.time.TimeSource;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.sql.spy.stats.Statistics.max;
import static net.codjo.sql.spy.stats.Statistics.min;

/**
 * Test for class {@link net.codjo.sql.spy.ConnectionSpy.OneQuery}.
 */
public class OneQueryTest extends TestCase {
    private TimeSource timeSource;


    @Before
    public void setUp() {
        timeSource = new MockTimeSource();
    }


    @Test
    public void testConstructor_timeSourceNull() {
        new OneQuery("a query", null);
    }


    @Test
    public void testGetTime() {
        OneQuery oneQuery = new OneQuery("query", timeSource);
        assertEquals(0, oneQuery.getTime());
    }


    @Test
    public void testAggregate() {
        OneQuery oneQuery1 = new OneQuery("query1", timeSource);
        OneQuery oneQuery2 = new OneQuery("query2", timeSource);

        OneQuery result = oneQuery1.aggregate(oneQuery2);

        assertNotNull(result);
        assertNotSame(result, oneQuery1);
        assertNotSame(result, oneQuery2);

        assertEquals(oneQuery1.getTime() + oneQuery2.getTime(), result.getTime());
        assertEquals(min(oneQuery1.getMinTime(), oneQuery2.getMinTime()), result.getMinTime());
        assertEquals(max(oneQuery1.getMaxTime(), oneQuery2.getMaxTime()), result.getMaxTime());
        assertEquals(oneQuery1.getCount() + oneQuery2.getCount(), result.getCount());
    }
}
