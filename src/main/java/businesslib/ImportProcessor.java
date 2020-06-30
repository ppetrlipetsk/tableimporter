package businesslib;

import com.ppsdevelopment.jdbcprocessor.DataBaseConnector;
import com.ppsdevelopment.jdbcprocessor.DataBaseProcessor;
import com.ppsdevelopment.tmcprocessor.tmctypeslib.FieldType;
import envinronment.QueryRepository;
import tableslib.TTable;
import tableslib.TableHelper;
import tableslib.TableTools;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

public class ImportProcessor {
    protected final String QUOTES_SYMBOL="'";


    public  HashMap<String, FieldType> getAliases(String destinationTable) throws SQLException {
        String query=QueryRepository.getAliasesQuery().replace("@tablename@",destinationTable);
        HashMap<String, FieldType> aliases=new HashMap<>();
        try(DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection())) {
            ResultSet resultSet=dp.query(query);
            if ((resultSet!=null)) {
                while (resultSet.next()) {
                    String fieldalias = resultSet.getString("fieldalias");
                    FieldType fieldType = TableTools.detectFieldType(resultSet.getString("fieldtype"));
                    aliases.put(fieldalias, fieldType);
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Ошибка чтения псевдонимов полей.\n"+e.getMessage()+"\n QUERY="+query);
        }
        return aliases;
    }
//TODO сделать проверку на существование таблиц и на соответствие полей.
    public HashMap<String, FieldStateType> getChangedRecords(String sourceTable, String destinationTable, String removedId, String keyExpression, String idn) throws Exception {
        String query=TableHelper.generateChangedQuery(sourceTable,destinationTable,removedId, keyExpression);
        HashMap<String, FieldStateType> changedRecords=new HashMap<>();
        try(DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());) {
            ResultSet resultSet=dp.query(query);
            if ((resultSet!=null)){
                while (resultSet.next()) {
                    changedRecords.put(resultSet.getString(idn),FieldStateType.UPDATE);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Ошибка чтения представления, содержащего измененные записи."+e.getMessage()+". QUERY="+query);
        }
        return changedRecords;
    }

    public  HashMap<String,FieldStateType> detectAddedRecords(HashMap<String, FieldStateType> changedRecords, String sourceTable, String DestinationTable, String removedField, String idExpression, String id) throws Exception {
        String query=TableHelper.getAddedRecordsQuery( sourceTable,  DestinationTable,  removedField,  idExpression,  id);
        try (DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());){
            ResultSet resultSet=dp.query(query);
            if ((resultSet!=null)){
                    while (resultSet.next()) {
                        changedRecords.put(resultSet.getString("idn"),FieldStateType.INSERT);
                    }
            }
        } catch (SQLException e) {
            throw new Exception("Ошибка определения добавленных записей. Сообщение об ошибке:"+e.toString()+" \nQuery:"+query);
        }
        return changedRecords;
    }

    public LinkedList<String> detectDeletedRecords(String sourceTable, String destinationTable, String removedField, String idExpression, String id,HashMap<String,FieldStateType> changedRecords) throws Exception {
        String query=QueryRepository.getDeletedRecordsDetectQuery();
        String destinationIdnView=TableHelper.getIdnViewQuery(destinationTable,removedField,idExpression);
        String sourceIdnView=TableHelper.getIdnViewQuery(sourceTable,removedField,idExpression);
        query=query.replace("%source_idn_view%",sourceIdnView).replace("%destination_idn_view%",destinationIdnView).replace("%dataset%",TableHelper.getDiffValuesStr(changedRecords,QUOTES_SYMBOL));
        LinkedList<String> deletedLines=new LinkedList<>();
        try (DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());) {
            ResultSet resultSet = dp.query(query);
            if ((resultSet != null)) {
                while (resultSet.next()) {
                    deletedLines.add(resultSet.getString(id));
                }
            }
        }
        catch (Exception e){
            throw new Exception("Ошибка определения удаленных записей. Сообщение об ошибке:"+e.toString()+" Query:"+query);
        }
        return deletedLines;
    }


    public int[] changeRecords(HashMap<String, FieldStateType> changedRecords, TTable table, String sourcetable, String removedFields, String idExpression) throws Exception {
        //Выбираем только те записи, которые новые или измененные в таблице импорта
        //String query=table.getImportDifferenceRecordsQuery(getDiffValuesStr(changedRecords,QUOTES_SYMBOL));
        int addedCount=0;
        int changedCount=0;
        String query=QueryRepository.getDestinationImportDifferenceRecords();
        String view=TableHelper.getIdnViewQuery(sourcetable,removedFields,idExpression);
        query=query.replace("%import_idn_view%",view).replace("%range%",TableHelper.getDiffValuesStr(changedRecords,QUOTES_SYMBOL));
        DataBaseProcessor dp=new DataBaseProcessor(DataBaseConnector.getConnection());
        try {
            ResultSet resultSet=dp.query(query);
            if ((resultSet!=null)){
                    while (resultSet.next()) {
                        String idn= resultSet.getString("idn");
                        FieldStateType fieldType=changedRecords.get(idn);
                        if (FieldStateType.INSERT==fieldType){
                            table.insertRecord(resultSet);
                            addedCount++;
                        }
                        else{
                            if (FieldStateType.UPDATE==fieldType){
                                table.updateRecord(resultSet);
                                changedCount++;
                            }
                        }
                    }
                }
        }
        catch (SQLException e) {
            throw new Exception("Ошибка изменения записи таблицы БД. Сообщение об ошибке:"+e.toString()+" \n QUERY="+query);
        }
        return new int[]{addedCount,changedCount};
    }

    public int delRecords(LinkedList<String> deletedRecords, TTable table) throws Exception {
        int count=0;
        if (deletedRecords!=null) {
            for (String idn : deletedRecords) {
                String[] keys = table.getKeys(idn);
                if (table.deleteLine(keys)) count++;
            }
        }
        return count;
    }

    public enum FieldStateType{
        INSERT,UPDATE
    }

}
