package tableslib;

import envinronment.QueryRepository;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ZMM_Table extends TTable {
    public ZMM_Table( String destinationTable) {
        super( destinationTable);
    }

    @Override
    public String getInsertQueryStr(StringBuilder fieldsStr, StringBuilder valuesStr) {
        return QueryRepository.getZMMInsertQuery().replace("@values@",valuesStr).replace("@fields@",fieldsStr);
    }

    public String getDeleteLineQuery(String[] keys){
        return QueryRepository.getZMMDeleteQuery().replace("@potrebnost_pen@",keys[0]).replace("@pozitsiya_potrebnosti_pen@",keys[1]);
    }

    @Override
    public String getKeyExpression() {
        return "LTRIM(STR(potrebnost_pen)) + '_' + LTRIM(RTRIM(STR(pozitsiya_potrebnosti_pen))) AS idn";
    }

    @Override
    protected String getUpdateQueryFromTable(ResultSet resultSet) throws SQLException {
        String potrebnost_pen=resultSet.getString("potrebnost_pen");
        int pozitsiya_potrebnosti_pen=resultSet.getInt("pozitsiya_potrebnosti_pen");
        return QueryRepository.getZMMUpdateQuery().replace("@potrebnost_pen@",potrebnost_pen).replace("@pozitsiya_potrebnosti_pen@",Integer.valueOf(pozitsiya_potrebnosti_pen).toString());
    }

    @Override
    public String[] getKeys(String idn) {
        return idn.split("_");
    }

    @Override
    public String getKeyName() {
        return "idn";
    }
}
