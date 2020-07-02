package tableslib;

import envinronment.QueryRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PPS_Table extends TTable {

    public PPS_Table( String destinationTable) {
        super( destinationTable);
    }

    @Override
    public String getInsertQueryStr(StringBuilder fieldsStr, StringBuilder valuesStr) {
        return QueryRepository.getPPSInsertQuery().replace("@values@",valuesStr).replace("@fields@",fieldsStr);
    }

    public String getDeleteLineQuery(String[] keys){
        return QueryRepository.getPPSDeleteQuery().replace("@idn@",keys[0]);
    }

    @Override
    public String getKeyExpression() {
        return "idn";
    }



    @Override
    protected String getUpdateQueryFromTable(ResultSet resultSet) throws SQLException {
        String idn=resultSet.getString("idn");
        return QueryRepository.getPPSUpdateQuery().replace("@idn@",idn);
    }

    @Override
    public String[] getKeys(String idn) {
        return new String[] {idn};
    }

    @Override
    public String getKeyName() {
        return "idn";
    }
}
