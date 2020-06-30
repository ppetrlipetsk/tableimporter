package envinronment;

import java.util.Map;

public class QueryRepository {

    public static String getAliasesQuery() {
        return "select * from aliases where table_id=(select id from tables where tablename='@tablename@')";
    }

    public static String getZMMDifferenceView(){
        return "SELECT [idn]  FROM [dogc].[dbo].[zmm_difference_view]";
        //return "SELECT * FROM [dogc].[dbo].[tables]";
    }

    public static String getZMMAddedLines(){
        return "  SELECT [idn] FROM [dogc].[dbo].[zmm_difference_view] " +
                "    except " +
                " SELECT [idn] FROM [dogc].[dbo].[zmm_idn_view]";
    }

    public static String getZMMDeletedLines() {
        return "select idn, deleted from ( SELECT * FROM zmm_idn_view EXCEPT" +
                " SELECT  * FROM zmm_import_idn_view ) i1 " +
                " where  (deleted is null or deleted=0) and idn not in (@dataset@)";
    }

    public static String getZMMImportDifRecords(){
        return "select * from [dogc].[dbo].zmm_import_idn_view where idn in (@range@)";
    }

    public static String getDestinationImportDifferenceRecords(){
        return "select * from (%import_idn_view%) i1 where idn in (%range%)";
    }

    public static String getZMMDeleteQuery(){
        return "update [dogc].[dbo].zmm set deleted=1 where potrebnost_pen='@potrebnost_pen@' and pozitsiya_potrebnosti_pen=@pozitsiya_potrebnosti_pen@";
    }

    public static String getZMMUpdateQuery(){
        return "UPDATE [dbo].[ZMM]" +
                "   SET @values@" +
                " WHERE potrebnost_pen='@potrebnost_pen@' and pozitsiya_potrebnosti_pen=@pozitsiya_potrebnosti_pen@";
    }

    public static String getZMMInsertQuery() {
        return "INSERT INTO [dbo].[ZMM] (@fields@) values (@values@)";
    }

// ================== PPS BLOCK =========================================

    public static String getPPSDifferenceView(){
        return "SELECT [idn]  FROM [dogc].[dbo].[ppz_difference_view]";
    }

    public static String getPPSAddedLines(){
        return "  SELECT [idn] FROM [dogc].[dbo].[ppz_difference_view] " +
                "    except " +
                " SELECT [idn] FROM [dogc].[dbo].[ppz_idn_view]";
    }

    public static String getPPSDeletedLines() {
        return "select idn, deleted from ( SELECT * FROM ppz_idn_view"+
                " EXCEPT"+
                " SELECT  * FROM ppz_import_idn_view ) i1 "+
                " where @dataset@ (deleted is null or deleted=0)";
    }

    public static String getPPSImportDifRecords(){
        return "select * from [dogc].[dbo].ppz_import_idn_view where idn in (@range@)";
    }

    public static String getPPSDeleteQuery(){
        return "update [dogc].[dbo].ppz set deleted=1 where idn='@idn@'";
    }

    public static String getPPSUpdateQuery(){
        return "UPDATE [dbo].[ppz]" +
                "   SET @values@" +
                " WHERE idn='@idn@'";
    }

    public static String getPPSInsertQuery() {
        return "INSERT INTO [dbo].[ppz] (@fields@) values (@values@)";
    }

    public static String getDeletedRecordsDetectQuery(){
        return "select idn, deleted from ( %destination_idn_view% i2 \n EXCEPT \n" +
                " %source_idn_view% i1) i3  \n" +
                " where  (deleted is null or deleted=0) and idn not in (%dataset%)";
    }

    public static String getAddedRecordsQuery(){
        return "SELECT [idn] FROM (%difference_view%) dv" +
                " except" +
                " SELECT [idn] FROM (%idn_view%) iv";
    }

    public static String getTableFieldsQuery(String table){
        return "SELECT  c.name as fname FROM    syscolumns c\n" +
                "INNER JOIN systypes t ON c.xtype = t.xtype and c.usertype = t.usertype\n" +
                "                WHERE   c.id = OBJECT_ID('"+table+"') order by fname";
    }
/*
    public static String getChangedFieldsQuery(String fieldsList, String fieldsList2, String tableOne, String tableTwo){
        return "SELECT        *\n" +
                "FROM            \n(SELECT "+
                fieldsList+
                " FROM "+tableOne+
                ")\n i1 EXCEPT \n" +
                " SELECT *  FROM  (\nSELECT "+fieldsList2+ " \nFROM "+tableTwo+") i2";
    }
*/

    public static String getIdnDifferenceViewQuery(String idnview_from, String idnview_to){
        return ("SELECT        idn FROM (%idnview_from%\n EXCEPT \n" +
                " %idnview_to%) i1").replace("%idnview_from%",idnview_from).replace("%idnview_to%",idnview_to);
    }


}
