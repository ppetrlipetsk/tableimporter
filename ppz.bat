set sourcetable=ppz_import
set destinationtable=ppz
set applog=ppz_applog.log
set errorlog=ppz_errorlog.log
set tableclass=PPS_Table
set wpath=C:\Users\96-paliy\IdeaProjects\tmc_system\out\artifacts\tmc_system_jar

java -jar target\table_importer-2.0.0-jar-with-dependencies.jar sourcetable=%sourcetable% destinationtable=%destinationtable% applog=%applog% errorlog=%errorlog% tableclass=%tableclass%
pause