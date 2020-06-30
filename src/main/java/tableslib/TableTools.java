package tableslib;

import com.ppsdevelopment.tmcprocessor.tmctypeslib.FieldType;
import com.ppsdevelopment.tmcprocessor.tmctypeslib.FieldTypeDefines;

public class TableTools {
    public static FieldType detectFieldType(String fieldtype) {
        return FieldType.valueOf(fieldtype);
    }

    private String getFieldValueStr(FieldType fieldType, String valueStr ){
        String mask= FieldTypeDefines.getTypesFieldDBMask().get(fieldType);
        if (valueStr==null)
            return "null";
        else
            return mask.replace("@value@", valueStr);
    }


}
