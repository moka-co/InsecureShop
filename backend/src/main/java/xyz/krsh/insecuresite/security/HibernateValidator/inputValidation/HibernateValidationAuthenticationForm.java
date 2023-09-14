package xyz.krsh.insecuresite.security.HibernateValidator.inputValidation;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import xyz.krsh.insecuresite.rest.entities.User;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class HibernateValidationAuthenticationForm {

    private static final Logger logger = LogManager.getLogger();

    private Validator instance;

    public HibernateValidationAuthenticationForm() {
        super();
        this.instance = Validation.buildDefaultValidatorFactory().getValidator();

    }

    public boolean testAuthenticationForm(Map<String, String[]> requestParameterMap) throws IOException {
        Map<String, String> formDataMap = new HttpBodyParser().convertHttpBodyToMap(requestParameterMap);
        if (formDataMap.get("username") != null && formDataMap.get("password") != null) { // then it's a Form

            String username = formDataMap.get("username");
            Set<ConstraintViolation<User>> constraintViolations = instance.validateValue(User.class, "id", username);
            constraintViolations.stream().forEach(cv -> {
                logger.warn("Invalid input for class: " + cv.getRootBeanClass() + " - Invalid value "
                        + cv.getInvalidValue() + " triggered error message: " + cv.getMessage());
            });
            if (constraintViolations.size() > 0) {
                return false;
            }
        }
        return true;
    }

}
