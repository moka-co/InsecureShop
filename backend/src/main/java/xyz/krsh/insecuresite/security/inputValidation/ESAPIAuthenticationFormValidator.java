package xyz.krsh.insecuresite.security.inputValidation;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;

public class ESAPIAuthenticationFormValidator {

    private Validator instance;

    public ESAPIAuthenticationFormValidator() {
        super();
        this.instance = ESAPI.validator();

    }

    public void testIsValidEmail(String email) {
        if (instance.isValidInput("authentication", email, "Email", 100, false) == false) {
            System.out.println("Invalid email!!");
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
