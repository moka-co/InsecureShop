package xyz.krsh.insecuresite.security.ESAPI.inputValidation;

import java.io.IOException;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;

import xyz.krsh.insecuresite.security.HibernateValidator.inputValidation.HttpBodyParser;

public class ESAPIAuthenticationFormValidator {

    private Validator instance;

    public ESAPIAuthenticationFormValidator() {
        super();
        this.instance = ESAPI.validator();

    }

    public boolean testAuthenticationForm(Map<String, String[]> requestParameterMap) throws IOException {
        Map<String, String> formDataMap = new HttpBodyParser().convertHttpBodyToMap(requestParameterMap);
        if (formDataMap.get("username") != null && formDataMap.get("password") != null) {

            return instance.isValidInput("authentication", formDataMap.get("username"), "Email", 100, false)
                    && instance.isValidInput("authentication", formDataMap.get("password"), "Password", 20, false);
        }

        // Either username or password is missing, therefore return false
        return false;
    }
}