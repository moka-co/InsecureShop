package xyz.krsh.insecuresite.rest.service;

import xyz.krsh.insecuresite.security.inputValidation.ESAPIAuthenticationFormValidator;

public class LoginValidatorService {

    ESAPIAuthenticationFormValidator validator;

    public LoginValidatorService() {
        validator = new ESAPIAuthenticationFormValidator();
    }

}
