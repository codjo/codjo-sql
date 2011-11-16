package net.codjo.sql.server;
import java.sql.SQLException;
/**
 *
 */
public class JdbcServerException extends SQLException {
    public JdbcServerException(String message) {
        super(message);
    }


    public JdbcServerException(Throwable cause) {
        super(cause.getLocalizedMessage());
        initCause(cause);
    }
}
