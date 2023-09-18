package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

@Service
public class ESAPIValidatorService {
    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    private MongoTemplate mongoTemplate;

    private Encoder encoder = ESAPI.encoder();
    private Validator validator = ESAPI.validator();

    static final int MIN_DEFAULT = 0;

    static final int MAX_DEFAULT = 1024;

    public Validator getValidator() {
        return validator;
    }

    public boolean isValidCookie(String documentName, String input) {

        try {
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

    public boolean validateBean(Object bean, String documentKey) throws ValidationException {
        logger.info("Retrieving document from repository");
        MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection("validationRuleDocument");
        Document document = mongoCollection.find(new Document("_id", documentKey)).first();

        if (document == null) {
            throw new NullPointerException("Document not found");
        }

        if ((boolean) document.get("enabled") == false) {
            logger.info(documentKey + " is disabled!");
            return true;
        }

        Stream<Method> inputMethods = Arrays.stream(bean.getClass().getMethods())
                .filter(method -> method.getName().startsWith("get")
                        && method.getReturnType() != java.lang.Class.class);
        List<Method> listOfMethods = inputMethods.collect(Collectors.toList());

        for (Method method : listOfMethods) {
            String field = method.getName().substring(3, method.getName().length()).toLowerCase();
            try {
                Object value = method.invoke(bean);
                boolean result = validateRule(document, field, value);
                if (result == false) {
                    throw new ValidationException(
                            "Invalid input: please change " + field + " input value and retry ",
                            "Invalid input: " + value + " is not a valid input for " + field);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                return false;
            }
        }
        return true;
    }

    public boolean validateRule(Document document, String fieldName, Object input) throws ValidationException {
        boolean returnValue = false;
        logger.info("Entering validateRule method, validating " + fieldName + "with value " + input
                + " against document " + (Document) document.get(fieldName));

        if (document.containsKey(fieldName) == false) {
            throw new RuntimeException(
                    fieldName + " is not a document validation rule key, add it to the document");
        }

        Document ruleDocument = (Document) document.get(fieldName);
        if (!(boolean) ruleDocument.get("enabled")) {
            return true;
        }

        int min = ruleDocument.get("min") != null ? (int) ruleDocument.get("min") : MIN_DEFAULT;
        int max = ruleDocument.get("max") != null ? (int) ruleDocument.get("max") : MAX_DEFAULT;

        String typeName = input.getClass().getTypeName();

        if (typeName.equals("java.lang.String")) {
            String rule = (String) ruleDocument.get("rule");
            StringValidationRule stringValidationRule = new StringValidationRule(fieldName + "ValidationRule", encoder,
                    rule);
            stringValidationRule.setMaximumLength(max);
            stringValidationRule.setMinimumLength(min);

            returnValue = stringValidationRule.isValid("Check if input " + input + "is valid", input.toString());

        } else if (typeName.equals("java.lang.Integer")
                || typeName.equals("java.lang.Float")) {
            NumberValidationRule numberValidationRule = new NumberValidationRule(fieldName + "ValidationRule", encoder,
                    min, max);
            returnValue = numberValidationRule.isValid("Check if input " + input + " is valid ", input.toString());
        } else {
            logger.error("Unknown typename");
            return false;
        }

        return returnValue;

    }
}
