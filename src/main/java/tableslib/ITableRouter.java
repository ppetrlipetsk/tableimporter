package tableslib;

import java.sql.ResultSet;

public interface ITableRouter {
    boolean updateRecord(ResultSet resultSet) throws Exception;

    void insertRecord(ResultSet resultSet) throws Exception;

    boolean deleteLine(String[] keys) throws Exception;

}
