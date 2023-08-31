package xyz.krsh.insecuresite.rest.service.documents;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;
import xyz.krsh.insecuresite.rest.repository.mongodb.ValidationRuleRepository;

@Service
public class ESAPIValidatorService {
    protected static final Logger logger = LogManager.getLogger();

    public Validator getValidator() {
        return ESAPI.validator();
    }

    public StringValidationRule getCookieValidationRule(ValidationRuleRepository validationRuleRepository) {
        try {
            Optional<ValidationRuleDocument> result = validationRuleRepository.findById("jsessionid");
            ValidationRuleDocument doc = result.get();
            String typename = "CookieValidationRule";
            String whitelist = doc.getRule(); // "^[A-Za-z0-9-_]{10,50}$";
            return new StringValidationRule(typename, ESAPI.encoder(), whitelist);

        } catch (Exception e) {
            logger.error(e);
            return null;
        }

    }

}
