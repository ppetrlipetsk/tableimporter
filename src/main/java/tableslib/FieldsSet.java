package tableslib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FieldsSet {
    private final LinkedHashMap<String, Integer> fields;

    public FieldsSet() {
        this.fields = new LinkedHashMap<>();
    }

    public void put(String key, Integer value) {
        fields.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        boolean equals=true;
        HashMap<String, Integer> map2= ((FieldsSet) o).getFields();//(HashMap<String,Integer>) o;
        if (map2.size()!=fields.size()) return false;
        for (Map.Entry<String, Integer> entry : fields.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (!(map2.containsKey(key) && map2.containsValue(value))){
                equals= false;
                break;
            }
        }
        return equals;
    }

    private LinkedHashMap<String, Integer> getFields() {
        return fields;
    }
}
