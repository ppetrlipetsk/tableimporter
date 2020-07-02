set sourcetable=zmm_import
set destinationtable=zmm
set applog=zmm_applog.log
set errorlog=zmm_errorlog.log
set tableclass=ZMM_Table
set wpath=C:\Users\96-paliy\IdeaProjects\tmc_system\out\artifacts\tmc_system_jar


java -jar target\table_importer-2.0.0-jar-with-dependencies.jar
rem  sourcetable=%sourcetable% destinationtable=%destinationtable% applog=%applog% errorlog=%errorlog% tableclass=%tableclass%
pause