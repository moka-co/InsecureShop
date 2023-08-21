package xyz.krsh.insecuresite.rest.service;

import xyz.krsh.insecuresite.security.inputValidation.HibernateValidationAuthenticationForm;

public class LoginValidatorService {

    HibernateValidationAuthenticationForm validator;

    public LoginValidatorService() {
        validator = new HibernateValidationAuthenticationForm();
    }

}
