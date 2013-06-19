package net.codjo.sql.spy.stats;
/**
 *
 */
public class Statistics {
    private int count;
    private long time;
    private long minTime = -1L;
    private long maxTime = -1L;


    public int getCount() {
        return count;
    }


    public long getTime() {
        return time;
    }


    public long getMinTime() {
        return minTime;
    }


    public long getMaxTime() {
        return maxTime;
    }


    public void add(Statistics other) {
        time += other.time;
        count += other.count;
        minTime = min(minTime, other.minTime);
        maxTime = max(maxTime, other.maxTime);
    }


    public static long min(long value1, long value2) {
        if (value1 < 0) {
            return value2;
        }
        else if (value2 < 0) {
            return value1;
        }
        else {
            return Math.min(value1, value2);
        }
    }


    public static long max(long value1, long value2) {
        if (value1 < 0) {
            return value2;
        }
        else if (value2 < 0) {
            return value1;
        }
        else {
            return Math.max(value1, value2);
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(count);
        if (time != 0) {
            builder.append(" - ");
            appendSeconds(builder, "time", time);
            appendSeconds(builder, " minTime", minTime);
            appendSeconds(builder, " maxTime", maxTime);
        }
        return builder.toString();
    }


    private static void appendSeconds(StringBuilder builder, String name, long value) {
        builder.append(name).append("=").append(value / 1000.0).append("s");
    }


    public void inc() {
        count++;
    }


    public void addTime(long timeToAdd) {
        time += timeToAdd;
        minTime = min(minTime, timeToAdd);
        maxTime = max(maxTime, timeToAdd);
    }
}
