package tableslib;

import businesslib.ImportProcessor;
import com.ppsdevelopment.jdbcprocessor.DataBaseConnector;
import com.ppsdevelopment.jdbcprocessor.DataBaseProcessor;
import envinronment.QueryRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TableHelper {

    /**
     *Возвращает строку, содержащую набор полей таблицы БД, разделенных запятой
     */
    public static String getTableFieldsLine(LinkedList<String> fields, String idExpression){
        boolean deleted=false;
        if (fields.contains("deleted")) {
            deleted=true;
            int i=fields.indexOf("deleted");
            fields.remove(i);
        }
        String s1= Arrays.toString(fields.toArray()).replace(" ","").replace("[","").replace("]","");
        if (deleted) s1+=", deleted";
        if (!fields.contains(idExpression))
            s1=idExpression+","+s1;
        return s1;
    }

    public static String generateChangedQuery(String tableOne, String tableTwo,String removeId, String keyExpression) throws SQLException {
//        String fieldsList= addDeletedField(TableHelper.getTableFieldsLine(getTableFields(tableOne,removeId),keyExpression));
//        String fieldsList2=addDeletedField(TableHelper.getTableFieldsLine(getTableFields(tableTwo,removeId),keyExpression));
        String view_from=TableHelper.getIdnViewQuery(tableOne,removeId,keyExpression);
        String view_to=TableHelper.getIdnViewQuery(tableTwo,removeId,keyExpression);
        return QueryRepository.getIdnDifferenceViewQuery(view_from,view_to);
    }

    public static String addDeletedField(String tableFieldsLine) {
        if (!tableFieldsLine.contains("deleted")) tableFieldsLine+=", 0 as deleted";
        return tableFieldsLine;
    }

    public static LinkedList<String> getTableFields(String table, String key) throws SQLException {
        String query=QueryRepository.getTableFieldsQuery(table);
        DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());
        ResultSet resultSet=dp.query(query);
        LinkedList<String> list=new LinkedList<>();
        if (resultSet!=null){
            while (resultSet.next()) {
                String fname= resultSet.getString("fname");
                if (!key.equals(fname))
                    list.add(fname);
            }
        }
        return list;
    }
    public static String getAddedRecordsQuery(String sourceTable, String destinationTable, String removedField, String idExpression, String id) throws Exception {
        String query=QueryRepository.getAddedRecordsQuery();
        String dv=TableHelper.generateChangedQuery(sourceTable,destinationTable,removedField, idExpression);
        String iv=getIdnViewQuery(destinationTable,removedField, idExpression);
        return query.replace("%difference_view%",dv).replace("%idn_view%",iv);
    }

    public static String getIdnViewQuery(String tableName, String removedField, String idExpression) throws SQLException {
        String query="select %fields% from "+tableName;
        String fields=TableHelper.addDeletedField(getTableFieldsLine(getTableFields(tableName,removedField),idExpression));;
        return query.replace("%fields%",fields);
    }


    /**
     * Возвражает строку ключей, записей, попавших в выборку разницы таблицы импорта и действующей таблицы
     * @return
     */
    public static String getDiffValuesStr(HashMap<String, ImportProcessor.FieldStateType> changedRecords, String quotes) {
        StringBuilder line=new StringBuilder();
        for (Map.Entry<String, ImportProcessor.FieldStateType> entry : changedRecords.entrySet()) {
            if (line.length()>0) line.append(",");
            String key=entry.getKey();
            line.append("'").append(key).append("'");
        }
        if (line.length()==0) line.append("NULL");
        return line.toString();
    }


}
