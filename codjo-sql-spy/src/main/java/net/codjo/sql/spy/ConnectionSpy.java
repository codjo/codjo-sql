package net.codjo.sql.spy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.codjo.util.time.SimpleStatistics;
import net.codjo.util.time.Statistics;
import net.codjo.util.time.StatisticsOwner;
import net.codjo.util.time.SystemTimeSource;
import net.codjo.util.time.TimeSource;
import org.apache.log4j.Logger;

public class ConnectionSpy extends ConnectionDecorator {
    private static final Logger LOGGER = Logger.getLogger(ConnectionSpy.class);
    private int createdStatementCount;
    private int preparedStatementCount;
    private int preparedCallCount;
    private boolean displayQuery = false;
    private Map<String, OneQuery> createdStatements = new TreeMap<String, OneQuery>();
    private Map<String, OneQuery> preparedStatements = new TreeMap<String, OneQuery>();
    private Map<String, OneQuery> preparedCalls = new TreeMap<String, OneQuery>();
    final TimeSource timeSource;


    public ConnectionSpy(Connection connection) {
        this(connection, null);
    }


    public ConnectionSpy(Connection connection, TimeSource timeSource) {
        super(connection);

        this.timeSource = SystemTimeSource.defaultIfNull(timeSource);
    }


    @Override
    public Statement createStatement() throws SQLException {
        createdStatementCount++;
        return new StatementSpy(super.createStatement(), this);
    }


    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        addIt(sql, preparedStatements);
        preparedStatementCount++;
        return new PreparedStatementSpy(super.prepareStatement(sql), sql, this, timeSource);
    }


    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        addIt(sql, preparedCalls);
        preparedCallCount++;
        return new CallableStatementSpy(super.prepareCall(sql), sql, this, timeSource);
    }


    public List<OneQuery> getAllQueries() {
        List<OneQuery> queries = new ArrayList<OneQuery>();
        queries.addAll(createdStatements.values());
        queries.addAll(preparedStatements.values());
        queries.addAll(preparedCalls.values());
        return queries;
    }


    public void addStatement(String sql) {
        addIt(sql, createdStatements);
    }


    public Map<String, OneQuery> getCreatedStatements() {
        return createdStatements;
    }


    public OneQuery getStatement(String sql) {
        return createdStatements.get(sql);
    }


    public Map<String, OneQuery> getPreparedStatements() {
        return preparedStatements;
    }


    public OneQuery getPreparedStatement(String sql) {
        return preparedStatements.get(sql);
    }


    public Map<String, OneQuery> getPreparedCalls() {
        return preparedCalls;
    }


    public OneQuery getPreparedCall(String sql) {
        return preparedCalls.get(sql);
    }


    public boolean shouldDisplayQuery() {
        return displayQuery;
    }


    public void setDisplayQuery(boolean displayQuery) {
        this.displayQuery = displayQuery;
    }


    public void printAudit() {
        LOGGER.info("---------------------------------------------- ## PRINT AUDIT");
        LOGGER.info("Calls : " + (createdStatementCount + preparedStatementCount + preparedCallCount));
        LOGGER.info("Details :");
        LOGGER.info(" - created statements : " + createdStatementCount);
        LOGGER.info(" - prepared statements : " + preparedStatementCount);
        LOGGER.info(" - prepared calls : " + preparedCallCount);
        LOGGER.info("");
        LOGGER.info("Execution time : " +
                    (sumTotalTime(createdStatements)
                     + sumTotalTime(preparedStatements)
                     + sumTotalTime(preparedCalls)) / 1000.0 + "s");
        LOGGER.info("Details :");
        LOGGER.info(" - created statements : " + sumTotalTime(createdStatements) / 1000.0 + "s");
        LOGGER.info(" - prepared statements : " + sumTotalTime(preparedStatements) / 1000.0 + "s");
        LOGGER.info(" - prepared calls : " + sumTotalTime(preparedCalls) / 1000.0 + "s");
        LOGGER.info("");
        LOGGER.info("Queries :");
        LOGGER.info(" - created statements : " + createdStatements.size());
        printIt(createdStatements);
        LOGGER.info(" - prepared statements : " + preparedStatements.size());
        printIt(preparedStatements);
        LOGGER.info(" - prepared calls : " + preparedCalls.size());
        printIt(preparedCalls);
    }


    private void addIt(String sql, Map<String, OneQuery> collection) {
        if (!collection.containsKey(sql)) {
            collection.put(sql, new OneQuery(sql, timeSource));
        }
    }


    private void printIt(Map<String, OneQuery> sqlToQuery) {
        for (OneQuery query : sqlToQuery.values()) {
            LOGGER.info("\t " + query);
        }
    }


    private long sumTotalTime(Map<String, OneQuery> map) {
        Statistics stats = SimpleStatistics.aggregate(map.values());
        return stats.getTotalTime();
    }


    public static class OneQuery implements StatisticsOwner {
        long when;
        private final SimpleStatistics statistics;
        String sql;
        final TimeSource timeSource;


        public OneQuery(String sql, TimeSource timeSource) {
            this(sql, timeSource, new SimpleStatistics());
        }


        private OneQuery(String sql, TimeSource timeSource, SimpleStatistics statistics) {
            this.sql = sql.replaceAll("\n", " ");
            this.timeSource = SystemTimeSource.defaultIfNull(timeSource);
            this.when = this.timeSource.getTime();
            this.statistics = statistics;
        }


        public long getWhen() {
            return when;
        }


        public int getCount() {
            return statistics.getCount();
        }


        public long getTotalTime() {
            return statistics.getTotalTime();
        }


        public long getMinTime() {
            return statistics.getMinTime();
        }


        public long getMaxTime() {
            return statistics.getMaxTime();
        }


        public String getSql() {
            return sql;
        }


        public OneQuery aggregate(OneQuery other) {
            SimpleStatistics stats = new SimpleStatistics();
            stats.add(statistics);
            stats.add(other.statistics);
            return new OneQuery(sql, timeSource, stats);
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("Statistics[");
            builder.append(statistics).append("] - ").append(sql);
            return builder.toString();
        }


        public void addTime(long timeToAdd) {
            statistics.addTime(timeToAdd);
        }


        public Statistics getStatistics() {
            return statistics;
        }
    }
}
