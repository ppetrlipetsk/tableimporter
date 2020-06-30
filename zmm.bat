set sourcetable=zmm_import
set destinationtable=zmm
set applog=zmm_applog.log
set errorlog=zmm_errorlog.log
set tableclass=ZMM_Table
set wpath=C:\Users\96-paliy\IdeaProjects\tmc_system\out\artifacts\tmc_system_jar


java -jar target\table_importer-1.0-SNAPSHOT-jar-with-dependencies.jar sourcetable=%sourcetable% destinationtable=%destinationtable% applog=%applog% errorlog=%errorlog% tableclass=%tableclass%
pause