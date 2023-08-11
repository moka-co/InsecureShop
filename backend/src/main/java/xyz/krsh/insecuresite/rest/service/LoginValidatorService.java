package xyz.krsh.insecuresite.rest.service;

import xyz.krsh.insecuresite.security.inputValidation.TestESAPIValidator;

public class LoginValidatorService {

    TestESAPIValidator validator;

    public LoginValidatorService() {
        validator = new TestESAPIValidator();
    }

}
