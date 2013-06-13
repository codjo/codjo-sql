package net.codjo.sql.server;
/**
 * Interface used to notify events happening to a {@link ConnectionPool}.
 */
public interface ConnectionPoolListener {
    /**
     * Method called when a pool is created.
     */
    void poolCreated(ConnectionPool pool);


    /**
     * Method called when a pool is destroyed.
     */
    void poolDestroyed(ConnectionPool pool);
}
