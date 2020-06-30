/*

Программа предназначена для импорта одной таблицы в другую.

 */

import businesslib.ImportProcessor;
import com.ppsdevelopment.jdbcprocessor.DataBaseConnector;
import com.ppsdevelopment.loglib.Logger;
import com.ppsdevelopment.programparameters.ProgramParameters;
import com.ppsdevelopment.tmcprocessor.tmctypeslib.FieldType;
import envinronment.*;
import tableslib.TTable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class MainClass {
    public static void main(String[] args) {
        ImportProcessor importProcessor=new ImportProcessor();
        try {
            if (!ApplicationInitializer.initApplication(args)) {
                System.out.println("Инициализация пограммы прошла с ошибкой!");
                ProgramMesssages.showAppParams();
            } else {
                Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(), "Начало работы программы импорта: " + new Date().toString(), true);
                ProgramMesssages.putProgramParamsToLog();
                HashMap<String, FieldType> aliases;
                HashMap<String, ImportProcessor.FieldStateType> changedRecords;
                LinkedList<String> deletedRecords;
                TTable table=createTableInstance(ProgramParameters.getParameterValue("tableclass"));
                if (table!=null) {
                    try {

                        aliases=importProcessor.getAliases(table.getDestinationTable());

                        table.setAliases(aliases);

                        //changedRecords=importProcessor.getChangedRecords(table);
                        changedRecords=importProcessor.getChangedRecords(ProgramParameters.getParameterValue("sourcetable"), ProgramParameters.getParameterValue("destinationtable"),"id","LTRIM(STR(potrebnost_pen)) + '_' + LTRIM(RTRIM(STR(pozitsiya_potrebnosti_pen))) AS idn", "idn");

                        importProcessor.detectAddedRecords(changedRecords,ProgramParameters.getParameterValue("sourcetable"), ProgramParameters.getParameterValue("destinationtable"),"id","LTRIM(STR(potrebnost_pen)) + '_' + LTRIM(RTRIM(STR(pozitsiya_potrebnosti_pen))) AS idn", "idn");

                        deletedRecords=importProcessor.detectDeletedRecords(ProgramParameters.getParameterValue("sourcetable"), ProgramParameters.getParameterValue("destinationtable"),"id","LTRIM(STR(potrebnost_pen)) + '_' + LTRIM(RTRIM(STR(pozitsiya_potrebnosti_pen))) AS idn", "idn",changedRecords);

                        // MessagesClass.importProcessMessageBegin();

                        int[] counts=importProcessor.changeRecords(changedRecords, table,ProgramParameters.getParameterValue("sourcetable"),"id","LTRIM(STR(potrebnost_pen)) + '_' + LTRIM(RTRIM(STR(pozitsiya_potrebnosti_pen))) AS idn");

                        int delCount=importProcessor.delRecords(deletedRecords,table);

                        Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(), "Изменено:" + counts[1]+", добавлено:" +counts[0]+", удалено:"+delCount+" записей. \n Импорт завершен успешно.", true);
                        Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(), "Время завершения:" + new Date().toString(), true);

                        //MessagesClass.importProcessMessageEnd();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    }
                }

        } catch (Exception e) {
            Logger.putLineToLog(ApplicationGlobals.getERRORLOGName(), "Импорт завершен с ошибками.\nСообщение об ошибке:" + e.toString(), true);
        }
        finally {
            try {
                DataBaseConnector.close();
                Logger.closeAll();
            } catch (SQLException e) {
                System.out.println("Ошибка закрытия соединения с БД. Сообщение об ошибке:" + e.toString());
            }
            catch (IOException e){
                System.out.println("Ошибка закрытия файлов журналов. Сообщение об ошибке:"+e.toString());
            }
        }

    }

    static TTable createTableInstance(String className) throws Exception {
        Class<TTable> tableClass;
        TTable table;
        if ((className==null)||(className.length()==0))
            throw new Exception("Ошибка создания класса "+className);
        Class[] params={String.class};
        try {
            tableClass= (Class<TTable>) Class.forName("tableslib."+className);
            table = tableClass.getConstructor(params).newInstance(ProgramParameters.getParameterValue("destinationtable"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e){
            throw new Exception("Ошибка создания класса "+className);
        }
        return table;
    }

}
