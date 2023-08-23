package xyz.krsh.insecuresite.security.inputValidation;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import xyz.krsh.insecuresite.rest.entities.User;

public class HibernateValidationAuthenticationForm {

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
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<User> cv : constraintViolations) {
                    System.out.println(
                            "Invalid input for class: " + cv.getRootBeanClass());
                    System.out.println(
                            "Invalid value: " + cv.getInvalidValue() + " triggered error message: " + cv.getMessage());

                }
                return false;
            }

        }
        return true;
    }

}
