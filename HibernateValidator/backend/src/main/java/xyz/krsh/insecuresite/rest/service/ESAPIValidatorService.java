package xyz.krsh.insecuresite.rest.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.Validator;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;
import xyz.krsh.insecuresite.rest.repository.mongodb.ValidationRuleRepository;

@Service
public class ESAPIValidatorService {
    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    private ValidationRuleRepository testRepository;

    private Encoder encoder = ESAPI.encoder();
    private Validator validator = ESAPI.validator();

    public Validator getValidator() {
        return validator;
    }

    public StringValidationRule getCookieValidationRule() {
        if (testRepository == null) {
            logger.warn("Test repository null");
        }

        try {
            Optional<ValidationRuleDocument> result = testRepository.findById("jsessionid");
            ValidationRuleDocument doc = result.get();
            String typename = "CookieValidationRule";
            String whitelist = doc.getRule(); // "^[A-Za-z0-9-_]{10,50}$";
            return new StringValidationRule(typename, encoder, whitelist);

        } catch (Exception e) {
            logger.error(e);
            return null;
        }

    }

}
