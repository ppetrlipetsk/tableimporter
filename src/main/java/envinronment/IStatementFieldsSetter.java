package envinronment;

import throwlib.FieldTypeError;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IStatementFieldsSetter {
    boolean setValues(PreparedStatement statement) throws SQLException, FieldTypeError;
}
