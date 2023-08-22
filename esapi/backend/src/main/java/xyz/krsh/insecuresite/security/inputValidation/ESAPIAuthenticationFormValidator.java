package xyz.krsh.insecuresite.security.inputValidation;

import java.io.IOException;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;

public class ESAPIAuthenticationFormValidator {

    private Validator instance;

    public ESAPIAuthenticationFormValidator() {
        super();
        this.instance = ESAPI.validator();

    }

    public void testAuthenticationForm(Map<String, String[]> requestParameterMap) throws IOException {
        Map<String, String> formDataMap = new HttpBodyParser().convertHttpBodyToMap(requestParameterMap);
        if (formDataMap.get("username") != null && formDataMap.get("password") != null) {
            // then it's an Authentication Form Map
            this.testIsValidEmail(formDataMap.get("username"));
            this.testIsValidPassword(formDataMap.get("password"));
        }
    }

    public void testIsValidEmail(String email) {
        if (instance.isValidInput("authentication", email, "Email", 100, false) == false) {
            System.out.println("Invalid email!");
        } else {
            System.out.println("Email is ok");
        }
    }

    public void testIsValidPassword(String password) {
        if (instance.isValidInput("authentication", password, "Password", 20, false) == false) {
            System.out.println("Invalid password");
        }

    }

}
