package xyz.krsh.insecuresite.security.ESAPI;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;

public class ESAPIValidatorWrapper {
    private Validator validator = ESAPI.validator();

    public Validator getValidator() {
        return validator;
    }

}
