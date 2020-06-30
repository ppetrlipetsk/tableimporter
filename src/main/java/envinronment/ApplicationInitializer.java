package envinronment;

import com.ppsdevelopment.configlib.ConfigReader;
import com.ppsdevelopment.jdbcprocessor.DataBaseConnector;
import com.ppsdevelopment.loglib.Logger;
import com.ppsdevelopment.programparameters.ProgramParameter;
import com.ppsdevelopment.programparameters.ProgramParameters;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class ApplicationInitializer {

    public static boolean initApplication(String[] args) throws Exception {
        initProgramParametersValues();
        if (!importProgramParameters(args)) return false;
        if (!importConfigParameters()) return false;
        Logger.createLoggers(new String[]{ApplicationGlobals.getAPPLOGName(),ApplicationGlobals.getERRORLOGName()}, ApplicationGlobals.getLINESLIMIT());
        dataBaseConnection();
        return true;
    }

    private static boolean importConfigParameters() {
        if (!ConfigReader.readConfig("config.ini")) return false;
        ApplicationGlobals.setDatabaseName(ConfigReader.getPropertyValue("databaseName"));
        ApplicationGlobals.setInstanceName(ConfigReader.getPropertyValue("instanceName"));
        ApplicationGlobals.setDBPassword(ConfigReader.getPropertyValue("password"));
        ApplicationGlobals.setUserName(ConfigReader.getPropertyValue("userName"));
        return true;
    }

    private static boolean importProgramParameters(String[] args) {
        return (args.length != 0) && (parseProgramParameters(args));
    }

    private static boolean parseProgramParameters(String[] args) {
        for( String arg:args){
            String[] par=arg.split("=");
            if ((par.length!=2)||!ProgramParameters.parameterExists(par[0])) {
                System.out.println("Ошибка параметра "+par[0]);
                return false;
            }
            ProgramParameters.setParameterValue(par[0],par[1]);
        }

        Iterator<Map.Entry<String, ProgramParameter>> entries = ProgramParameters.getIterator();
        while (entries.hasNext()) {
            Map.Entry<String, ProgramParameter> entry = entries.next();
            ProgramParameter p=entry.getValue();
            String s=p.getValue();
            if ((s.length()==0)&&(p.isRequire())) {
                System.out.println("Ошибка параметра "+entry.getKey());
                return false;
            }
        }
        return true;
    }

    private static void dataBaseConnection() throws Exception {
        try {
            DataBaseConnector.connectDataBase(ApplicationGlobals.getConnectionUrl(),ApplicationGlobals.getDBUserName(),ApplicationGlobals.getDbPassword(),ApplicationGlobals.getDBInstanceName(),ApplicationGlobals.getDatabaseName());
        } catch (SQLException|ClassNotFoundException e) {
          throw new Exception("Ошибка подключения к БД..."+ApplicationGlobals.getDBInstanceName()+" Сообщение об ошибке:"+e.toString());
        }
    }

    private static void initProgramParametersValues(){
        ProgramParameters.setParameterProperties("sourcetable","",true);
        ProgramParameters.setParameterProperties("destinationtable","",true);
        ProgramParameters.setParameterProperties("applog","",true);
        ProgramParameters.setParameterProperties("errorlog","false",false);
        ProgramParameters.setParameterProperties("tableclass","false",false);
    }

}
