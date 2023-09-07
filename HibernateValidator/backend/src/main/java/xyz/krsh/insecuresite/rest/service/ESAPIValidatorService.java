package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.Field;
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
import org.owasp.esapi.reference.validation.NumberValidationRule;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
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

    boolean valid = false;

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

    /*
     * Retrieve boardgame validation document from MongoDB
     * and validate every single field against rules from database
     */
    public boolean validateBoardgame(BoardgameDto boardgame) {
        try {
            logger.info("Retrieving document from repository");
            MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection("validationRuleDocument");
            Document document = mongoCollection.find(new Document("_id", "boardgame")).first();

            // Check price
            Document priceMinmax = (Document) document.get("price");
            int min = (int) priceMinmax.get("min");
            int max = (int) priceMinmax.get("max");
            NumberValidationRule priceValidationRule = new NumberValidationRule("priceValidationRule", encoder, min,
                    max);
            if (priceValidationRule.isValid("validating boardgameDto price",
                    String.valueOf(boardgame.getPrice())) == false) {
                logger.warn("invalid price in " + boardgame.toString());
                return false;
            }

            // Check Quantity
            Document quantityMinmax = (Document) document.get("quantity");
            min = (int) quantityMinmax.get("min");
            max = (int) quantityMinmax.get("max");
            NumberValidationRule quantityValidationRule = new NumberValidationRule("quantityValidationRule", encoder,
                    min,
                    max);
            if (quantityValidationRule.isValid("validating boardgameDto quantity",
                    String.valueOf(boardgame.getQuantity())) == false) {
                logger.warn("invalid quantity in " + boardgame.toString());
                return false;
            }

            // Check description
            String rule = (String) document.get("description");
            StringValidationRule descriptionValidationRule = new StringValidationRule("descriptionValidationRule",
                    encoder, rule);
            descriptionValidationRule.setMaximumLength(1024);
            if (descriptionValidationRule.isValid("validating boardgameDto quantity",
                    boardgame.getDescription()) == false) {
                logger.warn("invalid description in " + boardgame.toString());
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error(e);
            return false;
        }

    }

    /*
     * Retrieve boardgame validation document from MongoDB
     * and validate every single field against rules from database
     */
    public boolean validateBoardgame2(BoardgameDto boardgame) {
        this.valid = true;

        try {
            logger.info("Retrieving document from repository");
            MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection("validationRuleDocument");
            Document document = mongoCollection.find(new Document("_id", "boardgame")).first();

            logger.info("Begin validation of " + boardgame.toString());
            Stream<Method> boardgameMethod = Arrays.stream(BoardgameDto.class.getMethods())
                    .filter(method -> method.getName().startsWith("get"));
            boardgameMethod.forEach(method -> {
                if (this.valid == false) { // valid is false, don't valid other methods
                    return;
                }
                logger.info("Currently validating method: " + method + " return type: " + method.getReturnType());

                if (method.getReturnType() == String.class) {
                    logger.info("Validating: " + method.getName() + " from " + boardgame.getClass().getSimpleName());
                    this.valid = valid && validateStringType(method, document, boardgame);

                } else if (method.getReturnType() == float.class || method.getReturnType() == int.class) {
                    logger.info("Validating: " + method.getName() + " from " + boardgame.getClass().getSimpleName());
                    this.valid = valid && validateNumberType(method, document, boardgame);
                }
            });

            return this.valid;

        } catch (Exception e) {
            logger.error(e);
            return false;
        }

    }

    public boolean validateStringType(Method method, Document document, Object input) {

        // Set validation rules
        String rule = (String) document.get("description");
        StringValidationRule stringValidationRule = new StringValidationRule(
                "descriptionValidationRule",
                encoder, rule);
        stringValidationRule.setMaximumLength(1024);

        // Check input against rules
        try {
            String value = (String) method.invoke(input);
            return stringValidationRule.isValid("Validating " + input.toString(), value);
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    public boolean validateNumberType(Method method, Document document, Object input) {

        // Set validation rules
        Document priceMinmax = (Document) document.get("price");
        int min = (int) priceMinmax.get("min");
        int max = (int) priceMinmax.get("max");
        NumberValidationRule numberValidationRule = new NumberValidationRule("numberValidationRule",
                encoder, min, max);

        // Check input against rules;
        try {
            String value = String.valueOf(method.invoke(input));
            return numberValidationRule.isValid("Validating + " + input.toString(), value);

        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

}
