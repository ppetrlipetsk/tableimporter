package envinronment;

import com.ppsdevelopment.loglib.Logger;
import com.ppsdevelopment.programparameters.ProgramParameters;

public class ProgramMesssages {
    private static void appLog(String message){
        Logger.putLineToLog(ApplicationGlobals.getAPPLOGName(),message,true);
    }

    public static void putProgramParamsToLog() {
        appLog("Задание на импорт данных в БД.");
        appLog("Имя таблицы импорта:"+ProgramParameters.getParameterValue("sourcetable"));
        appLog("Имя основной таблицы:"+ProgramParameters.getParameterValue("destinationtable"));
        appLog("Имя файла журнала программы:"+ProgramParameters.getParameterValue("applog"));
        appLog("Имя файла журнала ошибок:"+ProgramParameters.getParameterValue("errorlog"));
        appLog("Класс-обработчик:"+ProgramParameters.getParameterValue("tableclass"));
    }

    public static void showAppParams() {
        String text="Программа импорта измененной таблицы БД в основную.\n\n" +
                "sourcetable=[source table name] destinationtable=[destination table name] applog=[applog_file_name] errorlog=[errorlog_file_name] tableclass=[tableclass]\n\n" +
                "Значения параметров приложения:\n" +
                "\nsourcetable-имя таблицы с импортируемыми данными \n" +
                "destinationtable= имя основной таблицы \n" +
                "applog=имя файла журнала приложения \n" +
                "errorlog=имя файла журнала ошибок приложения \n" +
                "tableclass=класс, обслуживающий таблицу. (PPS_Table или ZMM_Table)\n";
        System.out.println(text);
    }
}
