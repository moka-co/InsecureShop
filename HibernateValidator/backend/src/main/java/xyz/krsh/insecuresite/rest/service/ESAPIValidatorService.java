package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.Validator;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.validation.NumberValidationRule;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;
import xyz.krsh.insecuresite.rest.repository.mongodb.ValidationRuleRepository;

@Service
public class ESAPIValidatorService {
    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ValidationRuleRepository testRepository;

    private Encoder encoder = ESAPI.encoder();
    private Validator validator = ESAPI.validator();

    static final int MIN_DEFAULT = 0;

    static final int MAX_DEFAULT = 1024;

    public Validator getValidator() {
        return validator;
    }

    public boolean isValidCookie(String documentName, String input) {

        try {
            System.out.println("Cookie input: " + input);
            MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection("validationRuleDocument");
            Document document = mongoCollection.find(new Document("_id", "jsessionid_v2")).first();

            if (document == null) {
                throw new NullPointerException("Document with name \"jsessionid_v2\" not found");
            }

            if ((boolean) document.get("enabled") == false) {
                return true;
            }

            String typename = "CookieValidationRule";
            String whitelist = (String) document.get("whitelist");

            if (whitelist == null) {
                throw new ValidationException("Internal server error",
                        "Invalid document type, whitelist for cookie not found");
            }

            StringValidationRule cookieValidationRule = new StringValidationRule(typename, encoder, whitelist);
            return cookieValidationRule.isValid("Validating  " + input + " cookie", input);
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    /*
     * Input:
     * - bean - the object to validate
     * - documentKey - the key of the document containing the validation rules
     * Output:
     * - true if bean is valid, else false or throw exceptions
     */
    public boolean validateBean(Object bean, String documentKey) {
        try {
            logger.info("Retrieving document from repository");
            MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection("validationRuleDocument");
            Document document = mongoCollection.find(new Document("_id", documentKey)).first();

            if ((boolean) document.get("enabled") == false) {
                return true;
            }

            Stream<Method> bordgameDtoMethods = Arrays.stream(BoardgameDto.class.getMethods())
                    .filter(method -> method.getName().startsWith("get")
                            && method.getReturnType() != java.lang.Class.class);

            bordgameDtoMethods.forEach(method -> {
                String methodName = method.getName();
                String field = methodName.substring(3, methodName.length()).toLowerCase();
                try {
                    Object value = method.invoke(bean);
                    if (validateRule(document, field, method.invoke(bean)) == false) {
                        throw new ValidationException(
                                "Invalid input: please change " + field + " input value and retry ",
                                "Invalid input: " + value + " is not a valid input for " + field);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error(e);
                    throw new RuntimeException("cannot access to value from method " + method);
                } catch (ValidationException validationException) {
                    logger.error(validationException.getMessage());
                }
            });

            return true;

        } catch (Exception e) {
            logger.error(e);
            return false;
        }

    }

    public boolean validateRule(Document document, String fieldName, Object input) {

        if (document.containsKey(fieldName) == false) {
            throw new RuntimeException(
                    fieldName + " is not a document validation rule key, add it to the document");
        }

        Document ruleDocument = (Document) document.get(fieldName);
        if (!(boolean) ruleDocument.get("enabled")) {
            return true;
        }

        logger.info("fieldName: " + fieldName + " -> " + ruleDocument.toString());

        int min = ruleDocument.get("min") != null ? (int) ruleDocument.get("min") : MIN_DEFAULT;
        int max = ruleDocument.get("max") != null ? (int) ruleDocument.get("max") : MAX_DEFAULT;

        String typeName = input.getClass().getTypeName();
        if (typeName.equals("String")) {
            String rule = (String) ruleDocument.get("rule");
            StringValidationRule stringValidationRule = new StringValidationRule(fieldName + "ValidationRule", encoder,
                    rule);
            stringValidationRule.setMaximumLength(max);
            stringValidationRule.setMinimumLength(min);
            return stringValidationRule.isValid("Check if input " + input + "is valid", input.toString());

        } else if (typeName.toLowerCase().equals("integer") || typeName.toLowerCase().equals("float")) {
            NumberValidationRule numberValidationRule = new NumberValidationRule(fieldName + "ValidationRule", encoder,
                    min, max);
            return numberValidationRule.isValid("Check if input " + input + " is valid ", input.toString());
        }

        logger.info(input + "  is valid input for " + ruleDocument.toString());
        return true;

    }
}
