package xyz.krsh.insecuresite.security.inputValidation;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;

public class TestESAPIValidator {

    private Validator instance;

    public TestESAPIValidator() {
        super();
        this.instance = ESAPI.validator();

    }

    public void testIsValidEmail(String email) {
        System.out.println("Received " + email);

        if (instance.isValidInput("test", email, "Email", 100, false) == false) {
            System.out.println("Invalid email!!");

        } else {
            System.out.println("Email is ok");
        }
    }

}
