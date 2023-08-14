package xyz.krsh.insecuresite.security.inputValidation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpBodyParser {

    public Map<String, String> convertHttpBodyToMap(Map<String, String[]> parameterMap) throws IOException {
        Map<String, String> formDataMap = new HashMap<String, String>();

        for (Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] paramValues = entry.getValue();
            if (paramValues.length == 1) {
                formDataMap.put(entry.getKey(), paramValues[0]);
            }
        }

        return formDataMap;
    }

}
