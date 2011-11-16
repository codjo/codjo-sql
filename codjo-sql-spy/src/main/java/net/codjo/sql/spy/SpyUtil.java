package net.codjo.sql.spy;
import java.sql.SQLException;

public class SpyUtil {
    SpyUtil() {
    }


    public int countParameters(String query) {
        int nbParams = 0;
        int lastIndex = query.indexOf('?');
        while (lastIndex >= 0) {
            lastIndex = query.indexOf('?', lastIndex + 1);
            nbParams++;
        }
        return nbParams;
    }


    public void setValue(String[] parameters, int parameterIndex, Object val) {
        if (val == null) {
            parameters[parameterIndex - 1] = "NULL";
        }
        else {
            parameters[parameterIndex - 1] = val.toString();
        }
    }


    public String builtQuery(String[] parameters, String query) throws SQLException {
        if (parameters == null) {
            return query;
        }
        StringBuilder builtQuery = new StringBuilder(query);
        for (String parameter : parameters) {
            int idx = builtQuery.toString().indexOf("?");
            if (parameter == null) {
                builtQuery.replace(idx, idx + 1, "UNSET_PARAMETER");
            }
            else {
                builtQuery.replace(idx, idx + 1, parameter);
            }
        }
        return builtQuery.toString();
    }
}
