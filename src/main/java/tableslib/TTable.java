package tableslib;

import com.ppsdevelopment.jdbcprocessor.DataBaseConnector;
import com.ppsdevelopment.jdbcprocessor.DataBaseProcessor;
import com.ppsdevelopment.tmcprocessor.tmctypeslib.FieldType;
import com.ppsdevelopment.tmcprocessor.tmctypeslib.FieldTypeDefines;

import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class TTable  implements ITableRouter {
    private final String destinationTable;
    private HashMap<String, FieldType> aliases=new HashMap<>();

    public void setAliases(HashMap<String, FieldType> aliases) {
        this.aliases = aliases;
    }

    public String getDestinationTable() {
        return destinationTable;
    }

    TTable(String destinationTable) {
      //  this.sourceTable = sourceTable;
        this.destinationTable=destinationTable;
    }

    private void generateInsertQueryArguments(ResultSet resultSet, StringBuilder fieldsStr, StringBuilder valuesStr) throws SQLException {
        for (Map.Entry<String, FieldType> alias: aliases.entrySet()){
            String fieldName=alias.getKey();
            FieldType fieldType=alias.getValue();
            if (fieldsStr.length()>0)fieldsStr.append(",");
            fieldsStr.append(fieldName);
            if (valuesStr.length()>0)valuesStr.append(",");
            try {
                valuesStr.append(getFieldValueStr(fieldType,resultSet.getString(fieldName)));
            } catch (SQLException e) {
                throw new SQLException("Ошибка генерирования выражения вставки строки. FieldName="+fieldName+"\n"+e.toString());
            }
        }
    }

    private String getUpdateQueryValues(ResultSet resultSet) throws SQLException {

        StringBuilder valuesStr=new StringBuilder();

        for (Map.Entry<String, FieldType> alias: aliases.entrySet()){
            String fieldName=alias.getKey();
            FieldType fieldType=alias.getValue();
            if (valuesStr.length()>0){
                valuesStr.append(",");
            }
            valuesStr.append(fieldName).append("=");
                valuesStr.append(getFieldValueStr(fieldType,resultSet.getString(fieldName)));
        }
        return valuesStr.toString();
    }

    private String getFieldValueStr(FieldType fieldType, String valueStr){
        String mask= FieldTypeDefines.getFieldMaskStrByType(fieldType);
        if (valueStr==null)
            return "null";
        else
        return mask.replace("@value@", valueStr);
    }

    @Override
    public boolean updateRecord(ResultSet resultSet) throws Exception {
        String values;
        try {
            values = getUpdateQueryValues(resultSet);
            String query=getUpdateQueryFromTable(resultSet).replace("@values@",values);
            DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());
            dp.exec(query);
        } catch (SQLException | ConnectException e) {
            throw new Exception("Ошибка обновления строки."+e.getMessage());
        }
        return true;
    }

    @Override
    public void insertRecord(ResultSet resultSet) throws Exception {
        StringBuilder fieldsStr=new StringBuilder();
        StringBuilder valuesStr=new StringBuilder();
        generateInsertQueryArguments(resultSet, fieldsStr, valuesStr);
        try {
            DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());

            String query = getInsertQueryStr(fieldsStr, valuesStr);
            dp.exec(query);
        } catch (SQLException | ConnectException e) {
            throw new Exception("Ошибка добавления строки."+e.getMessage());
        }
    }

    @Override
    public boolean deleteLine(String[] keys) throws Exception {
        String query=getDeleteLineQuery(keys);
        try {
            DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());
            dp.exec(query);
        } catch (SQLException | ConnectException e) {
            e.printStackTrace();
            throw new Exception("Ошибка удаления строки."+e.getMessage());
        }
        return true;
    }

    abstract String getInsertQueryStr(StringBuilder fieldsStr, StringBuilder valuesStr);

    abstract String getDeleteLineQuery(String[] keys);

    public abstract String getKeyExpression();

    protected abstract String getUpdateQueryFromTable(ResultSet resultSet) throws SQLException;

    public abstract String[] getKeys(String idn);

    public abstract String getKeyName();
}
