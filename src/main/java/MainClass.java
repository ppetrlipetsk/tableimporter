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
        try {
            if (!ApplicationInitializer.initApplication(args)) {
                System.out.println("Инициализация пограммы прошла с ошибкой!");
                ProgramMesssages.showAppParams();
            } else {
                Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(), "Начало работы программы импорта: " + new Date().toString(), true);
                ProgramMesssages.putProgramParamsToLog();

                ImportProcessor importProcessor=new ImportProcessor();
                HashMap<String, FieldType> aliases;
                HashMap<String, ImportProcessor.FieldStateType> changedRecords;
                LinkedList<String> deletedRecords;
                TTable table=createTableInstance(ProgramParameters.getParameterValue("tableclass"));
                if (table!=null) {
                    try {

                        aliases=importProcessor.getAliases(table.getDestinationTable());

                        table.setAliases(aliases);

                        changedRecords=importProcessor.getChangedRecords(table);

                        importProcessor.detectAddedRecords(changedRecords,table);

                        deletedRecords=importProcessor.detectDeletedRecords(changedRecords,table);

                        // MessagesClass.importProcessMessageBegin();

                        importProcessor.changeRecords(changedRecords, table);

                        importProcessor.delRecords(deletedRecords,table);

                        //MessagesClass.importProcessMessageEnd();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    }
                }

                //Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(), "Импортировано:" + importProcessor.getRowCount() + " записей. \n Импорт завершен успешно.", true);
                Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(), "Время завершения:" + new Date().toString(), true);
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
