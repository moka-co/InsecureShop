package xyz.krsh.insecuresite.security.HibernateValidator.hibernateValidatorBootstrapping;

import java.util.Locale;

import javax.validation.MessageInterpolator;

public class MyMessageInterpolator implements MessageInterpolator {

    @Override
    public String interpolate(String messageTemplate, Context context) {
        StringBuilder stringBuilder = new StringBuilder(
                "Invalid input: " + context.getValidatedValue().toString() + " Violated Constraint: "
                        + messageTemplate);
        return stringBuilder.toString();
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        StringBuilder stringBuilder = new StringBuilder(
                "Invalid input: " + context.getValidatedValue().toString() + " Violated Constraint: "
                        + messageTemplate);
        return stringBuilder.toString();
    }

}
