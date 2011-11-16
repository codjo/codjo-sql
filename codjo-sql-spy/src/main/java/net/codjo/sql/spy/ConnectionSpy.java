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


    public ConnectionSpy(Connection connection) {
        super(connection);
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
        return new PreparedStatementSpy(super.prepareStatement(sql), sql, this);
    }


    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        addIt(sql, preparedCalls);
        preparedCallCount++;
        return new CallableStatementSpy(super.prepareCall(sql), sql, this);
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
                    (sumTime(createdStatements)
                     + sumTime(preparedStatements)
                     + sumTime(preparedCalls)) / 1000.0 + "s");
        LOGGER.info("Details :");
        LOGGER.info(" - created statements : " + sumTime(createdStatements) / 1000.0 + "s");
        LOGGER.info(" - prepared statements : " + sumTime(preparedStatements) / 1000.0 + "s");
        LOGGER.info(" - prepared calls : " + sumTime(preparedCalls) / 1000.0 + "s");
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
            collection.put(sql, new OneQuery(sql));
        }
        collection.get(sql).inc();
    }


    private void printIt(Map<String, OneQuery> sqlToQuery) {
        for (OneQuery query : sqlToQuery.values()) {
            LOGGER.info("\t " + query);
        }
    }


    private long sumTime(Map<String, OneQuery> map) {
        long sum = 0;
        for (OneQuery query : map.values()) {
            sum += query.time;
        }
        return sum;
    }


    public static class OneQuery {
        long when;
        int count;
        long time;
        String sql;


        public OneQuery(String sql) {
            this.sql = sql.replaceAll("\n", " ");
            this.when = System.currentTimeMillis();
        }


        public long getWhen() {
            return when;
        }


        public int getCount() {
            return count;
        }


        public long getTime() {
            return time;
        }


        public String getSql() {
            return sql;
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(count);
            if (time != 0) {
                builder.append(" - ").append(time / 1000.0).append("s");
            }
            return builder.append(" - ").append(sql).toString();
        }


        public void inc() {
            count++;
        }


        public void addTime(long one) {
            time += one;
        }
    }
}
